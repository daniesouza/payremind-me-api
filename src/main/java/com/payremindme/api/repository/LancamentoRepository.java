package com.payremindme.api.repository;

import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.custom.LancamentoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento,Long>, LancamentoRepositoryCustom {

    List<Lancamento> findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate dataVencimento);
}
