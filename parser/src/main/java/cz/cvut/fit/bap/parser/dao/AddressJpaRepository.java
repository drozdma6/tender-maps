package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressJpaRepository extends JpaRepository<Address, Long>{
    @Query(value = "SELECT * FROM address WHERE building_number=:#{#address.buildingNumber} " +
                   "AND city=:#{#address.city} " + "AND street=:#{#address.street} " +
                   "AND country_code=:#{#address.countryCode} " +
                   "AND postal_code=:#{#address.postalCode}", nativeQuery = true)
    Optional<Address> readAddress(@Param("address") Address address);
}
