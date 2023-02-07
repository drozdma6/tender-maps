package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementJpaRepository extends JpaRepository<Procurement, Long>{
}
