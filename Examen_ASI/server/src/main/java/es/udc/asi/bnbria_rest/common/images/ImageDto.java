package es.udc.asi.bnbria_rest.common.images;

import java.io.InputStream;

public record ImageDto(
  String filename,
  String mimeType,
  InputStream content
) {
}
