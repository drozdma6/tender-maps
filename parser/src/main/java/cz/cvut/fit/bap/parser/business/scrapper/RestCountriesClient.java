package cz.cvut.fit.bap.parser.business.scrapper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class RestCountriesClient{
    private final String baseUrl = "https://restcountries.com/v3.1/translation/";

    /**
     * Encodes value to uri encoding with UTF-8. Required for values like Česká republika
     *
     * @param value value which is supposed to be encoded
     * @return encoded string to UTF_8
     */
    private String encodeValue(String value){
        return UriUtils.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Gets country shortcut from its official name in language. E.g. for Česká republika returns CZ.
     *
     * @param countryOfficialName in any supported translation
     * @return 2 letters country shortcut
     */
    public String getCountryShortcut(String countryOfficialName) throws IOException{
        URL url = new URL(baseUrl + encodeValue(countryOfficialName));

        String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(json);
        return jsonArray.getJSONObject(0).get("cca2").toString();
    }
}
