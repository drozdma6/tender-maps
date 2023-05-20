package cz.cvut.fit.bap.parser.controller.Geocoder;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.converter.AddressDtoToAddress;
import cz.cvut.fit.bap.parser.domain.Address;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class for handling communication with Google's geocoding api.
 */
@Component
public class GoogleGeocoding implements Geocoder, AutoCloseable{
    private final GeoApiContext context;
    private final AddressDtoToAddress addressDtoToAddress;

    public GoogleGeocoding(@Value("${googleApiKey:}") final String apiKey,
                           AddressDtoToAddress addressDtoToAddress){
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();

        this.addressDtoToAddress = addressDtoToAddress;
    }

    /**
     * Geocodes addressDto
     *
     * @param addressDto addressDto which is supposed to be geocoded
     * @return address with latitude, longitude and country code
     */
    @Override
    @Timed(value = "scrapper.google.geocode")
    public Address geocode(AddressDto addressDto){
        GeocodingResult[] geocodingResults = sendRequest(addressDto);
        Address address = addressDtoToAddress.apply(addressDto);
        if(address.getCountryCode() == null){
            address.setCountryCode(getCountryCode(geocodingResults));
        }
        address.setLongitude(getLongitude(geocodingResults));
        address.setLatitude(getLatitude(geocodingResults));
        return address;
    }

    private GeocodingResult[] sendRequest(AddressDto addressDto){
        String addressStr = addressDto.buildingNumber() + ' ' + addressDto.street() + ' ' +
                addressDto.city() + ' ' + addressDto.postalCode() + ' ' +
                addressDto.country();
        try{
            return GeocodingApi.geocode(context, addressStr).await();
        }catch(ApiException | InterruptedException | IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets country code from GeocodingResult
     *
     * @param results array of geocodingResult
     * @return two letters country code on success, null otherwise
     */
    private String getCountryCode(GeocodingResult[] results){
        AddressComponent[] addressComponents = results[0].addressComponents;
        for(AddressComponent addressComponent : addressComponents){
            for (int j = 0; j < addressComponent.types.length; j++){
                if (addressComponent.types[j].toString().equals("country")){
                    return addressComponent.shortName;
                }
            }
        }
        return null;
    }

    /**
     * Gets latitude from geocodingResult
     *
     * @param results array of geocodingResult
     * @return latitude
     */
    private double getLatitude(GeocodingResult[] results){
        return results[0].geometry.location.lat;
    }

    /**
     * Gets longitude from geocodingResult
     *
     * @param results array of geocodingResult
     * @return longitude
     */
    private double getLongitude(GeocodingResult[] results){
        return results[0].geometry.location.lng;
    }

    /**
     * Shuts down geocoding api context after all request were sent
     */
    @Override
    public void close(){
        context.shutdown();
    }
}
