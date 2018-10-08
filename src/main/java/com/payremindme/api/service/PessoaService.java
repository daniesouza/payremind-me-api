package com.payremindme.api.service;

import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public List<Pessoa> findAll(){
        return  pessoaRepository.findAll();
    }

    public Pessoa find(Long codigo){
        Pessoa pessoaDb = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return pessoaDb;
    }

    public Pessoa save(Pessoa pessoa){
        return pessoaRepository.save(pessoa);
    }

    public Pessoa update(Long codigo, Pessoa pessoa){
        Pessoa pessoaDb = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        BeanUtils.copyProperties(pessoa,pessoaDb,"codigo");
        return pessoaRepository.save(pessoaDb);
    }

    public void delete(Long codigo){
        pessoaRepository.deleteById(codigo);
    }

    public void updatePropertyAtivo(Long codigo, Boolean ativo) {
        Pessoa pessoaDb = find(codigo);
        pessoaDb.setAtivo(ativo);
        save(pessoaDb);
    }
}
