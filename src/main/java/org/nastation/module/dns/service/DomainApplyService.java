package org.nastation.module.dns.service;

import org.nastation.module.dns.data.DomainApply;
import org.nastation.module.dns.repo.DomainApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class DomainApplyService extends CrudService<DomainApply, Integer> {

    private DomainApplyRepository repository;

    public DomainApplyService(@Autowired DomainApplyRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DomainApplyRepository getRepository() {
        return repository;
    }

}
