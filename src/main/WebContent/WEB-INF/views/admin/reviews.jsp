<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="/WEB-INF/functions" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Gestione Recensioni | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/admin-orders.css">
    <%-- Reused styles for table/filter layout --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/admin-reviews.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <div class="admin-header-content">
            <h1><i class="fas fa-star"></i> Gestione Recensioni</h1>
            <p>Visualizza e modera le recensioni lasciate dagli utenti.</p>
        </div>
        <div class="admin-header-actions">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Torna alla Dashboard
            </a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/components/error_message.jsp"/>

    <div class="content-card filter-card">
        <h2>Filtra Recensioni</h2>
        <form action="${pageContext.request.contextPath}/admin/reviews" method="get" class="filter-form">
            <div class="form-group">
                <label for="reviewsUserEmail">Email Utente:</label>
                <input type="text" id="reviewsUserEmail" name="reviewsUserEmail" placeholder="email@example.com"
                       value="${reviewsUserEmail}">
            </div>
            <div class="form-group">
                <label for="productName">Nome Prodotto:</label>
                <input type="text" id="productName" name="productName" placeholder="Nome prodotto"
                       value="${productName}">
            </div>
            <div class="form-group">
                <label for="reviewStatus">Stato:</label>
                <select id="reviewStatus" name="reviewStatus">
                    <option value="">Tutti</option>
                    <%--@elvariable id="reviewStatuses" type="com.cardhaven.cardhaven.model.dto.ReviewDTO.ReviewStatus[]"--%>
                    <c:forEach var="status" items="${reviewStatuses}">
                        <option value="${status}" ${status == param.reviewStatus ? 'selected' : ''}>
                            <c:out value="${status}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="minRating">Voto Minimo:</label>
                <select id="minRating" name="minRating">
                    <option value="">Qualsiasi</option>
                    <c:forEach begin="1" end="5" var="rating">
                        <option value="${rating}" ${rating == param.minRating ? 'selected' : ''}>
                            <c:out value="${rating}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="maxRating">Voto Massimo:</label>
                <select id="maxRating" name="maxRating">
                    <option value="">Qualsiasi</option>
                    <c:forEach begin="1" end="5" var="rating">
                        <option value="${rating}" ${rating == param.maxRating ? 'selected' : ''}>
                            <c:out value="${rating}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-actions filter-actions">
                <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> Applica Filtri</button>
                <a href="${pageContext.request.contextPath}/admin/reviews" class="btn btn-outline">Reset Filtri</a>
            </div>
        </form>
    </div>

    <div class="content-card">
        <div class="table-container">
            <table class="reviews-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Prodotto</th>
                    <th>Utente</th>
                    <th>Voto</th>
                    <th>Titolo</th>
                    <th>Data</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <%--@elvariable id="reviews" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.ReviewDTO>"--%>
                <%--@elvariable id="productMap" type="java.util.Map<java.lang.Integer, com.cardhaven.cardhaven.model.dto.ProductDTO>"--%>
                <%--@elvariable id="userMap" type="java.util.Map<java.lang.Integer, com.cardhaven.cardhaven.model.dto.UserDTO>"--%>
                <c:forEach var="review" items="${reviews}">
                    <tr>
                        <td data-label="ID">#<c:out value="${review.reviewId}"/></td>
                        <td data-label="Prodotto">
                            <c:set var="product" value="${productMap[review.productId]}"/>
                            <c:choose>
                                <c:when test="${not empty product}">
                                    <a href="${pageContext.request.contextPath}/products/detail/${product.productId}"
                                       target="_blank">
                                        <c:out value="${product.productName}"/>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    N/A (ID: <c:out value="${review.productId}"/>)
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Utente">
                            <div>

                                <c:set var="user" value="${userMap[review.userId]}"/>
                                <c:choose>
                                    <c:when test="${not empty user}">
                                        <c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/><br>
                                        <span class="customer-email"><c:out value="${user.email}"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        N/A (ID: <c:out value="${review.userId}"/>)
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </td>
                        <td data-label="Voto" class="rating-cell">
                            <div>
                                <c:forEach begin="1" end="5" var="i">
                                    <i class="fas fa-star ${i <= review.rating ? 'rated' : ''}"></i>
                                </c:forEach>
                            </div>
                        </td>
                        <td data-label="Titolo" class="review-title">
                            <div><c:out value="${review.title}"/></div>
                        </td>
                        <td data-label="Data"><c:out value="${my:formatDateTime(review.createdAt)}"/></td>
                        <td data-label="Stato" class="review-status-cell">
                            <form class="update-status-form" action="${pageContext.request.contextPath}/admin/reviews"
                                  method="post">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                <input type="hidden" name="reviewsUserEmail" value="${reviewsUserEmail}">
                                <input type="hidden" name="productName" value="${productName}">
                                <input type="hidden" name="reviewStatus" value="${reviewStatus}">
                                <input type="hidden" name="minRating" value="${minRating}">
                                <input type="hidden" name="maxRating" value="${maxRating}">
                                <select name="newStatus" class="status-select">
                                    <c:forEach var="status" items="${reviewStatuses}">
                                        <option value="${status}" ${review.reviewStatus == status ? 'selected' : ''}>
                                            <c:out value="${status.toItalian()}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </form>
                        </td>
                        <td data-label="Azioni">
                            <div class="actions-cell">
                                    <%-- Add a view review button/modal functionality if needed for full text --%>
                                <button type="button" class="action-btn btn-view view-review-btn"
                                        title="Visualizza recensione"
                                        data-review-text="<c:out value="${review.reviewText}"/>">
                                    <i class="fas fa-search"></i>
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/reviews" method="post"
                                      onsubmit="return confirm('Sei sicuro di voler eliminare questa recensione? L\'azione è irreversibile.');"
                                      style="display: inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="reviewId" value="${review.reviewId}">
                                    <input type="hidden" name="reviewsUserEmail" value="${reviewsUserEmail}">
                                    <input type="hidden" name="productName" value="${productName}">
                                    <input type="hidden" name="reviewStatus" value="${reviewStatus}">
                                    <input type="hidden" name="minRating" value="${minRating}">
                                    <input type="hidden" name="maxRating" value="${maxRating}">
                                    <button type="submit" class="action-btn btn-delete" title="Elimina Recensione">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty reviews}">
                    <tr>
                        <td colspan="8" class="text-center" style="padding: 2rem;">Nessuna recensione trovata con i
                            filtri selezionati.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Review Detail Modal -->
    <div id="reviewDetailModal" class="modal">
        <div class="modal-content">
            <span class="close-button">&times;</span>
            <h2>Testo Recensione</h2>
            <p id="modalReviewText"></p>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/admin/admin-reviews.js" defer></script>
</body>
</html>
