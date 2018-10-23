package com.payremindme.api.controller;

import com.payremindme.api.model.Estado;
import com.payremindme.api.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private EstadoRepository estadoRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Estado> listar() {
        return estadoRepository.findAll();
    }
}