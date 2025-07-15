<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Costruisce una stringa JSON per il data-attribute, gestendo l'escape dei caratteri --%>
<c:set var="nameJson" value="${fn:replace(category_to_render.name, \"'\", \"\\\\'\")}"/>
<c:set var="descJson" value="${fn:replace(category_to_render.description, \"'\", \"\\\\'\")}"/>
<c:set var="categoryJson"
       value="{'id': ${category_to_render.id}, 'name': '${nameJson}', 'parentId': ${empty category_to_render.parentId ? 'null' : category_to_render.parentId}, 'type': '${category_to_render.type}', 'description': '${descJson}'}"/>

<li class="category-item"
    data-category="${categoryJson}">
    <div class="category-item-content">
        <div class="category-info">
            <p><c:out value="${category_to_render.name}"/></p>
            <span class="type-badge"><c:out value="${category_to_render.type}"/></span>
        </div>
        <div class="category-actions">
            <a href="#" class="btn btn-sm btn-outline edit-btn">Modifica</a>
            <form action="${pageContext.request.contextPath}/admin/categories" method="post" style="display: inline;"
                  onsubmit="return confirm('Sei sicuro di voler eliminare questa categoria?');">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="categoryId" value="${category_to_render.id}">
                <button type="submit" class="btn btn-sm btn-danger">Elimina</button>
            </form>
        </div>
    </div>

    <%-- Chiamata Ricorsiva per le Sottocategorie --%>
    <c:set var="children" value="${childrenMap[category_to_render.id]}"/>
    <c:if test="${not empty children}">
        <ul class="category-tree">
            <c:forEach var="child" items="${children}">
                <c:set var="category_to_render" value="${child}" scope="request"/>
                <jsp:include page="_category_item.jsp"/>
            </c:forEach>
        </ul>
    </c:if>
</li>
