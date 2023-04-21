package cz.cvut.fit.bap.parser.business.Geocoder;

import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.dto.converter.AddressDtoToAddress;
import kotlin.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

/*
    Geocoding api client used for geocoding czech locations
 */
@Component
public class ProfinitGeocodingApi implements Geocoder{
    private final String baseUrl = "https://geolokator.profinit.cz";
    private final String czechShortCountryCode = "CZ";

    @Value("${profinitApiToken}")
    private String apiToken;
    private final WebClient webClient;
    private final AddressDtoToAddress addressDtoToAddress;

    public ProfinitGeocodingApi(AddressDtoToAddress addressDtoToAddress,
                                WebClient.Builder webClientBuilder){
        this.addressDtoToAddress = addressDtoToAddress;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Geocodes address by calling profinit geocoding api.
     *
     * @param addressDto which is supposed to be geocoded
     * @return address with latitude and longitude
     */
    @Override
    public Address geocode(AddressDto addressDto){
        Address address = addressDtoToAddress.apply(addressDto);
        address.setCountryCode(
                czechShortCountryCode); //profinit geocoder is used only for czech places
        Pair<Double, Double> coordinates = getWgsCoordinates(sendQueryRequest(addressDto));
        address.setLatitude(coordinates.getFirst()); //wgs_x
        address.setLongitude(coordinates.getSecond()); //wgx_y
        return address;
    }

    /**
     * Sends request to geocoder with exponential backoff
     *
     * @param addressDto which is supposed to be geocoded
     * @return Json response
     */
    private String sendQueryRequest(AddressDto addressDto){
        String addressStr = addressDto.getStreet() + ' ' + addressDto.getBuildingNumber() + ", " +
                            addressDto.getPostalCode() + ", " + addressDto.getCity();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/query").queryParam("token", apiToken)
                        .queryParam("query", addressStr).build()).retrieve()
                .bodyToMono(String.class).retryWhen(Retry.backoff(5, Duration.ofSeconds(2)))
                .block();
    }

    private Pair<Double, Double> getWgsCoordinates(String jsonString){
        JSONObject json = new JSONObject(jsonString);
        JSONArray results = json.getJSONArray("results");

        if (results.isEmpty()){
            return new Pair<>(null, null);
        }
        JSONObject firstResult = results.getJSONObject(0);
        JSONObject ruian = firstResult.getJSONObject("ruian");
        return new Pair<>(ruian.getDouble("wgs_x"), ruian.getDouble("wgs_y"));
    }
}
