package com.payremindme.api.repository.lancamento;

import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.filter.LancamentoFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepositoryCustom {

    Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable);
}
