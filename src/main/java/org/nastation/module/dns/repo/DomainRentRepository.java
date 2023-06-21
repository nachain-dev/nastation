package org.nastation.module.dns.repo;

import org.nastation.module.dns.data.DomainRent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRentRepository extends JpaRepository<DomainRent, Integer> {

}