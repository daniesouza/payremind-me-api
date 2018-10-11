package com.payremindme.api.repository;

import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.custom.LancamentoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento,Long>, LancamentoRepositoryCustom {
}
