package es.udc.asi.bnbria_rest.property.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class PropertySyncService {

  private final RestTemplate rest = new RestTemplate();
  private final ObjectMapper mapper = new ObjectMapper();

  public record Coordinates(
    Double lat,
    Double lon
  ) {
  }

  public Coordinates lookupCoordinates(
    String street,
    String city,
    String province,
    String country,
    Long postalCode) throws Exception {

    URI uri = UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search")
      .queryParam("format", "json")
      .queryParam("street", street)
      .queryParam("village", city)
      .queryParam("province", province)
      .queryParam("country", country)
      .queryParam("postalcode", postalCode)
      .queryParam("limit", "1")
      .build()
      .encode()
      .toUri();

    ResponseEntity<String> response = rest.getForEntity(uri, String.class);
    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      return null;
    }

    JsonNode arr = mapper.readTree(response.getBody());
    if (arr.isArray() && !arr.isEmpty()) {
      JsonNode first = arr.get(0);
      Double lat = first.has("lat") ? first.get("lat").asDouble() : null;
      Double lon = first.has("lon") ? first.get("lon").asDouble() : null;
      if (lat != null && lon != null) {
        return new Coordinates(lat, lon);
      }
    }
    return null;
  }
}
