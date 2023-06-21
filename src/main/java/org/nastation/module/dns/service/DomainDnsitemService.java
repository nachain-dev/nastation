package org.nastation.module.dns.service;

import org.nastation.module.dns.data.DomainDnsItem;
import org.nastation.module.dns.repo.DomainDnsitemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class DomainDnsitemService extends CrudService<DomainDnsItem, Integer> {

    private DomainDnsitemRepository repository;

    public DomainDnsitemService(@Autowired DomainDnsitemRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DomainDnsitemRepository getRepository() {
        return repository;
    }

}
