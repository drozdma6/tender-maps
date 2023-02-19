package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Long>{
    Optional<Company> findCompanyByName(String name);
}
