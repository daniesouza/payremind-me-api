package com.payremindme.api.repository.custom;

import com.payremindme.api.dto.LancamentoEstatisticaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaDiaDTO;
import com.payremindme.api.dto.LancamentoEstatisticaPessoa;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.repository.filter.LancamentoFilter;
import com.payremindme.api.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LancamentoRepositoryCustomImpl implements LancamentoRepositoryCustom {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Lancamento> findAllByFilter(LancamentoFilter lancamentoFilter, Pageable pageable) {

        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Lancamento> criteria = criteriaBuilder.createQuery(Lancamento.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        Predicate[] predicates = criarRestricoes(lancamentoFilter, criteriaBuilder, root);
        criteria.where(predicates);

        TypedQuery<Lancamento> query = manager.createQuery(criteria);

        adicionaRestricaoPaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    @Override
    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {

        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<ResumoLancamento> criteria = criteriaBuilder.createQuery(ResumoLancamento.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(criteriaBuilder.construct(ResumoLancamento.class,
                root.get("codigo"),
                root.get("descricao"),
                root.get("dataVencimento"),
                root.get("dataPagamento"),
                root.get("valor"),
                root.get("tipo"),
                root.get("categoria").get("nome"),
                root.get("pessoa").get("nome")));

        Predicate[] predicates = criarRestricoes(lancamentoFilter, criteriaBuilder, root);
        criteria.where(predicates);

        TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);

        adicionaRestricaoPaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    @Override
    public List<LancamentoEstatisticaDTO> listPorCategoria(LocalDate mesReferencia) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaDTO> criteria = criteriaBuilder.createQuery(LancamentoEstatisticaDTO.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(criteriaBuilder.construct(LancamentoEstatisticaDTO.class,
                criteriaBuilder.sum(root.get("valor")),
                root.get("categoria")));

        LocalDate primeiroDiaMes = mesReferencia.withDayOfMonth(1);
        LocalDate ultimoDiaMes = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

        criteria.where(criteriaBuilder.greaterThanOrEqualTo(
                root.get("dataVencimento"), primeiroDiaMes),
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("dataVencimento"), ultimoDiaMes));

        criteria.groupBy(root.get("categoria"));

        TypedQuery<LancamentoEstatisticaDTO> query = manager.createQuery(criteria);


        return query.getResultList();

    }

    @Override
    public List<LancamentoEstatisticaDiaDTO> listPorDia(LocalDate mesReferencia) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaDiaDTO> criteria = criteriaBuilder.createQuery(LancamentoEstatisticaDiaDTO.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(criteriaBuilder.construct(LancamentoEstatisticaDiaDTO.class,
                root.get("tipo"),
                root.get("dataVencimento"),
                criteriaBuilder.sum(root.get("valor"))));

        LocalDate primeiroDiaMes = mesReferencia.withDayOfMonth(1);
        LocalDate ultimoDiaMes = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

        criteria.where(criteriaBuilder.greaterThanOrEqualTo(
                root.get("dataVencimento"), primeiroDiaMes),
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("dataVencimento"), ultimoDiaMes));

        criteria.groupBy(root.get("tipo"),root.get("dataVencimento"));

        TypedQuery<LancamentoEstatisticaDiaDTO> query = manager.createQuery(criteria);


        return query.getResultList();

    }

    @Override
    public List<LancamentoEstatisticaPessoa> listPorPessoa(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaPessoa> criteria = criteriaBuilder.createQuery(LancamentoEstatisticaPessoa.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(criteriaBuilder.construct(LancamentoEstatisticaPessoa.class,
                root.get("tipo"),
                root.get("pessoa"),
                criteriaBuilder.sum(root.get("valor"))));


        criteria.where(criteriaBuilder.greaterThanOrEqualTo(
                root.get("dataVencimento"), inicio),
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("dataVencimento"), fim));

        criteria.groupBy(root.get("tipo"),root.get("pessoa"));

        TypedQuery<LancamentoEstatisticaPessoa> query = manager.createQuery(criteria);


        return query.getResultList();

    }

    private Long total(LancamentoFilter lancamentoFilter) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
        criteria.where(predicates);
        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private void adicionaRestricaoPaginacao(TypedQuery<?> query, Pageable pageable) {
        PessoaRepositoryCustomImpl.adiciona(query, pageable);

    }

    private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder criteriaBuilder, Root<Lancamento> root) {

        List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("descricao")), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"
                    )
            );
        }

        if (lancamentoFilter.getDataVencimentoDe() != null) {

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("dataVencimento"), lancamentoFilter.getDataVencimentoDe()
                    )
            );
        }

        if (lancamentoFilter.getDataVencimentoAte() != null) {

            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("dataVencimento"), lancamentoFilter.getDataVencimentoAte()
                    )
            );

        }

        return predicates.toArray(new Predicate[0]);
    }
}
