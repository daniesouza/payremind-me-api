package com.payremindme.api.controller;

import com.payremindme.api.event.RecursoCriadoEvent;
import com.payremindme.api.exception.PessoaInativaException;
import com.payremindme.api.exception.PessoaInexistenteException;
import com.payremindme.api.exceptionhandler.CustomExceptionHandler;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.service.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable){
        return  lancamentoService.findAllByFilter(lancamentoFilter,pageable);
    }

    @PostMapping
    public ResponseEntity<Lancamento> save(@RequestBody @Valid Lancamento lancamento, HttpServletResponse response){
        Lancamento lancamentoDb = lancamentoService.save(lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this,response,lancamentoDb.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoDb);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Lancamento> find(@PathVariable Long codigo){
       Lancamento lancamentoDb = lancamentoService.find(codigo);
       return ResponseEntity.ok(lancamentoDb);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Lancamento> update(@PathVariable Long codigo,@RequestBody @Valid Lancamento lancamento){
        return ResponseEntity.status(HttpStatus.OK).body(lancamentoService.update(codigo,lancamento));
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long codigo){
        lancamentoService.delete(codigo);
    }


    @ExceptionHandler({PessoaInativaException.class, PessoaInexistenteException.class})
    protected ResponseEntity<Object> handlePessoaException(RuntimeException ex , WebRequest webRequest){

        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa",null, LocaleContextHolder.getLocale());
        String mensagemTecnica = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<CustomExceptionHandler.ErrorMessage> errors = Collections.singletonList(new CustomExceptionHandler.ErrorMessage(mensagemUsuario, mensagemTecnica));

        return ResponseEntity.badRequest().body(errors);

    }
}
