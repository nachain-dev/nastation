package org.nastation.module.dns.repo;

import org.nastation.module.dns.data.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Integer> {

}