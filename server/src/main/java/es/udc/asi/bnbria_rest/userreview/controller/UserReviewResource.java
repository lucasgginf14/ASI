package es.udc.asi.bnbria_rest.userreview.controller;

import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.userreview.service.UserReviewService;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewAdminView;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewCreateRequest;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewRef;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewView;
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
@RequestMapping("/api/user-reviews")
public class UserReviewResource {

  @Autowired
  private UserReviewService userReviewService;

  @PutMapping("/{id}")
  public UserReviewView publishReview(
    @PathVariable Long id,
    @RequestBody UserReviewCreateRequest request
  ) throws NotFoundException {
    return userReviewService.create(id, request);
  }

  @DeleteMapping("/admin/{id}")
  public void deleteReview(@PathVariable Long id) throws NotFoundException {
    userReviewService.deleteReviewById(id);
  }

  @GetMapping("/pending")
  public List<UserReviewRef> findMyPendingReviews() throws NotFoundException {
    return userReviewService.findPendingReviewsByReviewer();
  }

  @GetMapping("/admin")
  public List<UserReviewAdminView> findAll() {
    return userReviewService.findAll();
  }

}
