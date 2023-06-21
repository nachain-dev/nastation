package org.nastation.module.vote.repo;

import org.nastation.module.vote.data.VoteNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteNodeRepository extends JpaRepository<VoteNode, Integer> {

}