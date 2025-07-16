package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ReviewDAO;
import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminStatsServlet", value = "/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AdminStatsServlet.class.getName());
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private ReviewDAO reviewDAO;
    private Gson gson; // Gson per la conversione in JSON

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        orderDAO = new OrderDAO(dataSource);
        userDAO = new UserDAO(dataSource);
        productDAO = new ProductDAO(dataSource);
        reviewDAO = new ReviewDAO(dataSource);
        gson = new Gson(); // Inizializza Gson
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Integer> stats = new HashMap<>();
        try {
            stats.put("ordersCount", orderDAO.countAll());
            stats.put("usersCount", userDAO.countAll());
            stats.put("productsCount", productDAO.countAll());
            stats.put("reviewsCount", reviewDAO.countAll());

            out.print(gson.toJson(stats));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle statistiche della dashboard", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Errore nel recupero delle statistiche.")));
        } finally {
            out.flush();
        }
    }
}
