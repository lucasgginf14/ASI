package es.udc.asi.bnbria_rest.common.security;


import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component
public class MyUserDetailsService implements UserDetailsService {
  private final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

  @Autowired
  private UserDao userDAO;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userDAO.findByEmail(email);
    if (user == null) {
      throw new UsernameNotFoundException("User " + email + " not found");
    }
    if (!user.isActive()) {
      throw new UsernameNotFoundException("User " + email + " not found");
    }
    logger.info("Loaded user {} with authority {}", email, user.getAuthority().name());
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getAuthority().name());
    return new org.springframework.security.core.userdetails.User(email, user.getPassword(),
      Collections.singleton(authority));
  }
}
