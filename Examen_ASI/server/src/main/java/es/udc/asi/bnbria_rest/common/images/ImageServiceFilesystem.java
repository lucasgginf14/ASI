package es.udc.asi.bnbria_rest.common.images;

import es.udc.asi.bnbria_rest.common.config.Properties;
import es.udc.asi.bnbria_rest.common.exceptions.model.ModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class ImageServiceFilesystem implements ImageService {

  @Autowired
  private Properties properties;

  private Path rootLocation;

  @Override
  public String saveImage(MultipartFile file, Long id) throws ModelException {
    long maxSize = 50 * 1024 * 1024;
    if (file.getSize() > maxSize) {
      throw new ModelException("La imagen supera el tamaño máximo permitido de 2 MB");
    }

    String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    try (InputStream is = file.getInputStream()) {
      Files.copy(is, getRootLocation().resolve(id + getExtension(filename)), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      throw new ModelException("Se ha producido algún error procesando la imagen");
    }
    return filename;
  }

  private String getExtension(String filename) {
    return filename.substring(filename.lastIndexOf("."));
  }

  private Path getRootLocation() {
    if (rootLocation == null) {
      this.rootLocation = Paths.get(properties.getRutaImagenes());
      try {
        Files.createDirectories(this.rootLocation);
      } catch (IOException e) {
        throw new RuntimeException("No se puede crear el directorio de imágenes", e);
      }
    }
    return this.rootLocation;
  }

  @Override
  public ImageDto getImagen(Long id, String nombreImagen) throws ModelException {
    try {
      InputStream is = new FileInputStream(properties.getRutaImagenes() + id + getExtension(nombreImagen));

      byte[] buffer = new byte[1024];
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      int len;
      while ((len = is.read(buffer)) > -1) {
        os.write(buffer, 0, len);
      }
      InputStream imageIs = new ByteArrayInputStream(os.toByteArray());
      os.flush();
      is.close();

      return new ImageDto(nombreImagen, getImageMediaType(nombreImagen), imageIs);
    } catch (IOException e) {
      e.printStackTrace();
      throw new ModelException("se ha producido algún error al recuperar la imagen");
    }
  }

  private String getImageMediaType(String nombreImagen) {
    String extension = getExtension(nombreImagen);
    return switch (extension.toLowerCase()) {
      case ".png" -> MediaType.IMAGE_PNG_VALUE;
      case ".gif" -> MediaType.IMAGE_GIF_VALUE;
      default -> MediaType.IMAGE_JPEG_VALUE;
    };
  }

  @Override
  public void deleteImage(Long id, String nombreImagen) throws ModelException {
    String extension;
    try {
      extension = getExtension(nombreImagen);
    } catch (Exception e) {
      throw new ModelException("Nombre de imagen inválido");
    }

    Path path = getRootLocation().resolve(id + extension);
    try {
      if (!Files.exists(path)) {
        throw new ModelException("La imagen no existe");
      }
      Files.delete(path);
    } catch (IOException e) {
      e.printStackTrace();
      throw new ModelException("Se ha producido algún error eliminando la imagen");
    }
  }
}
