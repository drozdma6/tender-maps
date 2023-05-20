package cz.cvut.fit.bap.parser.controller.Geocoder;

import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.converter.AddressDtoToAddress;
import cz.cvut.fit.bap.parser.controller.fetcher.NenNipezFetcher;
import cz.cvut.fit.bap.parser.domain.Address;
import io.micrometer.core.annotation.Timed;
import kotlin.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

/*
    Geocoding api client used for geocoding czech locations
 */
@Component
public class ProfinitGeocoding implements Geocoder{
    private final String baseUrl = "https://geolokator.profinit.cz";
    private final String czechShortCountryCode = "CZ";
    private static final Logger LOGGER = LoggerFactory.getLogger(NenNipezFetcher.class);

    @Value("${profinitApiToken}")
    private String apiToken;
    private final WebClient webClient;
    private final AddressDtoToAddress addressDtoToAddress;

    public ProfinitGeocoding(AddressDtoToAddress addressDtoToAddress,
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
    @Timed(value = "scrapper.profinit.geocode")
    public Address geocode(AddressDto addressDto){
        Address address = addressDtoToAddress.apply(addressDto);
        address.setCountryCode(czechShortCountryCode); //profinit geocoder is used only for czech places
        String response = sendQueryRequest(addressDto);
        Pair<Double,Double> coordinates = getWgsCoordinates(response);
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
        String addressStr = addressDto.street() + ' ' + addressDto.buildingNumber() + ", " +
                addressDto.postalCode() + ", " + addressDto.city();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/query").queryParam("token", apiToken)
                        .queryParam("query", addressStr).build())
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                        .doAfterRetry(rs -> LOGGER.warn("Retrying profinit geocoding request.")))
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

        try{
            return new Pair<>(ruian.getDouble("wgs_x"), ruian.getDouble("wgs_y"));
        }catch(JSONException e){
            //if wgs_x or wgs_y is null
            return new Pair<>(null, null);
        }
    }
}
