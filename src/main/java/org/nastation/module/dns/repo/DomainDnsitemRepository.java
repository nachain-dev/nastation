package org.nastation.module.dns.repo;

import org.nastation.module.dns.data.DomainDnsItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainDnsitemRepository extends JpaRepository<DomainDnsItem, Integer> {

}