package com.payremindme.api.controller;

import com.payremindme.api.service.PessoaService;
import com.payremindme.api.event.RecursoCriadoEvent;
import com.payremindme.api.model.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read') ")
    public List<Pessoa> findAll(){
        return  pessoaService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write') ")
    public ResponseEntity<Pessoa> save(@RequestBody @Valid Pessoa pessoa, HttpServletResponse response){
        Pessoa pessoaDb = pessoaService.save(pessoa);
        publisher.publishEvent(new RecursoCriadoEvent(this,response,pessoaDb.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaDb);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read') ")
    public ResponseEntity<Pessoa> find(@PathVariable Long codigo){
       Pessoa pessoaDb = pessoaService.find(codigo);
       return ResponseEntity.ok(pessoaDb);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write') ")
    public ResponseEntity<Pessoa> update(@PathVariable Long codigo,@RequestBody @Valid Pessoa pessoa){
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.update(codigo,pessoa));
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write') ")
    public void delete(@PathVariable Long codigo){
        pessoaService.delete(codigo);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write') ")
    public void updatePropertyAtivo(@PathVariable Long codigo,@RequestBody Boolean ativo){
        pessoaService.updatePropertyAtivo(codigo,ativo);
    }
}
