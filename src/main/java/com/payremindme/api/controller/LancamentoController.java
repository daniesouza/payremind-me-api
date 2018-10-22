package com.payremindme.api.controller;

import com.payremindme.api.dto.Anexo;
import com.payremindme.api.dto.LancamentoEstatisticaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaDiaDTO;
import com.payremindme.api.event.RecursoCriadoEvent;
import com.payremindme.api.exception.PessoaInativaException;
import com.payremindme.api.exception.PessoaInexistenteException;
import com.payremindme.api.exceptionhandler.CustomExceptionHandler;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import com.payremindme.api.service.LancamentoService;
import com.payremindme.api.storage.StorageS3;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private StorageS3 storageS3;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read') ")
    public Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoService.findAllByFilter(lancamentoFilter, pageable);
    }

    @GetMapping(params = "resumo")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read') ")
    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoService.resumir(lancamentoFilter, pageable);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write') ")
    public ResponseEntity<Lancamento> save(@RequestBody @Valid Lancamento lancamento, HttpServletResponse response) {
        Lancamento lancamentoDb = lancamentoService.save(lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoDb.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoDb);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read') ")
    public ResponseEntity<Lancamento> find(@PathVariable Long codigo) {
        Lancamento lancamentoDb = lancamentoService.find(codigo);
        return ResponseEntity.ok(lancamentoDb);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write') ")
    public ResponseEntity<Lancamento> update(@PathVariable Long codigo, @RequestBody @Valid Lancamento lancamento) {
        return ResponseEntity.status(HttpStatus.OK).body(lancamentoService.update(codigo, lancamento));
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write') ")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long codigo) {
        lancamentoService.delete(codigo);
    }


    @ExceptionHandler({PessoaInativaException.class, PessoaInexistenteException.class})
    protected ResponseEntity<Object> handlePessoaException(RuntimeException ex, WebRequest webRequest) {

        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
        String mensagemTecnica = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<CustomExceptionHandler.ErrorMessage> errors = Collections.singletonList(new CustomExceptionHandler.ErrorMessage(mensagemUsuario, mensagemTecnica));

        return ResponseEntity.badRequest().body(errors);

    }

    @GetMapping("estatistica/categoria")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read') ")
    public List<LancamentoEstatisticaDTO> listPorCategoria() {
        return this.lancamentoService.listPorCategoria();
    }

    @GetMapping("estatistica/dia")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read') ")
    public List<LancamentoEstatisticaDiaDTO> listPorDia() {
        return this.lancamentoService.listPorDia();
    }

    @GetMapping("estatistica/relatorio")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read') ")
    public ResponseEntity<byte[]> relatorioPorPessoa(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws JRException {

        byte[] relatorioBytes = this.lancamentoService.relatorioPorPessoa(inicio, fim);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(relatorioBytes);
    }

    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write') ")
    @PostMapping("/anexo")
    public Anexo uploadAnexo(@RequestParam MultipartFile anexo) {
        return storageS3.saveTemporary(anexo);
    }
}
