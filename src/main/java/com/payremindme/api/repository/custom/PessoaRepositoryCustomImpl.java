package com.payremindme.api.repository.custom;

import com.payremindme.api.model.Pessoa;
import com.payremindme.api.repository.filter.PessoaFilter;
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
import java.util.ArrayList;
import java.util.List;

public class PessoaRepositoryCustomImpl implements PessoaRepositoryCustom {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Pessoa> findAllByFilter(PessoaFilter pessoaFilter, Pageable pageable) {

        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Pessoa> criteria = criteriaBuilder.createQuery(Pessoa.class);
        Root<Pessoa> root = criteria.from(Pessoa.class);

        Predicate[] predicates = criarRestricoes(pessoaFilter,criteriaBuilder,root);
        criteria.where(predicates);

        TypedQuery<Pessoa> query = manager.createQuery(criteria);

        adicionaRestricaoPaginacao(query,pageable);

        return new PageImpl<>(query.getResultList(),pageable,total(pessoaFilter));
    }


    private Long total(PessoaFilter pessoaFilter) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Pessoa> root = criteria.from(Pessoa.class);

        Predicate[] predicates = criarRestricoes(pessoaFilter,builder,root);
        criteria.where(predicates);
        criteria.select(builder.count(root));

       return manager.createQuery(criteria).getSingleResult();
    }

    private void adicionaRestricaoPaginacao(TypedQuery<?> query, Pageable pageable) {
        adiciona(query, pageable);

    }

    static void adiciona(TypedQuery<?> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPagina = pageable.getPageSize();
        int primeiroRegistroPagina = paginaAtual * totalRegistrosPagina;

        query.setFirstResult(primeiroRegistroPagina);
        query.setMaxResults(totalRegistrosPagina);
    }

    private Predicate[] criarRestricoes(PessoaFilter pessoaFilter, CriteriaBuilder criteriaBuilder, Root<Pessoa> root) {

        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(pessoaFilter.getNome())){
            predicates.add(criteriaBuilder.like(
                      criteriaBuilder.lower(root.get("nome")),"%"+pessoaFilter.getNome().toLowerCase() +"%"
                    )
            );
        }

        return predicates.toArray(new Predicate[0]);
    }
}
