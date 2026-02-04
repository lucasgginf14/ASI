package es.udc.asi.bnbria_rest;

import es.udc.asi.bnbria_rest.common.config.DatabaseLoader;
import es.udc.asi.bnbria_rest.common.exceptions.model.UserEmailExistsException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BnbriaRestApplication {
  private final Logger logger = LoggerFactory.getLogger(BnbriaRestApplication.class);

  @Autowired
  @Lazy
  private DatabaseLoader databaseLoader;

  public static void main(String[] args) {
    SpringApplication.run(BnbriaRestApplication.class, args);
  }

  @PostConstruct
  public void init() {
    try {
      databaseLoader.loadData();
    } catch (UserEmailExistsException e) {
      logger.error(e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
