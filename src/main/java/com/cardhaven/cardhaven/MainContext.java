package com.cardhaven.cardhaven;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
