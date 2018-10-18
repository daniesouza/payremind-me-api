package com.payremindme.api.config.security;

import org.apache.catalina.util.ParameterMap;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenPreProcessor implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        if(request.getRequestURI().endsWith("/oauth/token")
            && "refresh_token".equalsIgnoreCase(request.getParameter("grant_type"))
            && request.getCookies() != null){

            for(Cookie cookie:request.getCookies()){
                if(cookie.getName().equals("refresh_token")){
                    String refreshToken = cookie.getValue();
                    request = new CustomServletRequestWrapper(request,refreshToken);
                    break;
                }
            }
        }

        filterChain.doFilter(request,servletResponse);
    }

    @Override
    public void destroy() {
    }

    static  class CustomServletRequestWrapper extends HttpServletRequestWrapper{

        private String refreshToken;

        public CustomServletRequestWrapper(HttpServletRequest request,String refreshToken) {
            super(request);
            this.refreshToken = refreshToken;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
            map.put("refresh_token",new String[]{this.refreshToken});
            map.setLocked(true);
            return map;
        }
    }
}
