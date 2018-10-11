package com.payremindme.api.repository;

import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.custom.PessoaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa,Long>, PessoaRepositoryCustom {
}
