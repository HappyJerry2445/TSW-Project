<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Gestione Prodotti | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <div class="header-content">
            <h1><i class="fas fa-box-open"></i> Gestione Prodotti</h1>
            <p>Visualizza, aggiungi, modifica o elimina i prodotti del tuo catalogo.</p>
        </div>
        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Torna alla Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/admin/products/new" class="btn btn-primary">
                <i class="fas fa-plus"></i> Aggiungi Nuovo Prodotto
            </a>
        </div>
    </div>

    <%-- Card per contenere la tabella, per un look più pulito e moderno --%>
    <div class="content-card">
        <div class="table-container">
            <table class="products-table">
                <thead>
                <tr>
                    <th>Immagine</th>
                    <th>Nome Prodotto</th>
                    <th>SKU</th>
                    <th>Prezzo Corrente</th>
                    <th>Quantità</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <%--@elvariable id="products" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.ProductDTO>"--%>
                <%--@elvariable id="productImages" type="java.util.Map<java.lang.Integer, java.lang.Integer>"--%>
                <c:forEach var="product" items="${products}">
                    <tr>
                        <td data-label="Immagine" class="product-image-cell">
                            <c:set var="imageId" value="${productImages[product.productId]}"/>
                            <c:choose>
                                <c:when test="${not empty imageId}">
                                    <img src="${pageContext.request.contextPath}/image/${imageId}"
                                         alt="Immagine di <c:out value="${product.productName}"/>">
                                </c:when>
                                <c:otherwise>
                                    <div class="image-placeholder"><i class="fas fa-image"></i></div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Nome"><c:out value="${product.productName}"/></td>
                        <td data-label="SKU"><c:out value="${product.sku}"/></td>
                        <td data-label="Prezzo">
                            <fmt:setLocale value="it_IT"/>
                            <fmt:formatNumber value="${product.currentPrice}" type="currency" currencySymbol="€"/>
                        </td>
                        <td data-label="Quantità">
                            <c:choose>
                                <c:when test="${product.stockQuantity <= 0}">
                                    <span class="stock-badge stock-out">Esaurito</span>
                                </c:when>
                                <c:when test="${product.stockQuantity < 10}">
                                    <span class="stock-badge stock-low">${product.stockQuantity}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stock-badge stock-ok">${product.stockQuantity}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Stato">
                            <c:choose>
                                <c:when test="${product.isActive()}">
                                    <span class="status-badge status-active">Attivo</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-inactive">Inattivo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Azioni">
                            <div class="actions-cell">
                                <a href="${pageContext.request.contextPath}/admin/products/edit?id=${product.productId}"
                                   class="action-btn btn-edit" title="Modifica Prodotto">
                                    <i class="fas fa-pencil-alt"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/admin/products" method="post"
                                      onsubmit="return confirm('Sei sicuro di voler eliminare questo prodotto? L\'azione è irreversibile.');"
                                      style="display: inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="productId" value="${product.productId}">
                                    <button type="submit" class="action-btn btn-delete" title="Elimina Prodotto">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty products}">
                    <tr>
                        <td colspan="7" class="text-center" style="padding: 2rem;">Nessun prodotto trovato nel
                            catalogo.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>

</body>
</html>
