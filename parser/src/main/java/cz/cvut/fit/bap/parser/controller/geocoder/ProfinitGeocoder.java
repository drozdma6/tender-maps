package cz.cvut.fit.bap.parser.controller.geocoder;

import cz.cvut.fit.bap.parser.controller.builder.AddressBuilder;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.domain.Address;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import kotlin.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/*
    Geocoding api client used for geocoding czech locations
 */
@Component
public class ProfinitGeocoder implements Geocoder{
    private static final String BASE_URL = "https://geolokator.profinit.cz";
    private static final String CZECH_SHORT_COUNTRY_CODE = "CZ";
    private static final String LANGUAGE_PARAM = "en";
    private static final long CONNECTION_TIMEOUT_SEC = 10;
    private static final long REQUEST_TIMEOUT_SEC = 20;

    private final HttpClient httpClient;

    @Value("${PROFINIT_API_KEY}")
    private String apiToken;

    public ProfinitGeocoder() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SEC))
                .build();
    }

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
            JSONObject response = sendQueryRequest(addressData);
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
     * Sends request to geocoder. In case of GeocodingException, function retries the API request.
     *
     * @param addressData which is supposed to be geocoded
     * @return Json response
     */
    @Retryable(retryFor = GeocodingException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private JSONObject sendQueryRequest(AddressData addressData) {
        String addressStr = String.join(" ", addressData.street(), addressData.buildingNumber(),
                addressData.postalCode(), addressData.city());
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path("query")
                .queryParam("token", apiToken)
                .queryParam("query", addressStr)
                .queryParam("lang", LANGUAGE_PARAM)
                .build()
                .toUri();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SEC))
                .GET()
                .build();
        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        }catch(IOException e){
            throw new GeocodingException(e);
        }catch(InterruptedException e){
            e.printStackTrace();
            Metrics.counter("scrapper.profinit.geocoder.failed").increment();
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Pair<Double, Double> getWgsCoordinates(JSONObject json) {
        try {
            JSONObject ruian = json.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("ruian");
            double wgsX = ruian.getDouble("wgs_x");
            double wgsY = ruian.getDouble("wgs_y");
            return new Pair<>(wgsX, wgsY);
        } catch (JSONException e) {
            throw new GeocodingException("Error parsing JSON for coordinates: " + e.getMessage());
        }
    }
}
