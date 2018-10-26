package com.payremindme.api.service;

import com.payremindme.api.amazon.SNSPublisher;
import com.payremindme.api.dto.LancamentoEstatisticaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaDiaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaPessoa;
import com.payremindme.api.exception.PessoaInativaException;
import com.payremindme.api.exception.PessoaInexistenteException;
import com.payremindme.api.mail.Mailer;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.model.Pessoa;
import com.payremindme.api.model.Usuario;
import com.payremindme.api.repository.LancamentoRepository;
import com.payremindme.api.repository.PessoaRepository;
import com.payremindme.api.repository.UsuarioRepository;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import com.payremindme.api.amazon.StorageS3;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LancamentoService {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private Mailer mailer;

    @Autowired
    private StorageS3 storageS3;

    @Autowired
    private SNSPublisher snsPublisher;

    public Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.findAllByFilter(lancamentoFilter, pageable);
    }

    public List<Lancamento> findAll() {
        return lancamentoRepository.findAll();
    }

    public Lancamento find(Long codigo) {
        return lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
    }

    public Lancamento save(Lancamento lancamento) {
        validarPessoa(lancamento);

        if (StringUtils.hasText(lancamento.getAnexo())) {
            storageS3.save(lancamento.getAnexo());
        }

        return lancamentoRepository.save(lancamento);
    }


    public Lancamento update(Long codigo, Lancamento lancamento) {
        Lancamento lancamentoDb = lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

        if (!lancamento.getPessoa().equals(lancamentoDb.getPessoa())) {
            validarPessoa(lancamento);
        }

        if (StringUtils.isEmpty(lancamento.getAnexo()) && StringUtils.hasText(lancamentoDb.getAnexo())) {
            storageS3.delete(lancamentoDb.getAnexo());
        } else if (StringUtils.hasText(lancamento.getAnexo())
                && !lancamento.getAnexo().equals(lancamentoDb.getAnexo())) {
            storageS3.update(lancamentoDb.getAnexo(), lancamento.getAnexo());
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
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("DT_INICIO", Date.valueOf(inicio));
        parametros.put("DT_FIM", Date.valueOf(fim));
        parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));

        InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/lancamentos-por-pessoa.jasper");

        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
                new JRBeanCollectionDataSource(lancamentoEstatisticaPessoas));

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private List<LancamentoEstatisticaPessoa> listPorPessoa(LocalDate inicio, LocalDate fim) {
        return lancamentoRepository.listPorPessoa(inicio, fim);
    }


    // @Scheduled(fixedDelay = 1000*60*30)
    @Schedules({
            @Scheduled(cron = "00 00 18 * * *"),
            @Scheduled(cron = "00 00 19 * * *"),
            @Scheduled(cron = "00 00 20 * * *")
    })
    public void avisarLancamentoVencido() {

        if (logger.isDebugEnabled()) {
            logger.debug("Preparando envio de lancamentos vencidos");
        }


        List<Lancamento> vencidos = lancamentoRepository.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
        List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao("ROLE_PESQUISAR_LANCAMENTO");

        if (vencidos.isEmpty()) {
            logger.info("Sem lancamentos vencidos para envio");
            return;
        }

        logger.info("Existem {} lancamentos vencidos", vencidos.size());

        if (destinatarios.isEmpty()) {
            logger.warn("Sem destinatarios cadastrados para envio de email");
            return;
        }

        enviarEmailLancamentoVencido(vencidos, destinatarios);
        enviarSMSLancamentoVencido(vencidos, destinatarios);
    }

    private void enviarSMSLancamentoVencido(List<Lancamento> vencidos, List<Usuario> destinatarios) {
        logger.info("Enviando mensagem de SMS  para {} destinatarios ", destinatarios.size());
        snsPublisher.enviarSMSLancamentosVencidos(vencidos, destinatarios);
        logger.info("Mensagem de SMS enviada com sucesso para {} destinatarios", destinatarios.size());
    }

    private void enviarEmailLancamentoVencido(List<Lancamento> vencidos, List<Usuario> destinatarios) {
        logger.info("Enviando de email de lancamentos vencidos enviado para {} destinatarios", destinatarios.size());
        mailer.enviarEmailLancamentosVencidos(vencidos, destinatarios);
        logger.info("Email enviado com sucesso para {} destinatarios", destinatarios.size());
    }
}
