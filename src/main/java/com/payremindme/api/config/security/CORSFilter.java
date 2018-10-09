package com.payremindme.api.config.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Classe resposavel por implementar Filtros.
 * @see CORSFilter
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {
    
    private String origemPermitida = "http://localhost:8000";

    @Override
    public void destroy() {
        //Do nothing
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", origemPermitida);
        response.setHeader("Access-Control-Allow-Credentials", "true");

        
        if("OPTIONS".equalsIgnoreCase(request.getMethod()) && origemPermitida.equalsIgnoreCase(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization, Accept");

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }
        
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
      //Do nothing
    }

}
