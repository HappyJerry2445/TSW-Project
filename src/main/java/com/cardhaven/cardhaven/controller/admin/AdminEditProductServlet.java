package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.*;
import com.cardhaven.cardhaven.model.dto.*;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.sql.DataSource;

@WebServlet(name = "AdminEditProductServlet", value = "/admin/products/edit")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class AdminEditProductServlet extends HttpServlet {

    // Regex for validation
    private static final Pattern SKU_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9-]{3,50}$"
    );
    private static final Pattern TEXT_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s'.,-]{2,100}$"
    );

    private void validateAndRepopulate(
        HttpServletRequest request,
        List<String> errors,
        ProductDTO product
    ) {
        // General Product Info
        product.setProductName(request.getParameter("productName"));
        product.setSku(request.getParameter("sku"));
        product.setProductDescription(request.getParameter("description"));
        product.setActive(request.getParameter("isActive") != null);

        if (
            product.getProductName() == null ||
            product.getProductName().trim().isEmpty() ||
            product.getProductName().length() > 255
        ) {
            errors.add("Il nome del prodotto non è valido (1-255 caratteri).");
        }
        if (
            product.getSku() == null ||
            !SKU_PATTERN.matcher(product.getSku()).matches()
        ) {
            errors.add(
                "Lo SKU non è valido (3-50 caratteri, solo lettere, numeri e trattini)."
            );
        }
        if (
            product.getProductDescription() != null &&
            product.getProductDescription().length() > 2000
        ) {
            errors.add("La descrizione non può superare i 2000 caratteri.");
        }

        try {
            product.setBasePrice(
                new BigDecimal(request.getParameter("basePrice"))
            );
            if (product.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Il prezzo base non può essere negativo.");
            }
        } catch (NumberFormatException e) {
            errors.add("Formato prezzo base non valido.");
        }

        try {
            product.setCurrentPrice(
                new BigDecimal(request.getParameter("currentPrice"))
            );
            if (product.getCurrentPrice().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Il prezzo corrente non può essere negativo.");
            }
        } catch (NumberFormatException e) {
            errors.add("Formato prezzo corrente non valido.");
        }

        try {
            product.setStockQuantity(
                Integer.parseInt(request.getParameter("stockQuantity"))
            );
            if (product.getStockQuantity() < 0) {
                errors.add("La quantità in stock non può essere negativa.");
            }
        } catch (NumberFormatException e) {
            errors.add("Formato quantità non valido.");
        }
    }

    private void repopulateFormAndForward(
        HttpServletRequest request,
        HttpServletResponse response,
        List<String> errors,
        ProductDTO productToUpdate
    ) throws ServletException, IOException, SQLException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        request.setAttribute("errors", errors);

        try {
            // Fetch the original product just to get the unchangeable type and other data
            ProductDAO productDAO = new ProductDAO(ds);
            ProductDTO originalProduct = productDAO.getById(
                productToUpdate.getProductId()
            );
            if (originalProduct != null) {
                productToUpdate.setProductType(
                    originalProduct.getProductType()
                );
            }

            request.setAttribute("product", productToUpdate);

            // Repopulate type-specific fields with submitted data
            if (
                productToUpdate.getProductType() ==
                ProductDTO.ProductType.TradingCard
            ) {
                TradingCardDTO cardDetails = new TradingCardDTO();
                cardDetails.setCardSet(request.getParameter("cardSet"));
                cardDetails.setCardNumber(request.getParameter("cardNumber"));
                cardDetails.setRarity(request.getParameter("rarity"));
                cardDetails.setCardCondition(
                    request.getParameter("cardCondition")
                );
                request.setAttribute("cardDetails", cardDetails);
                request.setAttribute("repopulatedCard", cardDetails);
            } else if (
                productToUpdate.getProductType() ==
                ProductDTO.ProductType.Accessory
            ) {
                AccessoryDTO accessoryDetails = new AccessoryDTO();
                accessoryDetails.setAccessoryType(
                    request.getParameter("accessoryType")
                );
                accessoryDetails.setMaterial(request.getParameter("material"));
                accessoryDetails.setColor(request.getParameter("color"));
                request.setAttribute("accessoryDetails", accessoryDetails);
                request.setAttribute("repopulatedAccessory", accessoryDetails);
            }

            // Reload other necessary form data
            ProductImageDAO piDAO = new ProductImageDAO(ds);
            request.setAttribute(
                "productImages",
                piDAO.getAllByProductId(productToUpdate.getProductId())
            );
            CategoryDAO categoryDAO = new CategoryDAO(ds);
            request.setAttribute(
                "categories",
                categoryDAO.getAll("CategoryName")
            );
            String[] selectedCategories = request.getParameterValues(
                "categories"
            );
            if (selectedCategories != null) {
                List<Integer> selectedIds = Arrays.stream(selectedCategories)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
                request.setAttribute("selectedCategoryIds", selectedIds);
            }
        } catch (SQLException e) {
            errors.add(
                "Errore critico durante il ricaricamento del form: " +
                e.getMessage()
            );
        }

        request
            .getRequestDispatcher("/WEB-INF/views/admin/edit-product.jsp")
            .forward(request, response);
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        int productId;
        try {
            productId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "ID Prodotto non valido."
            );
            return;
        }

        try {
            ProductDAO productDAO = new ProductDAO(ds);
            ProductDTO product = productDAO.getById(productId);

            if (product == null) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Prodotto non trovato."
                );
                return;
            }
            request.setAttribute("product", product);

            // Carica dati specifici del tipo
            if (
                product.getProductType() == ProductDTO.ProductType.TradingCard
            ) {
                TradingCardDTO cardDetails = new TradingCardDAO(ds).getById(
                    productId
                );
                request.setAttribute("cardDetails", cardDetails);
                request.setAttribute("repopulatedCard", cardDetails);
            } else if (
                product.getProductType() == ProductDTO.ProductType.Accessory
            ) {
                AccessoryDTO accessoryDetails = new AccessoryDAO(ds).getById(
                    productId
                );
                request.setAttribute("accessoryDetails", accessoryDetails);
                request.setAttribute("repopulatedAccessory", accessoryDetails);
            }

            // Carica categorie e immagini
            CategoryDAO categoryDAO = new CategoryDAO(ds);
            ProductCategoryDAO pcDAO = new ProductCategoryDAO(ds);
            ProductImageDAO piDAO = new ProductImageDAO(ds);

            request.setAttribute(
                "categories",
                categoryDAO.getAll("CategoryName")
            );
            request.setAttribute(
                "productTypes",
                ProductDTO.ProductType.values()
            );
            request.setAttribute(
                "productImages",
                piDAO.getAllByProductId(productId)
            );

            // Fornisce una lista di ID per preselezionare le categorie nel form
            Collection<ProductCategoryDTO> existingCategories = pcDAO.getAll(
                null
            ); // Semplificato, da filtrare se necessario
            List<Integer> selectedCategoryIds = existingCategories
                .stream()
                .filter(pc -> pc.getProductId() == productId)
                .map(ProductCategoryDTO::getCategoryId)
                .collect(Collectors.toList());
            request.setAttribute("selectedCategoryIds", selectedCategoryIds);

            request
                .getRequestDispatcher("/WEB-INF/views/admin/edit-product.jsp")
                .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(
                "Errore del database durante il caricamento del prodotto.",
                e
            );
        }
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        List<String> errors = new ArrayList<>();
        int productId = 0;

        try {
            productId = Integer.parseInt(request.getParameter("productId"));
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "ID Prodotto non valido."
            );
            return;
        }

        // --- VALIDATION ---
        ProductDTO productToUpdate = new ProductDTO();
        productToUpdate.setProductId(productId); // Set ID for context
        validateAndRepopulate(request, errors, productToUpdate);

        if (!errors.isEmpty()) {
            try {
                repopulateFormAndForward(
                    request,
                    response,
                    errors,
                    productToUpdate
                );
            } catch (SQLException e) {
                throw new ServletException(
                    "Errore durante il repopolamento del form di modifica.",
                    e
                );
            }
            return;
        }

        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false); // Inizia transazione

            // DAOs
            ProductDAO productDAO = new ProductDAO(ds);
            ImageDAO imageDAO = new ImageDAO(ds);
            ProductImageDAO productImageDAO = new ProductImageDAO(ds);
            TradingCardDAO tradingCardDAO = new TradingCardDAO(ds);
            AccessoryDAO accessoryDAO = new AccessoryDAO(ds);
            ProductCategoryDAO productCategoryDAO = new ProductCategoryDAO(ds);

            // 1. Aggiorna il prodotto base
            ProductDTO product = productDAO.getById(productId);
            // Update with validated data
            product.setProductName(productToUpdate.getProductName());
            product.setSku(productToUpdate.getSku());
            product.setBasePrice(productToUpdate.getBasePrice());
            product.setCurrentPrice(productToUpdate.getCurrentPrice());
            product.setStockQuantity(productToUpdate.getStockQuantity());
            product.setActive(productToUpdate.isActive());
            product.setProductDescription(
                productToUpdate.getProductDescription()
            );
            productDAO.save(product);

            // 2. Aggiorna i dettagli specifici del tipo
            if (
                product.getProductType() == ProductDTO.ProductType.TradingCard
            ) {
                TradingCardDTO card = tradingCardDAO.getById(productId);
                card.setCardSet(request.getParameter("cardSet"));
                card.setCardNumber(request.getParameter("cardNumber"));
                card.setRarity(request.getParameter("rarity"));
                card.setCardCondition(request.getParameter("cardCondition"));
                tradingCardDAO.save(card);
            }
            if (product.getProductType() == ProductDTO.ProductType.Accessory) {
                AccessoryDTO accessory = accessoryDAO.getById(productId);
                accessory.setAccessoryType(
                    request.getParameter("accessoryType")
                );
                accessory.setMaterial(request.getParameter("material"));
                accessory.setColor(request.getParameter("color"));
                accessoryDAO.save(accessory);
            }

            // 3. Gestione immagini
            String imagesToDeleteParam = request.getParameter("imagesToDelete");
            if (imagesToDeleteParam != null && !imagesToDeleteParam.isEmpty()) {
                String[] imagesToDeleteIds = imagesToDeleteParam.split(",");
                for (String idStr : imagesToDeleteIds) {
                    if (!idStr.isEmpty()) {
                        productImageDAO.delete(Integer.parseInt(idStr));
                    }
                }
            }

            // Aggiungi le nuove
            Map<String, Integer> newImageIdMap = new HashMap<>();
            Collection<Part> newFileParts = request
                .getParts()
                .stream()
                .filter(
                    part ->
                        "newProductImages".equals(part.getName()) &&
                        part.getSize() > 0
                )
                .toList();

            for (Part filePart : newFileParts) {
                ImageDTO image = new ImageDTO();
                image.setMimeType(filePart.getContentType());
                image.setImageData(filePart.getInputStream().readAllBytes());
                imageDAO.save(image); // Salva e ottiene ID
                newImageIdMap.put(
                    filePart.getSubmittedFileName(),
                    image.getImageId()
                );
            }

            // Riordina tutto
            String imageOrderParam = request.getParameter("imageOrder");
            if (imageOrderParam != null && !imageOrderParam.isEmpty()) {
                String[] imageOrder = imageOrderParam.split(",");
                for (int i = 0; i < imageOrder.length; i++) {
                    String identifier = imageOrder[i];
                    if (identifier.startsWith("id:")) {
                        int productImageId = Integer.parseInt(
                            identifier.substring(3)
                        );
                        ProductImageDTO pi = productImageDAO.getById(
                            productImageId
                        );
                        if (pi != null) {
                            pi.setSortOrder(i);
                            productImageDAO.save(pi);
                        }
                    } else if (identifier.startsWith("new:")) {
                        String fileName = identifier.substring(4);
                        Integer newImageId = newImageIdMap.get(fileName);
                        if (newImageId != null) {
                            ProductImageDTO newPi = new ProductImageDTO(
                                0,
                                productId,
                                i,
                                newImageId
                            );
                            productImageDAO.save(newPi);
                        }
                    }
                }
            }

            // 4. Gestione Categorie
            int finalProductId = productId;
            Set<Integer> existingCategoryIds = productCategoryDAO
                .getAll(null)
                .stream()
                .filter(pc -> pc.getProductId() == finalProductId)
                .map(ProductCategoryDTO::getCategoryId)
                .collect(Collectors.toSet());
            String[] selectedCategoriesStr = request.getParameterValues(
                "categories"
            );
            Set<Integer> newCategoryIds = (selectedCategoriesStr == null)
                ? new HashSet<>()
                : Arrays.stream(selectedCategoriesStr)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());

            // Rimuovi quelle deselezionate
            for (int oldCatId : existingCategoryIds) {
                if (!newCategoryIds.contains(oldCatId)) {
                    productCategoryDAO.delete(
                        new ProductCategoryDTO.ProductCategoryKey(
                            productId,
                            oldCatId
                        )
                    );
                }
            }
            // Aggiungi quelle nuove
            for (int newCatId : newCategoryIds) {
                if (!existingCategoryIds.contains(newCatId)) {
                    productCategoryDAO.save(
                        new ProductCategoryDTO(productId, newCatId)
                    );
                }
            }

            conn.commit();
            NotificationUtil.sendNotification(
                request,
                "Prodotto aggiornato con successo!",
                "success"
            );
        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            // Instead of a simple notification, repopulate the form with errors
            errors.add("Errore durante l'aggiornamento: " + e.getMessage());
            try {
                // We need to build the object to repopulate from the request again
                ProductDTO productForRepopulation = new ProductDTO();
                productForRepopulation.setProductId(productId);
                validateAndRepopulate(request, errors, productForRepopulation);
                repopulateFormAndForward(
                    request,
                    response,
                    errors,
                    productForRepopulation
                );
            } catch (SQLException ex) {
                throw new ServletException(
                    "Errore critico durante la gestione di un errore di aggiornamento.",
                    ex
                );
            }
            return; // Important to stop further processing
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/products");
    }
}
