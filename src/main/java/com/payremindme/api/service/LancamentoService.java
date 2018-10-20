package com.payremindme.api.service;

import com.payremindme.api.dto.LancamentoEstatisticaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaDiaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaPessoa;
import com.payremindme.api.exception.PessoaInativaException;
import com.payremindme.api.exception.PessoaInexistenteException;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.LancamentoRepository;
import com.payremindme.api.repository.PessoaRepository;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LancamentoService {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.findAllByFilter(lancamentoFilter, pageable);
    }

    public List<Lancamento> findAll() {
        return lancamentoRepository.findAll();
    }

    public Lancamento find(Long codigo) {
        Lancamento lancamentoDb = lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return lancamentoDb;
    }

    public Lancamento save(Lancamento lancamento) {
        validarPessoa(lancamento);
        return lancamentoRepository.save(lancamento);
    }


    public Lancamento update(Long codigo, Lancamento lancamento) {
        Lancamento lancamentoDb = lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

        if (!lancamento.getPessoa().equals(lancamentoDb.getPessoa())) {
            validarPessoa(lancamento);
        }

        BeanUtils.copyProperties(lancamento, lancamentoDb, "codigo");
        return lancamentoRepository.save(lancamentoDb);
    }

    public void delete(Long codigo) {
        lancamentoRepository.deleteById(codigo);
    }

    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.resumir(lancamentoFilter, pageable);
    }

    private void validarPessoa(Lancamento lancamento) {
        Pessoa pessoaDb = pessoaRepository.findById(lancamento.getPessoa().getCodigo()).orElseThrow(PessoaInexistenteException::new);

        if (!pessoaDb.getAtivo()) {
            throw new PessoaInativaException();
        }
    }

    public List<LancamentoEstatisticaDTO> listPorCategoria() {
        return lancamentoRepository.listPorCategoria(LocalDate.now());
    }

    public List<LancamentoEstatisticaDiaDTO> listPorDia() {
        return lancamentoRepository.listPorDia(LocalDate.now());
    }

    public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws JRException {

        List<LancamentoEstatisticaPessoa> lancamentoEstatisticaPessoas = listPorPessoa(inicio, fim);
        Map<String,Object> parametros = new HashMap<>();
        parametros.put("DT_INICIO", Date.valueOf(inicio));
        parametros.put("DT_FIM", Date.valueOf(fim));
        parametros.put("REPORT_LOCALE", new Locale("pt","BR"));

        InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/lancamentos-por-pessoa.jasper");

        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream,parametros,
                new JRBeanCollectionDataSource(lancamentoEstatisticaPessoas));

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private List<LancamentoEstatisticaPessoa> listPorPessoa(LocalDate inicio, LocalDate fim) {
        return lancamentoRepository.listPorPessoa(inicio, fim);
    }
}
