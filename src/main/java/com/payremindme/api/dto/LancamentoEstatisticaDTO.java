package com.payremindme.api.dto;

import com.payremindme.api.model.Categoria;

import java.io.Serializable;
import java.math.BigDecimal;

public class LancamentoEstatisticaDTO implements Serializable {

    private static final long serialVersionUID = 1893202179319206130L;
    private BigDecimal total;
    private Categoria categoria;

    public LancamentoEstatisticaDTO(BigDecimal total, Categoria categoria) {
        this.total = total;
        this.categoria = categoria;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
