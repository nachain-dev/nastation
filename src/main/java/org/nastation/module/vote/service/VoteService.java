package org.nastation.module.vote.service;

import lombok.extern.slf4j.Slf4j;
import org.nastation.module.vote.data.Vote;
import org.nastation.module.vote.repo.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
@Slf4j
public class VoteService extends CrudService<Vote, Integer> {

    private VoteRepository repository;

    public VoteService(@Autowired VoteRepository repository) {
        this.repository = repository;
    }

    @Override
    protected VoteRepository getRepository() {
        return repository;
    }

}
