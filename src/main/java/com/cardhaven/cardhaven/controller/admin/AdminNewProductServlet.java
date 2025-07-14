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
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@WebServlet(name = "AdminNewProductServlet", value = "/admin/products/new")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 Mb
        maxFileSize = 1024 * 1024 * 10, // 5 Mb
        maxRequestSize = 1024 * 1024 * 15 // 10 Mb
) // Abilita il supporto per upload di file
public class AdminNewProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        CategoryDAO categoryDAO = new CategoryDAO(ds);

        try {
            // Passa alla JSP la lista delle categorie e i tipi di prodotto dall'enum
            Collection<CategoryDTO> categories = categoryDAO.getAll("CategoryName");
            request.setAttribute("categories", categories);
            request.setAttribute("productTypes", ProductDTO.ProductType.values());

            request.getRequestDispatcher("/WEB-INF/views/admin/new-product.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore nel caricamento dei dati per il form.");
            request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        Connection conn = null;

        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false); // Inizia la transazione

            // DAOs
            ProductDAO productDAO = new ProductDAO(ds);
            ImageDAO imageDAO = new ImageDAO(ds);
            ProductImageDAO productImageDAO = new ProductImageDAO(ds);
            TradingCardDAO tradingCardDAO = new TradingCardDAO(ds);
            AccessoryDAO accessoryDAO = new AccessoryDAO(ds);
            ProductCategoryDAO productCategoryDAO = new ProductCategoryDAO(ds);

            // 1. Crea e salva il prodotto base
            ProductDTO product = new ProductDTO();
            product.setProductName(request.getParameter("productName"));
            product.setSku(request.getParameter("sku"));
            product.setBasePrice(new BigDecimal(request.getParameter("basePrice")));
            product.setCurrentPrice(new BigDecimal(request.getParameter("currentPrice")));
            product.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            product.setProductType(ProductDTO.ProductType.valueOf(request.getParameter("productType")));
            product.setActive(request.getParameter("isActive") != null);
            productDAO.save(product); // L'ID viene settato dal DAO

            // 2. Salva le immagini e le loro associazioni
            List<Part> fileParts = request.getParts().stream()
                    .filter(part -> "productImages".equals(part.getName()) && part.getSize() > 0)
                    .toList();
            System.out.println(fileParts);

            String[] imageOrder = request.getParameter("imageOrder").split(",");
            System.out.println(Arrays.toString(imageOrder));

            if (!fileParts.isEmpty()) {
                for (int i = 0; i < imageOrder.length; i++) {
                    String orderedFileName = imageOrder[i];
                    for (Part filePart : fileParts) {
                        if (filePart.getSubmittedFileName().equals(orderedFileName)) {
                            // Salva l'immagine
                            ImageDTO image = new ImageDTO();
                            try (InputStream is = filePart.getInputStream()) {
                                image.setImageData(is.readAllBytes());
                            }
                            image.setMimeType(filePart.getContentType());
                            imageDAO.save(image);

                            // Crea l'associazione Prodotto-Immagine
                            ProductImageDTO productImage = new ProductImageDTO();
                            productImage.setProductId(product.getProductId());
                            productImage.setImageId(image.getImageId());
                            productImage.setSortOrder(i); // Usa l'ordine dal form
                            productImageDAO.save(productImage);
                            break;
                        }
                    }
                }
            }


            // 3. Salva i dati specifici del tipo di prodotto
            if (product.getProductType() == ProductDTO.ProductType.TradingCard) {
                TradingCardDTO card = new TradingCardDTO();
                card.setCardId(product.getProductId());
                card.setCardSet(request.getParameter("cardSet"));
                card.setCardNumber(request.getParameter("cardNumber"));
                card.setRarity(request.getParameter("rarity"));
                card.setCardCondition(request.getParameter("cardCondition"));
                tradingCardDAO.save(card);
            } else if (product.getProductType() == ProductDTO.ProductType.Accessory) {
                AccessoryDTO accessory = new AccessoryDTO();
                accessory.setAccessoryId(product.getProductId());
                accessory.setAccessoryType(request.getParameter("accessoryType"));
                accessory.setMaterial(request.getParameter("material"));
                accessory.setColor(request.getParameter("color"));
                accessoryDAO.save(accessory);
            }

            // 4. Associa le categorie
            String[] selectedCategories = request.getParameterValues("categories");
            if (selectedCategories != null) {
                for (String catIdStr : selectedCategories) {
                    int categoryId = Integer.parseInt(catIdStr);
                    ProductCategoryDTO productCategory = new ProductCategoryDTO(product.getProductId(), categoryId);
                    productCategoryDAO.save(productCategory);
                }
            }

            conn.commit(); // Se tutto è andato bene, conferma la transazione
            NotificationUtil.sendNotification(request, "Prodotto creato con successo!", "success");
            response.sendRedirect(request.getContextPath() + "/admin/products");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // In caso di errore, annulla tutte le modifiche
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            List<String> errors = new ArrayList<>();
            errors.add("Errore nella creazione del prodotto: " + e.getMessage());
            request.setAttribute("errors", errors);

            // Ricrea gli oggetti DTO con i dati inviati per ripopolare il form
            ProductDTO repopulatedProduct = new ProductDTO();
            repopulatedProduct.setProductName(request.getParameter("productName"));
            repopulatedProduct.setSku(request.getParameter("sku"));
            try {
                repopulatedProduct.setBasePrice(new BigDecimal(request.getParameter("basePrice")));
                repopulatedProduct.setCurrentPrice(new BigDecimal(request.getParameter("currentPrice")));
                repopulatedProduct.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            } catch (NumberFormatException nfe) {
                errors.add("Prezzo o quantità non validi.");
            }
            String productTypeStr = request.getParameter("productType");
            if (productTypeStr != null && !productTypeStr.isEmpty()) {
                repopulatedProduct.setProductType(ProductDTO.ProductType.valueOf(productTypeStr));
                if (repopulatedProduct.getProductType() == ProductDTO.ProductType.TradingCard) {
                    TradingCardDTO repopulatedCard = new TradingCardDTO();
                    repopulatedCard.setCardSet(request.getParameter("cardSet"));
                    repopulatedCard.setCardNumber(request.getParameter("cardNumber"));
                    repopulatedCard.setRarity(request.getParameter("rarity"));
                    repopulatedCard.setCardCondition(request.getParameter("cardCondition"));
                    request.setAttribute("repopulatedCard", repopulatedCard);
                } else if (repopulatedProduct.getProductType() == ProductDTO.ProductType.Accessory) {
                    AccessoryDTO repopulatedAccessory = new AccessoryDTO();
                    repopulatedAccessory.setAccessoryType(request.getParameter("accessoryType"));
                    repopulatedAccessory.setMaterial(request.getParameter("material"));
                    repopulatedAccessory.setColor(request.getParameter("color"));
                    request.setAttribute("repopulatedAccessory", repopulatedAccessory);
                }
            }
            repopulatedProduct.setActive(request.getParameter("isActive") != null);
            request.setAttribute("repopulatedProduct", repopulatedProduct);
            request.setAttribute("repopulatedDescription", request.getParameter("description"));

            String[] selectedCategories = request.getParameterValues("categories");
            if (selectedCategories != null) {
                request.setAttribute("selectedCategories", Arrays.asList(selectedCategories));
            }

            // Ricarica i dati necessari per la JSP (categorie, tipi, etc.)
            try {
                CategoryDAO categoryDAO = new CategoryDAO(ds);
                request.setAttribute("categories", categoryDAO.getAll("CategoryName"));
                request.setAttribute("productTypes", ProductDTO.ProductType.values());
            } catch (SQLException ex) {
                errors.add("Impossibile ricaricare i dati del form.");
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/new-product.jsp").forward(request, response);
            // --- FINE LOGICA DI RIPOPOLAMENTO ---
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
