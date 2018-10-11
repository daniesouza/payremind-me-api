package com.payremindme.api.repository.custom;

import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepositoryCustom {

    Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable);
    Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
}
