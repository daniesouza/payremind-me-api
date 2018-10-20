package com.payremindme.api.repository.custom;

import com.payremindme.api.dto.LancamentoEstatisticaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaDiaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaPessoa;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepositoryCustom {

    Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable);

    Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);

    List<LancamentoEstatisticaDTO> listPorCategoria(LocalDate mesReferencia);

    List<LancamentoEstatisticaDiaDTO> listPorDia(LocalDate mesReferencia);

    List<LancamentoEstatisticaPessoa> listPorPessoa(LocalDate inicio, LocalDate fim);
}
