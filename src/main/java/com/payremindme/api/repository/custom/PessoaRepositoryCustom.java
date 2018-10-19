package com.payremindme.api.repository.custom;

import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.filter.PessoaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaRepositoryCustom {

    Page<Pessoa> findAllByFilter(PessoaFilter pessoaFilter, Pageable pageable);
}
