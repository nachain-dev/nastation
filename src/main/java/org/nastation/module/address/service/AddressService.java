package org.nastation.module.address.service;

import org.nastation.module.address.data.Address;
import org.nastation.module.address.repo.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class AddressService extends CrudService<Address, Integer> {

    private AddressRepository repository;

    public AddressService(@Autowired AddressRepository repository) {
        this.repository = repository;
    }

    @Override
    public AddressRepository getRepository() {
        return repository;
    }

}
