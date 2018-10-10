package com.payremindme.api.service;

import com.payremindme.api.exception.PessoaInativaException;
import com.payremindme.api.exception.PessoaInexistenteException;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.LancamentoRepository;
import com.payremindme.api.repository.PessoaRepository;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoService {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable){
        return  lancamentoRepository.findAllByFilter(lancamentoFilter,pageable);
    }

    public List<Lancamento> findAll(){
        return  lancamentoRepository.findAll();
    }

    public Lancamento find(Long codigo){
        Lancamento lancamentoDb = lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return lancamentoDb;
    }

    public Lancamento save(Lancamento lancamento){
        Pessoa pessoaDb = pessoaRepository.findById(lancamento.getPessoa().getCodigo()).orElseThrow(PessoaInexistenteException::new);

        if(!pessoaDb.getAtivo()){
            throw new PessoaInativaException();
        }

        return lancamentoRepository.save(lancamento);
    }

    public Lancamento update(Long codigo, Lancamento lancamento){
        Lancamento lancamentoDb = lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        BeanUtils.copyProperties(lancamento,lancamentoDb,"codigo");
        return lancamentoRepository.save(lancamentoDb);
    }

    public void delete(Long codigo){
        lancamentoRepository.deleteById(codigo);
    }

    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.resumir(lancamentoFilter,pageable);
    }
}
