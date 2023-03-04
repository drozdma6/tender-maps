package cz.cvut.fit.bap.parser.business.scrapper;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

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


    public GeocodingResult[] geocode(String buildingNumber, String street, String city,
                                     String state, String postalCode){
        String address =
                buildingNumber + ' ' + street + ' ' + city + ' ' + state + ' ' + postalCode;
        try{
            return GeocodingApi.geocode(context, address).await();
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

    public double getLat(GeocodingResult[] results){
        return results[0].geometry.location.lat;
    }

    public double getLng(GeocodingResult[] results){
        return results[0].geometry.location.lng;
    }

    @Override
    public void close(){
        context.shutdown();
    }
}
