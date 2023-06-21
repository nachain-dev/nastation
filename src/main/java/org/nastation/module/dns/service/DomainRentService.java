package org.nastation.module.dns.service;

import org.nastation.module.dns.data.DomainRent;
import org.nastation.module.dns.repo.DomainRentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class DomainRentService extends CrudService<DomainRent, Integer> {

    private DomainRentRepository repository;

    public DomainRentService(@Autowired DomainRentRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DomainRentRepository getRepository() {
        return repository;
    }

}
