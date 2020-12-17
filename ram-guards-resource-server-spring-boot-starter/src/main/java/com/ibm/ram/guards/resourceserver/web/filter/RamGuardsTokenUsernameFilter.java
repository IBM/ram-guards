package com.ibm.ram.guards.resourceserver.web.filter;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RamGuardsTokenUsernameFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getParameter("username");
        String userPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (!StringUtils.isEmpty(username) && !username.trim().equals(userPrincipal)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("can not match the username in request param with the one in ram-guards token");
        }else {
            //doFilter
            filterChain.doFilter(request, response);
        }
    }
}