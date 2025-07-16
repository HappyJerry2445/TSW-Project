package com.cardhaven.cardhaven.model;

import com.cardhaven.cardhaven.model.dao.CategoryDAO;
import com.cardhaven.cardhaven.model.dto.CategoryDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.SQLException;
import java.util.Collection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebListener
public class MainContext implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        DataSource ds = null;
        try {
            Context intiCtx = new InitialContext();
            Context envCtx = (Context) intiCtx.lookup("java:comp/env");
            ds = (DataSource) envCtx.lookup("jdbc/CardHavenDB");
        } catch (NamingException e) {
            System.out.println("Error: " + e.getMessage());
        }
        context.setAttribute("ds", ds);
        System.out.println("DataSource creation..." + ds);

        // Carica le categorie nell'application scope all'avvio
        if (ds != null) {
            CategoryDAO categoryDAO = new CategoryDAO(ds);
            try {
                Collection<CategoryDTO> allCategories = categoryDAO.getAll(
                    "CategoryName"
                );
                context.setAttribute("allCategories", allCategories);
                System.out.println(
                    "Caricate " +
                    allCategories.size() +
                    " categorie nell'application scope."
                );
            } catch (SQLException e) {
                System.err.println(
                    "Impossibile caricare le categorie nell'application scope: " +
                    e.getMessage()
                );
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
