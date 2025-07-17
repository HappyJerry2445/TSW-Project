package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/products/suggest")
public class SearchSuggestServlet extends HttpServlet {

    private static final int MAX_SUGGESTIONS = 10;
    private static final Logger log = LoggerFactory.getLogger(
        SearchSuggestServlet.class
    );
    private ProductDAO productDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute(
            "ds"
        );
        if (dataSource == null) {
            throw new ServletException(
                "DataSource not found in ServletContext"
            );
        }
        productDAO = new ProductDAO(dataSource);
        gson = new Gson();
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        String query = request.getParameter("query");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (query == null || query.trim().length() < 2) {
            // Non restituire nulla se la query è troppo corta per evitare risultati inutili
            out.print(gson.toJson(Collections.emptyList()));
            out.flush();
            return;
        }

        try {
            List<ProductDTO> suggestedProducts =
                productDAO.findByNameSuggestions(query, MAX_SUGGESTIONS);
            // Trasforma la lista di ProductDTO in una lista di mappe (o un oggetto specifico)
            // per inviare sia l'ID che il nome.
            List<Suggestion> suggestions = suggestedProducts
                .stream()
                .map(p -> new Suggestion(p.getProductId(), p.getProductName()))
                .collect(Collectors.toList());

            String jsonResponse = gson.toJson(suggestions);
            out.print(jsonResponse);
        } catch (SQLException e) {
            // Log l'errore e invia una risposta di errore
            log.debug(
                "Errore del database durante il recupero dei suggerimenti di ricerca",
                e
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(
                gson.toJson(
                    Collections.singletonMap(
                        "error",
                        "Errore del server durante la ricerca."
                    )
                )
            );
        } finally {
            out.flush();
        }
    }

    // Classe interna per una struttura JSON più chiara
    private static class Suggestion {

        private final int id;
        private final String name;

        public Suggestion(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
