package es.udc.asi.bnbria_rest.propertyreview.controller;

import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.propertyreview.service.PropertyReviewService;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewAdminView;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewCreateRequest;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewRef;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/property-reviews")
public class PropertyReviewResource {

  @Autowired
  private PropertyReviewService propertyReviewService;

  @PutMapping("/{id}")
  public PropertyReviewView publishReview(
    @PathVariable Long id,
    @RequestBody PropertyReviewCreateRequest request
  ) throws NotFoundException {
    return propertyReviewService.create(id, request);
  }

  @DeleteMapping("/admin/{id}")
  public void deleteReview(@PathVariable Long id) throws NotFoundException {
    propertyReviewService.deleteReviewById(id);
  }

  @GetMapping("/pending")
  public List<PropertyReviewRef> findMyPendingReviews() throws NotFoundException {
    return propertyReviewService.findPendingReviewsByReviewer();
  }

  @GetMapping("/admin")
  public List<PropertyReviewAdminView> findAll() {
    return propertyReviewService.findAll();
  }
}
