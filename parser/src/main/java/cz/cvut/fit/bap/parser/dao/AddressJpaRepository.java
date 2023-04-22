package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressJpaRepository extends JpaRepository<Address, Long>{
    /**
     * Finds matching address, comparing two null values as equal with IS NOT DISTINCT FROM
     *
     * @param address which supposed to be found
     * @return optional of address
     */
    @Query(value = "SELECT * FROM address WHERE " +
                   "building_number IS NOT DISTINCT FROM :#{#address.buildingNumber} " +
                   "AND city IS NOT DISTINCT FROM :#{#address.city} " +
                   "AND street IS NOT DISTINCT FROM :#{#address.street} " +
                   "AND postal_code IS NOT DISTINCT FROM :#{#address.postalCode}",
           nativeQuery = true)
    Optional<Address> readAddress(@Param("address") AddressDto address);
}
