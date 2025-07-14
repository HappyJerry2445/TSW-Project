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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@WebServlet(name = "AdminNewProductServlet", value = "/admin/products/new")
@MultipartConfig // Abilita il supporto per upload di file
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

        } catch (SQLException | IOException | ServletException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // In caso di errore, annulla tutte le modifiche
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            // TODO: Ripopolare il form con i dati inseriti e mostrare l'errore
            NotificationUtil.sendNotification(request, "Errore nella creazione del prodotto: " + e.getMessage(), "error");
            response.sendRedirect(request.getContextPath() + "/admin/products/new");
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
