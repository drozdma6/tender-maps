package cz.cvut.fit.bap.parser.controller.geocoder;

import cz.cvut.fit.bap.parser.controller.builder.AddressBuilder;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.domain.Address;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import kotlin.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/*
    Geocoding api client used for geocoding czech locations
 */
@Component
public class ProfinitGeocoder implements Geocoder{
    private static final String BASE_URL = "https://geolokator.profinit.cz";
    private static final String CZECH_SHORT_COUNTRY_CODE = "CZ";

    @Value("${PROFINIT_API_KEY}")
    private String apiToken;

    /**
     * Geocodes address by calling profinit geocoding api.
     *
     * @param addressData which is supposed to be geocoded
     * @return address with latitude and longitude
     */
    @Override
    @Timed(value = "scrapper.profinit.geocode")
    public Address geocode(AddressData addressData){
        AddressBuilder addressBuilder = new AddressBuilder(addressData);
        addressBuilder.countryCode(CZECH_SHORT_COUNTRY_CODE); //profinit geocoder is used only for czech places
        try{
            String response = sendQueryRequest(addressData);
            Pair<Double,Double> coordinates = getWgsCoordinates(response);
            addressBuilder.latitude(coordinates.getFirst()); //wgs_x
            addressBuilder.longitude(coordinates.getSecond()); //wgx_y
            return addressBuilder.build();
        }catch(GeocodingException e){
            e.printStackTrace();
            Metrics.counter("scrapper.profinit.geocoder.failed").increment();
            return addressBuilder.build();
        }
    }

    /**
     * Sends request to geocoder
     *
     * @param addressData which is supposed to be geocoded
     * @return Json response
     */
    private String sendQueryRequest(AddressData addressData){
        String addressStr = addressData.street() + ' ' + addressData.buildingNumber() + ", " +
                addressData.postalCode() + ", " + addressData.city();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        String queryUrl = BASE_URL + "/query?token=" + URLEncoder.encode(apiToken, StandardCharsets.UTF_8) +
                "&query=" + URLEncoder.encode(addressStr, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(queryUrl))
                .timeout(Duration.of(20, SECONDS))
                .GET()
                .build();

        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }catch(IOException e){
            throw new GeocodingException(e);
        }catch(InterruptedException e){
            e.printStackTrace();
            Metrics.counter("scrapper.profinit.geocoder.failed").increment();
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Pair<Double,Double> getWgsCoordinates(String jsonString){
        JSONObject json = new JSONObject(jsonString);
        JSONArray results = json.getJSONArray("results");

        if(results.isEmpty()){
            Metrics.counter("scrapper.profinit.geocoder.failed").increment();
            return new Pair<>(null, null);
        }
        JSONObject firstResult = results.getJSONObject(0);
        JSONObject ruian = firstResult.getJSONObject("ruian");

        try{
            return new Pair<>(ruian.getDouble("wgs_x"), ruian.getDouble("wgs_y"));
        }catch(JSONException e){
            Metrics.counter("scrapper.profinit.geocoder.failed").increment();
            //if wgs_x or wgs_y is null
            return new Pair<>(null, null);
        }
    }
}
