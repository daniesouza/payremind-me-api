package com.payremindme.api.controller;

import com.payremindme.api.event.RecursoCriadoEvent;
import com.payremindme.api.model.Categoria;
import com.payremindme.api.repository.CategoriaRepository;
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
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read') ")
    public List<Categoria> findAll(){
        return  categoriaRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write') ")
    public ResponseEntity<Categoria> create(@RequestBody @Valid Categoria categoria, HttpServletResponse response){
        Categoria categoriaDb = categoriaRepository.save(categoria);
        publisher.publishEvent(new RecursoCriadoEvent(this,response,categoriaDb.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaDb);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read') ")
    public ResponseEntity<Categoria> find(@PathVariable Long codigo){
       Categoria categoriaDb = categoriaRepository.findById(codigo).orElse(null);
       return categoriaDb != null ? ResponseEntity.ok(categoriaDb) : ResponseEntity.notFound().build();
    }

}
