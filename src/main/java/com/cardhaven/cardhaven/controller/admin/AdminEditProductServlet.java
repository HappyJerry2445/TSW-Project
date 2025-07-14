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

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "AdminEditProductServlet", value = "/admin/products/edit")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 15)
public class AdminEditProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        int productId;
        try {
            productId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Prodotto non valido.");
            return;
        }

        try {
            ProductDAO productDAO = new ProductDAO(ds);
            ProductDTO product = productDAO.getById(productId);

            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato.");
                return;
            }
            request.setAttribute("product", product);

            // Carica dati specifici del tipo
            if (product.getProductType() == ProductDTO.ProductType.TradingCard) {
                request.setAttribute("cardDetails", new TradingCardDAO(ds).getById(productId));
            } else if (product.getProductType() == ProductDTO.ProductType.Accessory) {
                request.setAttribute("accessoryDetails", new AccessoryDAO(ds).getById(productId));
            }

            // Carica categorie e immagini
            CategoryDAO categoryDAO = new CategoryDAO(ds);
            ProductCategoryDAO pcDAO = new ProductCategoryDAO(ds);
            ProductImageDAO piDAO = new ProductImageDAO(ds);

            request.setAttribute("categories", categoryDAO.getAll("CategoryName"));
            request.setAttribute("productTypes", ProductDTO.ProductType.values());
            request.setAttribute("productImages", piDAO.getAllByProductId(productId));

            // Fornisce una lista di ID per preselezionare le categorie nel form
            Collection<ProductCategoryDTO> existingCategories = pcDAO.getAll(null); // Semplificato, da filtrare se necessario
            List<Integer> selectedCategoryIds = existingCategories.stream()
                    .filter(pc -> pc.getProductId() == productId)
                    .map(ProductCategoryDTO::getCategoryId)
                    .collect(Collectors.toList());
            request.setAttribute("selectedCategoryIds", selectedCategoryIds);

            request.getRequestDispatcher("/WEB-INF/views/admin/edit-product.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Errore del database durante il caricamento del prodotto.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        Connection conn = null;
        int productId = Integer.parseInt(request.getParameter("productId"));

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
            // ... (altri DAO come in doPost di AdminNewProductServlet)

            // 1. Aggiorna il prodotto base
            ProductDTO product = productDAO.getById(productId);

            product.setProductName(request.getParameter("productName"));
            product.setSku(request.getParameter("sku"));
            product.setBasePrice(new BigDecimal(request.getParameter("basePrice")));
            product.setCurrentPrice(new BigDecimal(request.getParameter("currentPrice")));
            product.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            product.setActive(request.getParameter("isActive") != null);
            productDAO.save(product);

            // 2. Aggiorna i dettagli specifici del tipo
            if (product.getProductType() == ProductDTO.ProductType.TradingCard) {
                TradingCardDTO card = tradingCardDAO.getById(productId);
                card.setCardSet(request.getParameter("cardSet"));
                card.setCardNumber(request.getParameter("cardNumber"));
                card.setRarity(request.getParameter("rarity"));
                card.setCardCondition(request.getParameter("cardCondition"));
                tradingCardDAO.save(card);
            }
            if (product.getProductType() == ProductDTO.ProductType.Accessory) {
                AccessoryDTO accessory = accessoryDAO.getById(productId);
                accessory.setAccessoryType(request.getParameter("accessoryType"));
                accessory.setMaterial(request.getParameter("material"));
                accessory.setColor(request.getParameter("color"));
                accessoryDAO.save(accessory);
            }

            // 3. Gestione immagini
            // Elimina quelle marcate
            String[] imagesToDeleteIds = request.getParameter("imagesToDelete").split(",");
            System.out.println(Arrays.toString(imagesToDeleteIds));
            for (String idStr : imagesToDeleteIds) {
                if (!idStr.isEmpty()) {
                    productImageDAO.delete(Integer.parseInt(idStr));
                }
            }

            // Aggiungi le nuove
            Map<String, Integer> newImageIdMap = new HashMap<>();
            Collection<Part> newFileParts = request.getParts().stream()
                    .filter(part -> "newProductImages".equals(part.getName()) && part.getSize() > 0)
                    .toList();

            for (Part filePart : newFileParts) {
                ImageDTO image = new ImageDTO();
                image.setMimeType(filePart.getContentType());
                image.setImageData(filePart.getInputStream().readAllBytes());
                imageDAO.save(image); // Salva e ottiene ID
                newImageIdMap.put(filePart.getSubmittedFileName(), image.getImageId());
            }

            // Riordina tutto
            String[] imageOrder = request.getParameter("imageOrder").split(",");
            for (int i = 0; i < imageOrder.length; i++) {
                String identifier = imageOrder[i];
                if (identifier.startsWith("id:")) {
                    int productImageId = Integer.parseInt(identifier.substring(3));
                    ProductImageDTO pi = productImageDAO.getById(productImageId);
                    if (pi != null) {
                        pi.setSortOrder(i);
                        productImageDAO.save(pi);
                    }
                } else if (identifier.startsWith("new:")) {
                    String fileName = identifier.substring(4);
                    Integer newImageId = newImageIdMap.get(fileName);
                    if (newImageId != null) {
                        ProductImageDTO newPi = new ProductImageDTO(0, productId, i, newImageId);
                        productImageDAO.save(newPi);
                    }
                }
            }

            // 4. Gestione Categorie
            Set<Integer> existingCategoryIds = productCategoryDAO.getAll(null).stream()
                    .filter(pc -> pc.getProductId() == productId)
                    .map(ProductCategoryDTO::getCategoryId).collect(Collectors.toSet());
            String[] selectedCategoriesStr = request.getParameterValues("categories");
            Set<Integer> newCategoryIds = (selectedCategoriesStr == null) ? new HashSet<>() :
                    Arrays.stream(selectedCategoriesStr).map(Integer::parseInt).collect(Collectors.toSet());

            // Rimuovi quelle deselezionate
            for (int oldCatId : existingCategoryIds) {
                if (!newCategoryIds.contains(oldCatId)) {
                    productCategoryDAO.delete(new ProductCategoryDTO.ProductCategoryKey(productId, oldCatId));
                }
            }
            // Aggiungi quelle nuove
            for (int newCatId : newCategoryIds) {
                if (!existingCategoryIds.contains(newCatId)) {
                    productCategoryDAO.save(new ProductCategoryDTO(productId, newCatId));
                }
            }

            conn.commit();
            NotificationUtil.sendNotification(request, "Prodotto aggiornato con successo!", "success");

        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            NotificationUtil.sendNotification(request, "Errore durante l'aggiornamento: " + e.getMessage(), "error");

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
