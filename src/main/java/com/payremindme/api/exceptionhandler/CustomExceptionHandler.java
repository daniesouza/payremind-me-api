package com.payremindme.api.exceptionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String mensagemUsuario = messageSource.getMessage("mensagem.invalida",null, LocaleContextHolder.getLocale());
        String mensagemTecnica = ex.getCause().toString();
        List<ErrorMessage> errors = Collections.singletonList(new ErrorMessage(mensagemUsuario, mensagemTecnica));

        return handleExceptionInternal(ex,errors,headers,HttpStatus.BAD_REQUEST,request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<ErrorMessage> errors = createErrorList(ex.getBindingResult());
        return handleExceptionInternal(ex,errors,headers,HttpStatus.BAD_REQUEST,request);
    }


    @ExceptionHandler({EmptyResultDataAccessException.class})
    protected ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex , WebRequest webRequest){

        String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado",null, LocaleContextHolder.getLocale());
        String mensagemTecnica = ex.toString();
        List<ErrorMessage> errors = Collections.singletonList(new ErrorMessage(mensagemUsuario, mensagemTecnica));

        return handleExceptionInternal(ex,errors,new HttpHeaders(),HttpStatus.NOT_FOUND,webRequest);

    }

    private List<ErrorMessage> createErrorList(BindingResult bindingResult){

        List<ErrorMessage> errors = new ArrayList<>();

        for(FieldError fieldError:bindingResult.getFieldErrors()){

            String mensagemUsuario = messageSource.getMessage(fieldError,LocaleContextHolder.getLocale());
            String mensagemTecnica = fieldError.toString();

            errors.add(new ErrorMessage(mensagemUsuario, mensagemTecnica));
        }

        return errors;
    }

    private static class ErrorMessage{

        private String mensagemUsuario;
        private String mensagemTecnica;

        public ErrorMessage(String mensagemUsuario, String mensagemTecnica) {
            this.mensagemUsuario = mensagemUsuario;
            this.mensagemTecnica = mensagemTecnica;
        }

        public String getMensagemUsuario() {
            return mensagemUsuario;
        }

        public String getMensagemTecnica() {
            return mensagemTecnica;
        }
    }
}
