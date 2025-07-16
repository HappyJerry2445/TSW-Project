<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" uri="/WEB-INF/functions" %>

<%-- Imposta il titolo della pagina con il nome del prodotto --%>
<c:set var="pageTitle" value="${product.productName} | CardHaven" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/productdetails.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container">
    <c:if test="${not empty errors}">
        <div class="alert alert-danger" role="alert">
            <c:forEach var="error" items="${errors}">
                <p>${error}</p>
            </c:forEach>
        </div>
    </c:if>

    <c:if test="${not empty product}">
        <div class="product-details-grid">
            <!-- Colonna Sinistra: Galleria Immagini -->
            <div class="product-gallery">
                <div class="main-image-container">
                    <c:choose>
                        <c:when test="${not empty productImages}">
                            <img id="main-product-image" src="${pageContext.request.contextPath}/image/${productImages[0].imageId}" alt="Immagine principale di ${product.productName}">
                        </c:when>
                        <c:otherwise>
                            <img id="main-product-image" src="${pageContext.request.contextPath}/images/noimage.png" alt="Immagine non disponibile">
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${productImages.size() > 1}">
                    <div class="thumbnail-strip">
                        <c:forEach var="img" items="${productImages}">
                            <img src="${pageContext.request.contextPath}/image/${img.imageId}" alt="Miniatura di ${product.productName}" class="thumbnail-item" data-full-image="${pageContext.request.contextPath}/image/${img.imageId}">
                        </c:forEach>
                    </div>
                </c:if>
            </div>

            <!-- Colonna Destra: Informazioni Principali -->
            <div class="product-info-main">
                <div class="product-categories">
                    <c:forEach var="cat" items="${categories}" varStatus="loop">
                        <a href="${pageContext.request.contextPath}/products/category/${cat.id}">${cat.name}</a>
                        <c:if test="${not loop.last}"> / </c:if>
                    </c:forEach>
                </div>
                <h1 class="product-title">${product.productName}</h1>
                <p class="product-sku">SKU: ${product.sku}</p>

                <div class="price-container-details">
                    <c:choose>
                        <c:when test="${product.currentPrice < product.basePrice}">
                             <span class="original-price-details">
                                 <fmt:formatNumber value="${product.basePrice}" type="currency" currencySymbol="€"/>
                             </span>
                            <span class="current-price-details sale">
                                <fmt:formatNumber value="${product.currentPrice}" type="currency" currencySymbol="€"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                             <span class="current-price-details">
                                 <fmt:formatNumber value="${product.currentPrice}" type="currency" currencySymbol="€"/>
                             </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="stock-info">
                    <c:choose>
                        <c:when test="${product.stockQuantity > 0}">
                            <span class="stock-status in-stock"><i class="fas fa-check-circle"></i> Disponibile</span>
                            <span class="stock-level">(${product.stockQuantity} rimanenti)</span>
                        </c:when>
                        <c:otherwise>
                            <span class="stock-status out-of-stock"><i class="fas fa-times-circle"></i> Esaurito</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${product.stockQuantity > 0}">
                    <form action="${pageContext.request.contextPath}/cart/add" method="post" class="add-to-cart-form">
                        <input type="hidden" name="productId" value="${product.productId}">
                        <div class="quantity-selector">
                            <label for="quantity">Quantità:</label>
                            <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQuantity}">
                        </div>
                        <button type="submit" class="btn btn-primary btn-lg btn-block">
                            <i class="fas fa-cart-plus"></i> Aggiungi al Carrello
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

        <!-- Sezione Dettagli e Recensioni (sotto le colonne) -->
        <div class="product-details-tabs">
            <nav class="tab-navigation">
                <a href="#tab-description" class="tab-link active">Descrizione</a>
                <a href="#tab-specs" class="tab-link">Dettagli</a>
                <a href="#tab-reviews" class="tab-link">Recensioni (${reviews.size()})</a>
            </nav>
            <div class="tab-content">
                <div id="tab-description" class="tab-pane active">
                    <c:choose>
                        <c:when test="${not empty product.productDescription}">
                            <p>${product.productDescription}</p>
                        </c:when>
                        <c:otherwise>
                            <p>Nessuna descrizione disponibile per questo prodotto.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="tab-specs" class="tab-pane">
                    <table class="specs-table">
                        <c:if test="${not empty cardDetails}">
                            <tr><th>Set</th><td>${cardDetails.cardSet}</td></tr>
                            <tr><th>Numero Carta</th><td>${cardDetails.cardNumber}</td></tr>
                            <tr><th>Rarità</th><td>${cardDetails.rarity}</td></tr>
                            <tr><th>Condizione</th><td>${cardDetails.cardCondition}</td></tr>
                            <c:if test="${not empty cardDetails.artist}"><tr><th>Artista</th><td>${cardDetails.artist}</td></tr></c:if>
                            <c:if test="${not empty cardDetails.yearPublished}"><tr><th>Anno</th><td>${cardDetails.yearPublished}</td></tr></c:if>
                        </c:if>
                        <c:if test="${not empty accessoryDetails}">
                            <tr><th>Tipo Accessorio</th><td>${accessoryDetails.accessoryType}</td></tr>
                            <c:if test="${not empty accessoryDetails.material}"><tr><th>Materiale</th><td>${accessoryDetails.material}</td></tr></c:if>
                            <c:if test="${not empty accessoryDetails.color}"><tr><th>Colore</th><td>${accessoryDetails.color}</td></tr></c:if>
                            <c:if test="${not empty accessoryDetails.dimensions}"><tr><th>Dimensioni</th><td>${accessoryDetails.dimensions}</td></tr></c:if>
                        </c:if>
                    </table>
                </div>
                <div id="tab-reviews" class="tab-pane">
                    <c:choose>
                        <c:when test="${not empty reviews}">
                            <div class="review-list">
                                <c:forEach var="review" items="${reviews}">
                                    <div class="review-item">
                                        <div class="review-header">
                                            <span class="review-author">${reviewUsers[review.userId].firstName}</span>
                                            <span class="review-date">${my:formatDateTimePattern(review.createdAt, "dd MMMM yyyy")}</span>
                                        </div>
                                        <div class="review-rating">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="fas fa-star ${i <= review.rating ? 'rated' : ''}"></i>
                                            </c:forEach>
                                        </div>
                                        <h4 class="review-title">${review.title}</h4>
                                        <p class="review-text">${review.reviewText}</p>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p>Non ci sono ancora recensioni per questo prodotto. Sii il primo a lasciarne una!</p>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${canReview}">
                        <div class="write-review-section">
                            <hr>
                            <h4>Scrivi la tua recensione</h4>
                            <form action="${pageContext.request.contextPath}/reviews/submit" method="post" id="review-form">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <div class="form-group">
                                    <label>La tua valutazione</label>
                                    <div class="star-rating">
                                        <input type="radio" id="star5" name="rating" value="5" required/><label for="star5" title="5 stelle"></label>
                                        <input type="radio" id="star4" name="rating" value="4"/><label for="star4" title="4 stelle"></label>
                                        <input type="radio" id="star3" name="rating" value="3"/><label for="star3" title="3 stelle"></label>
                                        <input type="radio" id="star2" name="rating" value="2"/><label for="star2" title="2 stelle"></label>
                                        <input type="radio" id="star1" name="rating" value="1"/><label for="star1" title="1 stella"></label>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="review-title">Titolo della recensione</label>
                                    <input type="text" id="review-title" name="title" class="form-control" required>
                                </div>
                                <div class="form-group">
                                    <label for="review-text">La tua recensione</label>
                                    <textarea id="review-text" name="reviewText" rows="5" class="form-control" required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary">Invia Recensione</button>
                            </form>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </c:if>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/product-details.js"></script>
<script src="${pageContext.request.contextPath}/scripts/async-cart.js"></script>
</body>
</html>
