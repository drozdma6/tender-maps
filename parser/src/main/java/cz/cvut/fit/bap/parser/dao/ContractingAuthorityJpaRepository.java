package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractingAuthorityJpaRepository extends JpaRepository<ContractingAuthority, Long> {
    Optional<ContractingAuthority> findContractingAuthorityByName(String name);
}