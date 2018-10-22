package com.payremindme.api.dto;

import com.payremindme.api.model.Categoria;
import com.payremindme.api.model.TipoLancamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoEstatisticaDiaDTO implements Serializable {

    private TipoLancamento tipo;
    private LocalDate dia;
    private BigDecimal total;

    public LancamentoEstatisticaDiaDTO(TipoLancamento tipo, LocalDate dia, BigDecimal total) {
        this.tipo = tipo;
        this.dia = dia;
        this.total = total;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}