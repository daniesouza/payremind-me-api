package com.payremindme.api.config.security;

import com.payremindme.api.config.property.PayRemindMeApiProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//SERVE PARA COLOCAR O REFRESH TOKEN NO COOKIE DO RESPONSE E REMOVER DO BODY POR MOTIVOS DE SEGURANCA
@Profile("oauth-security")
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

    @Autowired
    private PayRemindMeApiProperty payRemindMeApiProperty;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return methodParameter.getMethod().getName().equals("postAccessToken");
    }

    @Override
    public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken oAuth2AccessToken, MethodParameter methodParameter, MediaType mediaType,
                                             Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        HttpServletRequest req = ((ServletServerHttpRequest)serverHttpRequest).getServletRequest();
        HttpServletResponse resp = ((ServletServerHttpResponse)serverHttpResponse).getServletResponse();

        DefaultOAuth2AccessToken defaultToken = (DefaultOAuth2AccessToken) oAuth2AccessToken; 

        String refreshToken = oAuth2AccessToken.getRefreshToken().getValue();
        adicionarRefreshTokenCookie(refreshToken,req,resp);
        removerRefreshTokenBody(defaultToken);

        return oAuth2AccessToken;
    }

    private void removerRefreshTokenBody(DefaultOAuth2AccessToken defaultToken) {
        defaultToken.setRefreshToken(null);
    }

    private void adicionarRefreshTokenCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
        Cookie cookie = new Cookie("refresh_token",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(payRemindMeApiProperty.getSeguranca().isEnableHttps());
        cookie.setPath(req.getContextPath()+"/oauth/token");
        cookie.setMaxAge(2592000); // 30 dias
        resp.addCookie(cookie);
    }
}
