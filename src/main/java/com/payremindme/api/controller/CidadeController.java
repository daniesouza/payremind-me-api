package com.payremindme.api.controller;

import java.util.List;

import com.payremindme.api.model.Cidade;
import com.payremindme.api.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/cidades")
public class CidadeController {

    @Autowired
    private CidadeRepository cidadeRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Cidade> pesquisar(@RequestParam Long estado) {
        return cidadeRepository.findByEstadoCodigo(estado);
    }

}
