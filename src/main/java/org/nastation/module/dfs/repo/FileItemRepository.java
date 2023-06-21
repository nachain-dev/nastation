package org.nastation.module.dfs.repo;

import org.nastation.module.dfs.data.FileItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileItemRepository extends JpaRepository<FileItem, Integer> {

}