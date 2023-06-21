package org.nastation.module.vote.repo;

import org.nastation.module.vote.data.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

}