package com.cardhaven.cardhaven.filter;

import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

@WebFilter(filterName = "/AccessControlFilter", urlPatterns = "/*")
public class AccessControlFilter extends HttpFilter implements Filter {
    // TODO Could give the redirectAfterLogin attribute a default value on any page that isn't login or user-only
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        var httpReq = (HttpServletRequest) req;
        var httpRes = (HttpServletResponse) res;
        var userId = (Integer) httpReq.getSession().getAttribute("userId");
        var ds = (javax.sql.DataSource) getServletContext().getAttribute("ds");
        var userDAO = new UserDAO(ds);
        UserDTO.Role userRole = null;
        try {
            var userDTO = userDAO.getById(userId);
            userRole = userDTO.getRole();

        } catch (Exception e) {
        }
        var path = httpReq.getServletPath();
        if (path.contains("/common/") && userId == null) {
            httpReq.getSession().setAttribute("redirectAfterLogin", httpReq.getRequestURI());
            httpRes.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }
        if (path.contains("/admin/") && (userId == null || !Objects.equals(userRole, UserDTO.Role.Admin))) {
            httpReq.getSession().setAttribute("redirectAfterLogin", httpReq.getRequestURI());
            httpRes.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }
}
