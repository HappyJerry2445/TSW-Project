package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.CategoryDAO;
import com.cardhaven.cardhaven.model.dto.CategoryDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "AdminCategoriesServlet", value = "/admin/categories")
public class AdminCategoriesServlet extends HttpServlet {

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9\\s'._-]{3,100}$"
    );

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        CategoryDAO categoryDAO = new CategoryDAO(ds);

        try {
            Collection<CategoryDTO> allCategories = categoryDAO.getAll(
                    "CategoryName"
            );

            Map<Integer, List<CategoryDTO>> childrenMap = allCategories
                    .stream()
                    .filter(c -> c.getParentId() != null)
                    .collect(Collectors.groupingBy(CategoryDTO::getParentId));

            List<CategoryDTO> rootCategories = allCategories
                    .stream()
                    .filter(c -> c.getParentId() == null)
                    .collect(Collectors.toList());

            request.setAttribute("rootCategories", rootCategories);
            request.setAttribute("childrenMap", childrenMap);
            request.setAttribute("allCategories", allCategories);

            request
                    .getRequestDispatcher("/WEB-INF/views/admin/categories.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(
                    "Errore del database durante il recupero delle categorie.",
                    e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Azione non specificata."
            );
            return;
        }

        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            default:
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Azione non valida."
                );
        }
    }

    private void handleCreate(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        CategoryDAO categoryDAO = new CategoryDAO(ds);

        try {
            List<String> errors = new ArrayList<>();
            String name = request.getParameter("categoryName");
            String typeStr = request.getParameter("categoryType");

            if (name == null || !NAME_PATTERN.matcher(name).matches()) {
                errors.add(
                        "Il nome della categoria non è valido (3-100 caratteri, solo lettere, numeri, spazi e caratteri speciali comuni)."
                );
            }
            try {
                CategoryDTO.CategoryType.valueOf(typeStr);
            } catch (Exception e) {
                errors.add("Il tipo di categoria non è valido.");
            }

            if (!errors.isEmpty()) {
                NotificationUtil.sendNotification(
                        request,
                        String.join(" ", errors),
                        "error"
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/categories"
                );
                return;
            }

            CategoryDTO category = new CategoryDTO();
            category.setName(name);
            category.setDescription(request.getParameter("description"));
            category.setType(typeStr);

            String parentIdStr = request.getParameter("parentId");
            if (parentIdStr != null && !parentIdStr.isEmpty()) {
                category.setParentId(Integer.parseInt(parentIdStr));
            }

            categoryDAO.save(category);
            NotificationUtil.sendNotification(
                    request,
                    "Categoria creata con successo!",
                    "success"
            );
        } catch (SQLException | IllegalArgumentException e) {
            NotificationUtil.sendNotification(
                    request,
                    "Errore nella creazione: " + e.getMessage(),
                    "error"
            );
        }

        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private void handleUpdate(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        CategoryDAO categoryDAO = new CategoryDAO(ds);

        try {
            int categoryId = Integer.parseInt(
                    request.getParameter("categoryId")
            );

            // --- Validation ---
            List<String> errors = new ArrayList<>();
            String name = request.getParameter("categoryName");
            String typeStr = request.getParameter("categoryType");
            String parentIdStr = request.getParameter("parentId");

            if (name == null || !NAME_PATTERN.matcher(name).matches()) {
                errors.add(
                        "Il nome della categoria non è valido (3-100 caratteri)."
                );
            }
            try {
                CategoryDTO.CategoryType.valueOf(typeStr);
            } catch (Exception e) {
                errors.add("Il tipo di categoria non è valido.");
            }
            if (parentIdStr != null && !parentIdStr.isEmpty()) {
                int parentId = Integer.parseInt(parentIdStr);
                if (parentId == categoryId) {
                    errors.add(
                            "Una categoria non può essere figlia di se stessa."
                    );
                }
            }

            if (!errors.isEmpty()) {
                NotificationUtil.sendNotification(
                        request,
                        String.join(" ", errors),
                        "error"
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/categories"
                );
                return;
            }
            // --- End Validation ---

            CategoryDTO category = categoryDAO.getById(categoryId);

            if (category == null) {
                NotificationUtil.sendNotification(
                        request,
                        "Categoria non trovata.",
                        "error"
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/categories"
                );
                return;
            }

            category.setName(name);
            category.setDescription(request.getParameter("description"));
            category.setType(typeStr);

            if (parentIdStr != null && !parentIdStr.isEmpty()) {
                category.setParentId(Integer.parseInt(parentIdStr));
            } else {
                category.setParentId(null);
            }

            categoryDAO.save(category);
            NotificationUtil.sendNotification(
                    request,
                    "Categoria aggiornata con successo!",
                    "success"
            );
        } catch (SQLException | IllegalArgumentException e) {
            NotificationUtil.sendNotification(
                    request,
                    "Errore nell'aggiornamento: " + e.getMessage(),
                    "error"
            );
        }

        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private void handleDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        CategoryDAO categoryDAO = new CategoryDAO(ds);

        try {
            int categoryId = Integer.parseInt(
                    request.getParameter("categoryId")
            );
            categoryDAO.delete(categoryId);
            NotificationUtil.sendNotification(
                    request,
                    "Categoria eliminata. Le sottocategorie sono state spostate al livello principale.",
                    "success"
            );
        } catch (SQLException | NumberFormatException e) {
            NotificationUtil.sendNotification(
                    request,
                    "Errore durante l'eliminazione.",
                    "error"
            );
        }

        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
}
