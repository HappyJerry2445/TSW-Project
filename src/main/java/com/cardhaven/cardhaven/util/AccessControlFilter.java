package com.cardhaven.cardhaven.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "/AccessControlFilter", urlPatterns = "/*")
public class AccessControlFilter extends HttpFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        var httpReq = (HttpServletRequest) req;
        var httpRes = (HttpServletResponse) res;
        var isAdmin = (Boolean) httpReq.getSession().getAttribute("isAdmin");
        var path = httpReq.getServletPath();
        System.out.println(path);
        if (path.contains("/common/") && isAdmin == null) {
            httpRes.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }
        if (path.contains("/admin/") && (isAdmin == null || !isAdmin)) {
            httpRes.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }
}
