package com.cardhaven.cardhaven.util;

import com.cardhaven.cardhaven.model.dto.UserDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "/AccessControlFilter", urlPatterns = "/*")
public class AccessControlFilter extends HttpFilter implements Filter {
    // TODO Could give the redirectAfterLogin attribute a default value on any page that isn't login or user-only
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        var httpReq = (HttpServletRequest) req;
        var httpRes = (HttpServletResponse) res;
        var user = (UserDTO) httpReq.getSession().getAttribute("loggedInUser");
        var path = httpReq.getServletPath();
        System.out.println(path);
        if (path.contains("/common/") && user == null) {
            httpReq.getSession().setAttribute("redirectAfterLogin", httpReq.getRequestURI());
            httpRes.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }
        if (path.contains("/admin/") && (user == null || !user.getRole().equals(UserDTO.Role.Admin))) {
            httpReq.getSession().setAttribute("redirectAfterLogin", httpReq.getRequestURI());
            httpRes.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }
}
