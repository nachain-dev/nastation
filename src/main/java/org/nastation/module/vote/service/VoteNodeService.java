package org.nastation.module.vote.service;

import lombok.extern.slf4j.Slf4j;
import org.nastation.module.vote.data.VoteNode;
import org.nastation.module.vote.repo.VoteNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
@Slf4j
public class VoteNodeService extends CrudService<VoteNode, Integer> {

    private VoteNodeRepository repository;

    public VoteNodeService(@Autowired VoteNodeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected VoteNodeRepository getRepository() {
        return repository;
    }

}
