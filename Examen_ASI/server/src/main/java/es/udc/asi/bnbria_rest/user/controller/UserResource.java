package es.udc.asi.bnbria_rest.user.controller;

import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminRef;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminView;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPublic;
import es.udc.asi.bnbria_rest.user.service.dto.UserOwnerView;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicView;
import es.udc.asi.bnbria_rest.user.service.dto.UserUpdatePasswordRequest;
import es.udc.asi.bnbria_rest.user.service.dto.UserUpdateRequest;
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
@RequestMapping("/api/users")
public class UserResource {

  @Autowired
  private UserService userService;

  @GetMapping("/admin")
  public List<UserAdminRef> findAll() {
    return userService.findAll();
  }

  @GetMapping("/admin/search-email/{email}")
  public UserAdminView findByEmail(@PathVariable String email) throws NotFoundException {
    return userService.findByEmail(email);
  }

  @GetMapping("/{id}/public")
  public UserPublicView findById(@PathVariable Long id) throws NotFoundException {
    return userService.findById(id);
  }

  @GetMapping("/admin/{id}")
  public UserAdminView privateFindByAdminId(@PathVariable Long id) throws NotFoundException {
    return userService.privateFindById(id);
  }

  @GetMapping("/me")
  public UserOwnerView findMe() throws NotFoundException {
    return userService.findMe();
  }

  @PutMapping("/me")
  public UserOwnerView updateMe(@RequestBody UserUpdateRequest request) throws NotFoundException {
    return userService.updateMe(request);
  }

  @PutMapping("/me/password")
  public void updateMyPassword(@RequestBody UserUpdatePasswordRequest request) throws NotFoundException, OperationNotAllowed {
    userService.updateMyPassword(request);
  }

  @PutMapping("/admin/{id}/active")
  public UserDTOPublic activate(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    return userService.updateActive(id, true);
  }

  @DeleteMapping("/admin/{id}/active")
  public UserDTOPublic deactivate(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    return userService.updateActive(id, false);
  }

  @DeleteMapping("/admin/{id}")
  public void delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    userService.deleteById(id);
  }
}
