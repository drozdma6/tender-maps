package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService extends AbstractService<Company, Long>{
    protected CompanyService(JpaRepository<Company,Long> repository){
        super(repository);
    }
}
