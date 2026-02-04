package es.udc.asi.bnbria_rest.common.images;

import es.udc.asi.bnbria_rest.common.exceptions.model.ModelException;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

  String saveImage(MultipartFile file, Long id) throws ModelException;

  ImageDto getImagen(Long id, String imagen) throws ModelException;

  void deleteImage(Long id, String nombreImagen) throws ModelException;
}
