package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.beans.Category;
import com.cardhaven.cardhaven.model.dao.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private CategoryDAO categoryDao;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        categoryDao = new CategoryDAO(ds);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Collection<Category> categories = categoryDao.doRetrieveAll(null);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/categories.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Errore nel recupero delle categorie", e);
        }
    }
}
