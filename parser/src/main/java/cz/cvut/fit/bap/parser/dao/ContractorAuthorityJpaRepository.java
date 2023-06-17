package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractorAuthorityJpaRepository extends JpaRepository<ContractorAuthority, Long>{
    Optional<ContractorAuthority> findContractorAuthorityByName(String name);
}