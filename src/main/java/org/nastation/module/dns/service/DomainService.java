package org.nastation.module.dns.service;

import org.nastation.module.dns.data.Domain;
import org.nastation.module.dns.repo.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class DomainService extends CrudService<Domain, Integer> {

    private DomainRepository repository;

    public DomainService(@Autowired DomainRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DomainRepository getRepository() {
        return repository;
    }

}
