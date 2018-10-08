package com.payremindme.api.controller;

import com.payremindme.api.event.RecursoCriadoEvent;
import com.payremindme.api.model.Categoria;
import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.CategoriaRepository;
import com.payremindme.api.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public List<Pessoa> findAll(){
        return  pessoaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Pessoa> create(@RequestBody @Valid Pessoa pessoa, HttpServletResponse response){
        Pessoa pessoaDb = pessoaRepository.save(pessoa);
        publisher.publishEvent(new RecursoCriadoEvent(this,response,pessoa.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaDb);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Pessoa> find(@PathVariable Long codigo){
       Pessoa pessoaDb = pessoaRepository.findById(codigo).orElse(null);
       return pessoaDb != null ? ResponseEntity.ok(pessoaDb) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long codigo){
        pessoaRepository.deleteById(codigo);
    }
}
