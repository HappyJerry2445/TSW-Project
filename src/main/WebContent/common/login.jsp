<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="it">
    <head>
        <title>Login</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600;700&amp;family=Open+Sans:wght@400;600&amp;display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    </head>
        <body>
            <jsp:include page="header.jsp"/>

            <div class="login-container">
                <h2 class="section-title">Accedi al tuo account</h2>

                <% List<String> errors = (List<String>) request.getAttribute("errors"); %>
                <% if(errors != null && !errors.isEmpty()) { %>
                <div class="alert alert-danger">
                    <% for (String error: errors) { %>
                    <p><%= error %></p>
                    <% } %>
                </div>
                <% } %>

                <form action="Login" method="post" class="login-form">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" class="form-input" placeholder="Inserisci la tua email">
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <input id="password" type="password" name="password" class="form-input" placeholder="Inserisci la tua password">
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">Accedi</button>
                </form>

                <div class="login-links">
                    <a href="#">Password dimenticata?</a>
                    <a href="#">Registrati</a>
                </div>
            </div>

            <jsp:include page="footer.jsp"/>
        </body>
</html>