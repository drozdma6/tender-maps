package cz.cvut.fit.bap.parser.business;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Class for handling communication with Google's geocoding api.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GeocodingApiClient implements AutoCloseable{
    private final GeoApiContext context;


    public GeocodingApiClient() throws IOException{
        Properties properties = new Properties();
        properties.load(new FileReader("googleMapsApiKey.properties"));
        String apiKey = properties.getProperty("API_KEY");
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }


    /**
     * Send request to google's geocoding api with certain address
     *
     * @param address which is supposed to be geocoded
     * @return apis response
     */
    public GeocodingResult[] geocode(Address address){
        String addressStr =
                address.getBuildingNumber() + ' ' + address.getStreet() + ' ' + address.getCity() +
                ' ' + address.getCountryCode() + ' ' + address.getPostalCode();
        try{
            return GeocodingApi.geocode(context, addressStr).await();
        } catch (ApiException | InterruptedException | IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the result from geocoding API. Gets country shortcut from its official name in
     * language. E.g. for Česká republika returns CZ.
     *
     * @param results response from geocoding API
     * @return two letters country name or empty string
     */
    public String getCountryShortName(GeocodingResult[] results){
        AddressComponent[] addressComponents = results[0].addressComponents;
        for (AddressComponent addressComponent : addressComponents){
            for (int j = 0; j < addressComponent.types.length; j++){
                if (addressComponent.types[j].toString().equals("country")){
                    return addressComponent.shortName;
                }
            }
        }
        return "";
    }

    /**
     * Gets latitude from geocoding api result of address initialized by geocode methode
     *
     * @param results of geocoding api response
     * @return latitude
     */
    public double getLat(GeocodingResult[] results){
        return results[0].geometry.location.lat;
    }

    /**
     * Gets longitude from geocoding api result of certain address initialized by geocode methode
     *
     * @param results of geocoding api response
     * @return longitude
     */
    public double getLng(GeocodingResult[] results){
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
