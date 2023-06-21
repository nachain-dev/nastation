package org.nastation.module.dfs.service;

import org.nastation.module.dfs.data.FileItem;
import org.nastation.module.dfs.repo.FileItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class FileItemService extends CrudService<FileItem, Integer> {

    private FileItemRepository repository;

    public FileItemService(@Autowired FileItemRepository repository) {
        this.repository = repository;
    }

    @Override
    protected FileItemRepository getRepository() {
        return repository;
    }

}
