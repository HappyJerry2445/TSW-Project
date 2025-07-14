package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.model.dto.ProductImageDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AdminProductsServlet", value = "/admin/products")
public class AdminProductsServlet extends HttpServlet {

    private ProductDAO productDAO;
    private ProductImageDAO productImageDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
            productDAO = new ProductDAO(dataSource);
            productImageDAO = new ProductImageDAO(dataSource);
        } catch (Exception e) {
            throw new ServletException("Impossibile inizializzare il DAO per i prodotti", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recupera tutti i prodotti ordinati per nome
            Collection<ProductDTO> products = productDAO.getAll("ProductName");

            // Crea una mappa per contenere l'ID dell'immagine principale per ogni prodotto
            // Chiave: ProductId, Valore: ImageId
            Map<Integer, Integer> productFirstImageIds = new HashMap<>();
            for (ProductDTO product : products) {
                ProductImageDTO firstImage = productImageDAO.getFirstByProductId(product.getProductId());
                if (firstImage != null) {
                    productFirstImageIds.put(product.getProductId(), firstImage.getImageId());
                }
            }

            request.setAttribute("products", products);
            request.setAttribute("productImages", productFirstImageIds);

            // Inoltra alla pagina JSP
            request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);

        } catch (SQLException e) {
            // Gestione dell'errore: log e pagina di errore
            e.printStackTrace(); // Logga l'errore per il debug
            request.setAttribute("errorMessage", "Errore durante il recupero dei prodotti dal database.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            handleDelete(request, response);
        } else {
            // Altre azioni POST possono essere gestite qui
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            boolean deleted = productDAO.delete(productId);

            if (deleted) {
                NotificationUtil.sendNotification(request, "Prodotto eliminato con successo.", "success");
            } else {
                NotificationUtil.sendNotification(request, "Impossibile eliminare il prodotto. Potrebbe non esistere più.", "error");
            }
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "ID del prodotto non valido.", "error");
            e.printStackTrace();
        } catch (SQLException e) {
            NotificationUtil.sendNotification(request, "Errore del database durante l'eliminazione del prodotto.", "error");
            e.printStackTrace();
        }

        // Reindirizza alla pagina dei prodotti per mostrare l'elenco aggiornato
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }
}