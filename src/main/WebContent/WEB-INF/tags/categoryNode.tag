<%@ tag import="com.cardhaven.cardhaven.model.dto.CategoryDTO" %>
<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mytags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="currentCategory" required="true" type="com.cardhaven.cardhaven.model.dto.CategoryDTO" %>
<%@ attribute name="allCategories" required="true" type="java.util.List" %>


<c:set var="children" value="<%= new ArrayList<CategoryDTO>() %>"/>
<c:forEach var="potentialChild" items="${allCategories}">
    <c:if test="${not empty potentialChild.parentId and potentialChild.parentId == currentCategory.id}">
        <%
            List<CategoryDTO> childrenList = (List<CategoryDTO>) jspContext.getAttribute("children");
            CategoryDTO child = (CategoryDTO) jspContext.getAttribute("potentialChild");
            childrenList.add(child);
        %>
    </c:if>
</c:forEach>

<li class="${not empty children ? 'has-submenu' : ''}">
    <a href="${pageContext.request.contextPath}/products/category/${currentCategory.id}">
        <c:out value="${currentCategory.name}"/>
        <c:if test="${not empty children}">
            <i class="fas fa-chevron-right submenu-icon"></i>
        </c:if>
    </a>

    <c:if test="${not empty children}">
        <ul class="submenu">
            <c:forEach var="child" items="${children}">
                <mytags:categoryNode currentCategory="${child}" allCategories="${allCategories}"/>
            </c:forEach>
        </ul>
    </c:if>
</li>