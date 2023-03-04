package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.domain.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class GeoLocationService extends AbstractCreateService<GeoLocation, Long>{
    public GeoLocationService(JpaRepository<GeoLocation, Long> repository){
        super(repository);
    }

    @Override
    public GeoLocation create(GeoLocation entity){
        //Gets rid of throwing exception if entity with same id is already stored
        //it is unwanted behavior while sharing same primary key in one to one
        if (repository.existsById(entity.getId())){
            return entity;
        }
        return repository.save(entity);
    }
}
