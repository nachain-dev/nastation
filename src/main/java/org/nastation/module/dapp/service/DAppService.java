package org.nastation.module.dapp.service;

import org.nastation.module.dapp.data.DApp;
import org.nastation.module.dapp.repo.DAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class DAppService extends CrudService<DApp, Integer> {

    private DAppRepository repository;

    public DAppService(@Autowired DAppRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DAppRepository getRepository() {
        return repository;
    }

}
