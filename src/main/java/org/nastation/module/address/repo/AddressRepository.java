package org.nastation.module.address.repo;

import org.nastation.module.address.data.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    Address findByLabel(String label);

    List<Address> findAllByOrderByIdDesc();
}