package es.udc.asi.bnbria_rest.common.config;

import es.udc.asi.bnbria_rest.availability.persistence.dao.AvailabilityDao;
import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.booking.persistence.dao.BookingDao;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.common.exceptions.model.UserEmailExistsException;
import es.udc.asi.bnbria_rest.message.persistence.dao.MessageDao;
import es.udc.asi.bnbria_rest.message.persistence.entity.Message;
import es.udc.asi.bnbria_rest.property.persistence.dao.PropertyDao;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;
import es.udc.asi.bnbria_rest.property.service.PropertySyncService;
import es.udc.asi.bnbria_rest.propertyreview.persistence.dao.PropertyReviewDao;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.persistence.entity.UserAuthority;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserCreateRequest;
import es.udc.asi.bnbria_rest.userreview.persistence.dao.UserReviewDao;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Configuration
public class DatabaseLoader {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserDao userDAO;

  @Autowired
  private UserReviewDao userReviewDAO;

  @Autowired
  private PropertyDao propertyDAO;

  @Autowired
  private Properties properties;

  @Autowired
  private PropertyReviewDao propertyReviewDAO;

  @Autowired
  private AvailabilityDao availabilityDAO;

  @Autowired
  private BookingDao bookingDAO;

  @Autowired
  private UserService userService;

  @Autowired
  private MessageDao messageDAO;

  User a1, a2, h1, h2;
  Property p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
  Booking b1, b2, b3, b4, b5, b6;
  @Autowired
  private PropertySyncService propertySyncService;

  @Transactional(rollbackFor = Exception.class)
  public void loadData() throws Exception {
    loadUsers();
    loadProperties();
    loadAvailabilities();
    loadBookings();
    loadMessages();
    loadUserReviews();
    loadPropertiesReviews();
  }

  private void loadUsers() throws UserEmailExistsException {
    User adrian = new User();
    adrian.setEmail("adrian.gantes@udc.es");
    adrian.setPassword(passwordEncoder.encode("adrian123"));
    adrian.setName("Adrián");
    adrian.setSurname("Edreira");
    adrian.setSurname_2("Gantes");
    adrian.setAuthority(UserAuthority.ADMIN);
    adrian.setActive(true);
    userDAO.create(adrian);

    User lucas = new User();
    lucas.setEmail("lucas.garcia.garcia@udc.es");
    lucas.setPassword(passwordEncoder.encode("lucas123"));
    lucas.setName("Lucas");
    lucas.setSurname("García");
    lucas.setSurname_2("García");
    lucas.setAuthority(UserAuthority.ADMIN);
    lucas.setActive(true);
    userDAO.create(lucas);

    UserCreateRequest anfitrion1 = new UserCreateRequest(
      "anfitrion1@bnbria.es",
      "anfitrion123",
      "anfitrion123",
      "Anfitrión",
      "De Pruebas",
      "1",
      "XXX-XXX-XX1",
      Date.from(LocalDate.parse("1990-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userService.registerUser(anfitrion1);
    a1 = userDAO.findByEmail("anfitrion1@bnbria.es");

    UserCreateRequest anfitrion2 = new UserCreateRequest(
      "anfitrion2@bnbria.es",
      "anfitrion123",
      "anfitrion123",
      "Anfitrión",
      "De Pruebas",
      "2",
      "XXX-XXX-XX2",
      Date.from(LocalDate.parse("1990-01-01").atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userService.registerUser(anfitrion2);
    a2 = userDAO.findByEmail("anfitrion2@bnbria.es");

    UserCreateRequest huesped1 = new UserCreateRequest(
      "huesped1@bnbria.es"
      , "huesped123",
      "huesped123",
      "Huésped",
      "De Pruebas",
      "1",
      "XXX-XXX-XX3",
      Date.from(LocalDate.parse("1995-05-05").atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userService.registerUser(huesped1);
    h1 = userDAO.findByEmail("huesped1@bnbria.es");

    UserCreateRequest huesped2 = new UserCreateRequest(
      "huesped2@bnbria.es"
      , "huesped123",
      "huesped123",
      "Huésped",
      "De Pruebas",
      "2",
      "XXX-XXX-XX4",
      Date.from(LocalDate.parse("1995-05-05").atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userService.registerUser(huesped2);
    h2 = userDAO.findByEmail("huesped2@bnbria.es");
  }

  private void loadProperties() throws Exception {

    Path dstDir = Paths.get(properties.getRutaImagenes());
    Files.createDirectories(dstDir);

    p1 = new Property();
    p1.setOwner(a1);
    p1.setTitle("Apartamento céntrico A1");
    p1.setDescription("Apartamento luminoso y céntrico, equipado para 2 personas.");
    p1.setState(PropertyState.APPROVED);
    p1.setBathrooms(1L);
    p1.setBedrooms(1L);
    p1.setMaxOccupancy(2L);
    p1.setSquareMeters(45L);
    p1.setType(PropertyType.APARTMENT);
    p1.setDoor("A");
    p1.setNumber("10");
    p1.setStreet("Calle Real");
    p1.setCity("A Coruña");
    p1.setProvince("A Coruña");
    p1.setCountry("España");
    p1.setPostalCode(15001L);

    PropertySyncService.Coordinates cords1 = propertySyncService.lookupCoordinates(
      p1.getStreet(),
      p1.getCity(),
      p1.getProvince(),
      p1.getCountry(),
      p1.getPostalCode()
    );
    p1.setLatitude(cords1.lat());
    p1.setLongitude(cords1.lon());

    propertyDAO.create(p1);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/1.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("1.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p1.setImage("p1.png");
    propertyDAO.update(p1);

    p2 = new Property();
    p2.setOwner(a1);
    p2.setTitle("Estudio cómodo A2");
    p2.setDescription("Estudio moderno, ideal para estancias cortas.");
    p2.setState(PropertyState.PENDING);
    p2.setBathrooms(1L);
    p2.setBedrooms(0L);
    p2.setMaxOccupancy(2L);
    p2.setSquareMeters(30L);
    p2.setType(PropertyType.STUDIO);
    p2.setDoor("B");
    p2.setNumber("12");
    p2.setStreet("Avenida de la Marina");
    p2.setCity("A Coruña");
    p2.setProvince("A Coruña");
    p2.setCountry("España");
    p2.setPostalCode(15002L);

    PropertySyncService.Coordinates cords2 = propertySyncService.lookupCoordinates(
      p2.getStreet(),
      p2.getCity(),
      p2.getProvince(),
      p2.getCountry(),
      p2.getPostalCode()
    );
    p2.setLatitude(cords2.lat());
    p2.setLongitude(cords2.lon());

    propertyDAO.create(p2);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/2.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("2.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p2.setImage("p2.png");
    propertyDAO.update(p2);

    p3 = new Property();
    p3.setOwner(a2);
    p3.setTitle("Casa familiar B1");
    p3.setDescription("Casa espaciosa cerca de transporte y servicios.");
    p3.setState(PropertyState.APPROVED);
    p3.setBathrooms(2L);
    p3.setBedrooms(3L);
    p3.setMaxOccupancy(6L);
    p3.setSquareMeters(120L);
    p3.setType(PropertyType.HOUSE);
    p3.setDoor("C");
    p3.setNumber("5");
    p3.setStreet("Camino Real");
    p3.setCity("Santiago de Compostela");
    p3.setProvince("A Coruña");
    p3.setCountry("España");
    p3.setPostalCode(15701L);

    PropertySyncService.Coordinates cords3 = propertySyncService.lookupCoordinates(
      p3.getStreet(),
      p3.getCity(),
      p3.getProvince(),
      p3.getCountry(),
      p3.getPostalCode()
    );
    p3.setLatitude(cords3.lat());
    p3.setLongitude(cords3.lon());

    propertyDAO.create(p3);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/3.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("3.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p3.setImage("p3.png");
    propertyDAO.update(p3);

    p4 = new Property();
    p4.setOwner(a2);
    p4.setTitle("Loft acogedor B2");
    p4.setDescription("Loft con todo lo necesario para una estancia confortable.");
    p4.setState(PropertyState.REJECTED);
    p4.setBathrooms(1L);
    p4.setBedrooms(1L);
    p4.setMaxOccupancy(2L);
    p4.setSquareMeters(55L);
    p4.setType(PropertyType.LOFT);
    p4.setDoor("D");
    p4.setNumber("8");
    p4.setStreet("Calle Nueva");
    p4.setCity("Santiago de Compostela");
    p4.setProvince("A Coruña");
    p4.setCountry("España");
    p4.setPostalCode(15702L);

    PropertySyncService.Coordinates cords4 = propertySyncService.lookupCoordinates(
      p4.getStreet(),
      p4.getCity(),
      p4.getProvince(),
      p4.getCountry(),
      p4.getPostalCode()
    );
    p4.setLatitude(cords4.lat());
    p4.setLongitude(cords4.lon());

    propertyDAO.create(p4);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/4.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("4.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p4.setImage("p4.png");
    propertyDAO.update(p4);

    p5 = new Property();
    p5.setOwner(a1);
    p5.setTitle("Ático panorámico A3");
    p5.setDescription("Ático con terraza y vistas al mar, ideal para parejas.");
    p5.setState(PropertyState.APPROVED);
    p5.setBathrooms(1L);
    p5.setBedrooms(1L);
    p5.setMaxOccupancy(2L);
    p5.setSquareMeters(60L);
    p5.setType(PropertyType.APARTMENT);
    p5.setDoor("E");
    p5.setNumber("3");
    p5.setStreet("Rúa do Mar");
    p5.setCity("A Coruña");
    p5.setProvince("A Coruña");
    p5.setCountry("España");
    p5.setPostalCode(15003L);

    PropertySyncService.Coordinates cords5 = propertySyncService.lookupCoordinates(
      p5.getStreet(),
      p5.getCity(),
      p5.getProvince(),
      p5.getCountry(),
      p5.getPostalCode()
    );
    p5.setLatitude(cords5.lat());
    p5.setLongitude(cords5.lon());

    propertyDAO.create(p5);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/5.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("5.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p5.setImage("p5.png");
    propertyDAO.update(p5);

    p6 = new Property();
    p6.setOwner(a2);
    p6.setTitle("Villa con jardín B3");
    p6.setDescription("Villa independiente con jardín amplio y barbacoa.");
    p6.setState(PropertyState.APPROVED);
    p6.setBathrooms(3L);
    p6.setBedrooms(4L);
    p6.setMaxOccupancy(8L);
    p6.setSquareMeters(200L);
    p6.setType(PropertyType.HOUSE);
    p6.setDoor("F");
    p6.setNumber("21");
    p6.setStreet("Rúa da Rosa");
    p6.setCity("Santiago de Compostela");
    p6.setProvince("A Coruña");
    p6.setCountry("España");
    p6.setPostalCode(15703L);

    PropertySyncService.Coordinates cords6 = propertySyncService.lookupCoordinates(
      p6.getStreet(),
      p6.getCity(),
      p6.getProvince(),
      p6.getCountry(),
      p6.getPostalCode()
    );
    p6.setLatitude(cords6.lat());
    p6.setLongitude(cords6.lon());

    propertyDAO.create(p6);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/6.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("6.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p6.setImage("p6.png");
    propertyDAO.update(p6);

    p7 = new Property();
    p7.setOwner(a1);
    p7.setTitle("Mini estudio A4");
    p7.setDescription("Pequeño y funcional, perfecto para viajeros solos.");
    p7.setState(PropertyState.PENDING);
    p7.setBathrooms(1L);
    p7.setBedrooms(0L);
    p7.setMaxOccupancy(1L);
    p7.setSquareMeters(20L);
    p7.setType(PropertyType.STUDIO);
    p7.setDoor("G");
    p7.setNumber("6");
    p7.setStreet("Rúa Pequeña");
    p7.setCity("A Coruña");
    p7.setProvince("A Coruña");
    p7.setCountry("España");
    p7.setPostalCode(15004L);

    PropertySyncService.Coordinates cords7 = propertySyncService.lookupCoordinates(
      p7.getStreet(),
      p7.getCity(),
      p7.getProvince(),
      p7.getCountry(),
      p7.getPostalCode()
    );
    p7.setLatitude(cords7.lat());
    p7.setLongitude(cords7.lon());

    propertyDAO.create(p7);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/7.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("7.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p7.setImage("p7.png");
    propertyDAO.update(p7);

    p8 = new Property();
    p8.setOwner(a2);
    p8.setTitle("Loft artístico B4");
    p8.setDescription("Loft con decoración moderna y mucha luz natural.");
    p8.setState(PropertyState.APPROVED);
    p8.setBathrooms(1L);
    p8.setBedrooms(1L);
    p8.setMaxOccupancy(2L);
    p8.setSquareMeters(70L);
    p8.setType(PropertyType.LOFT);
    p8.setDoor("H");
    p8.setNumber("14");
    p8.setStreet("Avenida de Rosalía de Castro");
    p8.setCity("Santiago de Compostela");
    p8.setProvince("A Coruña");
    p8.setCountry("España");
    p8.setPostalCode(15704L);

    PropertySyncService.Coordinates cords8 = propertySyncService.lookupCoordinates(
      p8.getStreet(),
      p8.getCity(),
      p8.getProvince(),
      p8.getCountry(),
      p8.getPostalCode()
    );
    p8.setLatitude(cords8.lat());
    p8.setLongitude(cords8.lon());

    propertyDAO.create(p8);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/8.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("8.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p8.setImage("p8.png");
    propertyDAO.update(p8);

    p9 = new Property();
    p9.setOwner(a2);
    p9.setTitle("Apartamento familiar A5");
    p9.setDescription("Amplio apartamento cercano a parques y tiendas.");
    p9.setState(PropertyState.APPROVED);
    p9.setBathrooms(2L);
    p9.setBedrooms(2L);
    p9.setMaxOccupancy(4L);
    p9.setSquareMeters(85L);
    p9.setType(PropertyType.APARTMENT);
    p9.setDoor("I");
    p9.setNumber("27");
    p9.setStreet("Avenida das Flores");
    p9.setCity("Carballo");
    p9.setProvince("A Coruña");
    p9.setCountry("España");
    p9.setPostalCode(15005L);

    PropertySyncService.Coordinates cords9 = propertySyncService.lookupCoordinates(
      p9.getStreet(),
      p9.getCity(),
      p9.getProvince(),
      p9.getCountry(),
      p9.getPostalCode()
    );
    p9.setLatitude(cords9.lat());
    p9.setLongitude(cords9.lon());

    propertyDAO.create(p9);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/9.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("9.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p9.setImage("p9.png");
    propertyDAO.update(p9);

    p10 = new Property();
    p10.setOwner(a2);
    p10.setTitle("Casa rural B5");
    p10.setDescription("Casa tradicional en zona tranquila, ideal para desconectar.");
    p10.setState(PropertyState.REJECTED);
    p10.setBathrooms(2L);
    p10.setBedrooms(3L);
    p10.setMaxOccupancy(6L);
    p10.setSquareMeters(140L);
    p10.setType(PropertyType.HOUSE);
    p10.setDoor("J");
    p10.setNumber("2");
    p10.setStreet("Rúa do Pombal");
    p10.setCity("Santiago de Compostela");
    p10.setProvince("A Coruña");
    p10.setCountry("España");
    p10.setPostalCode(15705L);

    PropertySyncService.Coordinates cords10 = propertySyncService.lookupCoordinates(
      p10.getStreet(),
      p10.getCity(),
      p10.getProvince(),
      p10.getCountry(),
      p10.getPostalCode()
    );
    p10.setLatitude(cords10.lat());
    p10.setLongitude(cords10.lon());

    propertyDAO.create(p10);
    try (InputStream is = getClass()
      .getClassLoader()
      .getResourceAsStream("propertyimage/10.png")) {

      if (is == null) {
        System.err.println("Imagen no encontrada en resources");
        return;
      }

      Files.copy(is,
        dstDir.resolve("10.png"),
        StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      e.printStackTrace();
    }
    p10.setImage("p10.png");
    propertyDAO.update(p10);
  }

  private void loadAvailabilities() {

    // p1: dos continuas y una separada
    Availability avP1_1 = new Availability();
    avP1_1.setStartDate(LocalDate.parse("2026-02-01"));
    avP1_1.setEndDate(LocalDate.parse("2026-02-10"));
    avP1_1.setPrice(50.0);
    avP1_1.setProperty(p1);
    availabilityDAO.create(avP1_1);

    Availability avP1_2 = new Availability();
    avP1_2.setStartDate(LocalDate.parse("2026-02-11"));
    avP1_2.setEndDate(LocalDate.parse("2026-02-20"));
    avP1_2.setPrice(55.0);
    avP1_2.setProperty(p1);
    availabilityDAO.create(avP1_2);

    Availability avP1_3 = new Availability();
    avP1_3.setStartDate(LocalDate.parse("2026-03-01"));
    avP1_3.setEndDate(LocalDate.parse("2026-03-05"));
    avP1_3.setPrice(60.0);
    avP1_3.setProperty(p1);
    availabilityDAO.create(avP1_3);

    // p3: dispersas (no continuas)
    Availability avP3_1 = new Availability();
    avP3_1.setStartDate(LocalDate.parse("2026-04-01"));
    avP3_1.setEndDate(LocalDate.parse("2026-04-05"));
    avP3_1.setPrice(120.0);
    avP3_1.setProperty(p3);
    availabilityDAO.create(avP3_1);

    Availability avP3_2 = new Availability();
    avP3_2.setStartDate(LocalDate.parse("2026-04-10"));
    avP3_2.setEndDate(LocalDate.parse("2026-04-15"));
    avP3_2.setPrice(130.0);
    avP3_2.setProperty(p3);
    availabilityDAO.create(avP3_2);

    Availability avP3_3 = new Availability();
    avP3_3.setStartDate(LocalDate.parse("2026-05-01"));
    avP3_3.setEndDate(LocalDate.parse("2026-05-07"));
    avP3_3.setPrice(110.0);
    avP3_3.setProperty(p3);
    availabilityDAO.create(avP3_3);

    // p5: dos continuas y una separada
    Availability avP5_1 = new Availability();
    avP5_1.setStartDate(LocalDate.parse("2026-06-01"));
    avP5_1.setEndDate(LocalDate.parse("2026-06-07"));
    avP5_1.setPrice(80.0);
    avP5_1.setProperty(p5);
    availabilityDAO.create(avP5_1);

    Availability avP5_2 = new Availability();
    avP5_2.setStartDate(LocalDate.parse("2026-06-08"));
    avP5_2.setEndDate(LocalDate.parse("2026-06-14"));
    avP5_2.setPrice(85.0);
    avP5_2.setProperty(p5);
    availabilityDAO.create(avP5_2);

    Availability avP5_3 = new Availability();
    avP5_3.setStartDate(LocalDate.parse("2026-07-01"));
    avP5_3.setEndDate(LocalDate.parse("2026-07-10"));
    avP5_3.setPrice(90.0);
    avP5_3.setProperty(p5);
    availabilityDAO.create(avP5_3);

    // p6: no continuas, precios altos
    Availability avP6_1 = new Availability();
    avP6_1.setStartDate(LocalDate.parse("2026-08-01"));
    avP6_1.setEndDate(LocalDate.parse("2026-08-05"));
    avP6_1.setPrice(200.0);
    avP6_1.setProperty(p6);
    availabilityDAO.create(avP6_1);

    Availability avP6_2 = new Availability();
    avP6_2.setStartDate(LocalDate.parse("2026-08-10"));
    avP6_2.setEndDate(LocalDate.parse("2026-08-20"));
    avP6_2.setPrice(180.0);
    avP6_2.setProperty(p6);
    availabilityDAO.create(avP6_2);

    Availability avP6_3 = new Availability();
    avP6_3.setStartDate(LocalDate.parse("2026-09-01"));
    avP6_3.setEndDate(LocalDate.parse("2026-09-05"));
    avP6_3.setPrice(170.0);
    avP6_3.setProperty(p6);
    availabilityDAO.create(avP6_3);

    // p8: continuidad y brecha
    Availability avP8_1 = new Availability();
    avP8_1.setStartDate(LocalDate.parse("2026-10-01"));
    avP8_1.setEndDate(LocalDate.parse("2026-10-07"));
    avP8_1.setPrice(95.0);
    avP8_1.setProperty(p8);
    availabilityDAO.create(avP8_1);

    Availability avP8_2 = new Availability();
    avP8_2.setStartDate(LocalDate.parse("2026-10-08"));
    avP8_2.setEndDate(LocalDate.parse("2026-10-14"));
    avP8_2.setPrice(100.0);
    avP8_2.setProperty(p8);
    availabilityDAO.create(avP8_2);

    Availability avP8_3 = new Availability();
    avP8_3.setStartDate(LocalDate.parse("2026-10-20"));
    avP8_3.setEndDate(LocalDate.parse("2026-10-25"));
    avP8_3.setPrice(105.0);
    avP8_3.setProperty(p8);
    availabilityDAO.create(avP8_3);

    // p9: dos continuas y una en otro mes
    Availability avP9_1 = new Availability();
    avP9_1.setStartDate(LocalDate.parse("2026-11-01"));
    avP9_1.setEndDate(LocalDate.parse("2026-11-05"));
    avP9_1.setPrice(75.0);
    avP9_1.setProperty(p9);
    availabilityDAO.create(avP9_1);

    Availability avP9_2 = new Availability();
    avP9_2.setStartDate(LocalDate.parse("2026-11-06"));
    avP9_2.setEndDate(LocalDate.parse("2026-11-10"));
    avP9_2.setPrice(78.0);
    avP9_2.setProperty(p9);
    availabilityDAO.create(avP9_2);

    Availability avP9_3 = new Availability();
    avP9_3.setStartDate(LocalDate.parse("2026-12-01"));
    avP9_3.setEndDate(LocalDate.parse("2026-12-10"));
    avP9_3.setPrice(82.0);
    avP9_3.setProperty(p9);
    availabilityDAO.create(avP9_3);
  }

  private void loadBookings() {
    // Se crean reservas que cubren distintos estados y combinaciones de fechas
    // - reservas dentro de una única disponibilidad
    // - reservas que abarcan dos disponibilidades contiguas (precio promediado por noches según disponibilidad)
    // - solapamientos permitidos cuando la reserva no está en estado CONFIRMED

    // Helper para convertir distintos tipos a LocalDate
    Function<Object, LocalDate> toLocalDate = d -> {
      if (d == null) return null;
      if (d instanceof LocalDate) return (LocalDate) d;
      if (d instanceof java.sql.Date) return ((java.sql.Date) d).toLocalDate();
      if (d instanceof Date) return ((Date) d).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      return null;
    };

    // 1) p1: reserva confirmada dentro de avP1_1 (precio 50.0) para h1
    b1 = new Booking();
    b1.setState(BookingState.CONFIRMED);
    b1.setRequestMoment(LocalDateTime.now());
    b1.setStartDate(LocalDate.parse("2026-02-02"));
    b1.setEndDate(LocalDate.parse("2026-02-05"));
    b1.setNumGuests(2);
    b1.setGuest(h1);
    b1.setProperty(p1);
    // calcular noches y precio (noche = diferencia de días entre start y end)
    LocalDate b1s = toLocalDate.apply(b1.getStartDate());
    LocalDate b1e = toLocalDate.apply(b1.getEndDate());
    long nightsB1 = ChronoUnit.DAYS.between(b1s, b1e);
    b1.setPrice(nightsB1 * 50.0); // avP1_1 tiene precio 50.0
    bookingDAO.create(b1);

    // 2) p1: reserva pendiente que solapa con b1 (permitido porque no está CONFIRMED)
    b2 = new Booking();
    b2.setState(BookingState.PENDING);
    b2.setRequestMoment(LocalDateTime.now());
    b2.setStartDate(LocalDate.parse("2026-02-04"));
    b2.setEndDate(LocalDate.parse("2026-02-06"));
    b2.setNumGuests(2);
    b2.setGuest(h2);
    b2.setProperty(p1);
    LocalDate b2s = toLocalDate.apply(b2.getStartDate());
    LocalDate b2e = toLocalDate.apply(b2.getEndDate());
    long nightsB2 = ChronoUnit.DAYS.between(b2s, b2e);
    b2.setPrice(nightsB2 * 50.0);
    bookingDAO.create(b2);

    // 3) p5: reserva que abarca dos disponibilidades contiguas (avP5_1 2026-06-01..06-07 price 80.0,
    //    avP5_2 2026-06-08..06-14 price 85.0). Reservamos 2026-06-03 .. 2026-06-10
    b3 = new Booking();
    b3.setState(BookingState.CONFIRMED);
    b3.setRequestMoment(LocalDateTime.now());
    b3.setStartDate(LocalDate.parse("2026-06-03"));
    b3.setEndDate(LocalDate.parse("2026-06-10"));
    b3.setNumGuests(2);
    b3.setGuest(h1);
    b3.setProperty(p5);
    // calcular precio por noche iterando días y aplicando el precio correcto según disponibilidad conocida
    LocalDate iter = toLocalDate.apply(b3.getStartDate());
    LocalDate endIter = toLocalDate.apply(b3.getEndDate());
    double totalPriceB3 = 0.0;
    while (iter.isBefore(endIter)) {
      // avP5_1: hasta 2026-06-07 inclusive, avP5_2: desde 2026-06-08
      if (!iter.isAfter(LocalDate.of(2026, 6, 7))) {
        totalPriceB3 += 80.0;
      } else {
        totalPriceB3 += 85.0;
      }
      iter = iter.plusDays(1);
    }
    b3.setPrice(totalPriceB3);
    bookingDAO.create(b3);

    // 4) p8: reserva PENDING que solapa parcialmente con otra PENDING (ambas no CONFIRMED)
    b4 = new Booking();
    b4.setState(BookingState.PENDING);
    b4.setRequestMoment(LocalDateTime.now());
    b4.setStartDate(LocalDate.parse("2026-10-05"));
    b4.setEndDate(LocalDate.parse("2026-10-09")); // abarca avP8_1 y avP8_2 (contiguas)
    b4.setNumGuests(2);
    b4.setGuest(h2);
    b4.setProperty(p8);
    // precios: avP8_1 = 95.0 (2026-10-01..10-07), avP8_2 = 100.0 (2026-10-08..10-14)
    iter = toLocalDate.apply(b4.getStartDate());
    endIter = toLocalDate.apply(b4.getEndDate());
    double totalPriceB4 = 0.0;
    while (iter.isBefore(endIter)) {
      if (!iter.isAfter(LocalDate.of(2026, 10, 7))) {
        totalPriceB4 += 95.0;
      } else {
        totalPriceB4 += 100.0;
      }
      iter = iter.plusDays(1);
    }
    b4.setPrice(totalPriceB4);
    bookingDAO.create(b4);

    // 5) p6: reserva REJECTED dentro de una disponibilidad (precio alto)
    b5 = new Booking();
    b5.setState(BookingState.CANCELED_BY_HOST);
    b5.setRequestMoment(LocalDateTime.now());
    b5.setStartDate(LocalDate.parse("2026-08-10"));
    b5.setEndDate(LocalDate.parse("2026-08-15"));
    b5.setNumGuests(4);
    b5.setGuest(h1);
    b5.setProperty(p6);
    b5.setCancellationReason("No me gusta el huésped");
    // avP6_2 price 180.0 covers 2026-08-10 .. 2026-08-20
    LocalDate b5s = toLocalDate.apply(b5.getStartDate());
    LocalDate b5e = toLocalDate.apply(b5.getEndDate());
    long nightsB5 = ChronoUnit.DAYS.between(b5s, b5e);
    b5.setPrice(nightsB5 * 180.0);
    bookingDAO.create(b5);

    // 6) p9: reserva CANCELED (con motivo) dentro de una disponibilidad
    b6 = new Booking();
    b6.setState(BookingState.CANCELED_BY_GUEST);
    b6.setRequestMoment(LocalDateTime.now());
    b6.setStartDate(LocalDate.parse("2026-11-02"));
    b6.setEndDate(LocalDate.parse("2026-11-05"));
    b6.setNumGuests(3);
    b6.setGuest(h2);
    b6.setProperty(p9);
    long nightsB6 = ChronoUnit.DAYS.between(
      toLocalDate.apply(b6.getStartDate()), toLocalDate.apply(b6.getEndDate()));
    // avP9_1 price 75.0 covers this range
    b6.setPrice(nightsB6 * 75.0);
    b6.setCancellationReason("Cancelada por el huésped");
    bookingDAO.create(b6);
  }

  private void loadMessages() {
    // Mensajes para b1..b6. Algunas reservas con un solo mensaje; otras con conversaciones cortas.

    // b1: un solo mensaje del huésped al anfitrión (no leído por el anfitrión)
    Message m1 = new Message();
    m1.setRead(false);
    m1.setSentAt(LocalDateTime.now().minusDays(10));
    m1.setText("Hola, hemos llegado y todo está bien. Gracias por recibirnos.");
    m1.setWriter(h1);
    m1.setBooking(b1);
    messageDAO.create(m1);

    // b2: conversación corta (3 mensajes) entre huésped y anfitrión
    Message m2a = new Message();
    m2a.setRead(true);
    m2a.setSentAt(LocalDateTime.now().minusDays(8).minusHours(3));
    m2a.setText("Hola, ¿podría dejar las maletas antes del check-in?");
    m2a.setWriter(h2);
    m2a.setBooking(b2);
    messageDAO.create(m2a);

    Message m2b = new Message();
    m2b.setRead(true);
    m2b.setSentAt(LocalDateTime.now().minusDays(8).minusHours(2));
    m2b.setText("Sí, puedes dejar las maletas a partir de las 12:00.");
    m2b.setWriter(a1);
    m2b.setBooking(b2);
    messageDAO.create(m2b);

    Message m2c = new Message();
    m2c.setRead(false);
    m2c.setSentAt(LocalDateTime.now().minusDays(8).minusHours(1));
    m2c.setText("Perfecto, muchas gracias.");
    m2c.setWriter(h2);
    m2c.setBooking(b2);
    messageDAO.create(m2c);

    // b3: conversación más larga (6 mensajes) — alternancia huésped/anfitrión
    Message m3a = new Message();
    m3a.setRead(true);
    m3a.setSentAt(LocalDateTime.now().minusDays(20));
    m3a.setText("Hola, ¿hay utensilios para cocinar?");
    m3a.setWriter(h1);
    m3a.setBooking(b3);
    messageDAO.create(m3a);

    Message m3b = new Message();
    m3b.setRead(true);
    m3b.setSentAt(LocalDateTime.now().minusDays(20).plusMinutes(15));
    m3b.setText("Sí, la cocina está equipada con lo básico.");
    m3b.setWriter(a1);
    m3b.setBooking(b3);
    messageDAO.create(m3b);

    Message m3c = new Message();
    m3c.setRead(true);
    m3c.setSentAt(LocalDateTime.now().minusDays(19).minusHours(23));
    m3c.setText("Genial. ¿Hay parking cerca?");
    m3c.setWriter(h1);
    m3c.setBooking(b3);
    messageDAO.create(m3c);

    Message m3d = new Message();
    m3d.setRead(true);
    m3d.setSentAt(LocalDateTime.now().minusDays(19).minusHours(22).plusMinutes(10));
    m3d.setText("Hay un parking público a 200 m.");
    m3d.setWriter(a1);
    m3d.setBooking(b3);
    messageDAO.create(m3d);

    Message m3e = new Message();
    m3e.setRead(true);
    m3e.setSentAt(LocalDateTime.now().minusDays(18));
    m3e.setText("Perfecto, reservaremos plaza. Gracias.");
    m3e.setWriter(h1);
    m3e.setBooking(b3);
    messageDAO.create(m3e);

    Message m3f = new Message();
    m3f.setRead(true);
    m3f.setSentAt(LocalDateTime.now().minusDays(18).plusMinutes(5));
    m3f.setText("De nada, que disfrutes la estancia.");
    m3f.setWriter(a1);
    m3f.setBooking(b3);
    messageDAO.create(m3f);

    // b4: un solo mensaje del anfitrión al huésped (no leído por el huésped)
    Message m4 = new Message();
    m4.setRead(false);
    m4.setSentAt(LocalDateTime.now().minusDays(2));
    m4.setText("Hola, te dejo las instrucciones para el acceso al loft.");
    m4.setWriter(a2);
    m4.setBooking(b4);
    messageDAO.create(m4);

    // b5: conversación de 2 mensajes relacionada con cancelación (uno leído, otro no)
    Message m5a = new Message();
    m5a.setRead(true);
    m5a.setSentAt(LocalDateTime.now().minusDays(15));
    m5a.setText("Lamentamos la cancelación. ¿Deseas que intentemos reubicarte?");
    m5a.setWriter(a2);
    m5a.setBooking(b5);
    messageDAO.create(m5a);

    Message m5b = new Message();
    m5b.setRead(false);
    m5b.setSentAt(LocalDateTime.now().minusDays(15).plusMinutes(30));
    m5b.setText("Gracias, por ahora no es necesario.");
    m5b.setWriter(h1);
    m5b.setBooking(b5);
    messageDAO.create(m5b);

    // b6: conversación de 4 mensajes entre huésped y anfitrión
    Message m6a = new Message();
    m6a.setRead(true);
    m6a.setSentAt(LocalDateTime.now().minusDays(5));
    m6a.setText("¿Podrías confirmar la hora de llegada?");
    m6a.setWriter(h2);
    m6a.setBooking(b6);
    messageDAO.create(m6a);

    Message m6b = new Message();
    m6b.setRead(true);
    m6b.setSentAt(LocalDateTime.now().minusDays(5).plusMinutes(20));
    m6b.setText("Claro, ¿aproximadamente a qué hora estaréis?");
    m6b.setWriter(a2);
    m6b.setBooking(b6);
    messageDAO.create(m6b);

    Message m6c = new Message();
    m6c.setRead(true);
    m6c.setSentAt(LocalDateTime.now().minusDays(4).plusHours(2));
    m6c.setText("Salimos sobre las 18:30, graciaS.");
    m6c.setWriter(h2);
    m6c.setBooking(b6);
    messageDAO.create(m6c);

    Message m6d = new Message();
    m6d.setRead(false);
    m6d.setSentAt(LocalDateTime.now().minusDays(4).plusHours(2).plusMinutes(5));
    m6d.setText("Perfecto, os esperamos.");
    m6d.setWriter(a2);
    m6d.setBooking(b6);
    messageDAO.create(m6d);
  }

  private void loadUserReviews() {
    Date now = new Date(System.currentTimeMillis());

    // Reviews de a1 para b1..b3
    UserReview ur1 = new UserReview();
    ur1.setReviewer(a1);
    ur1.setBooking(b1);
    ur1.setRating(null);
    ur1.setDescription(null);
    ur1.setCreationDate(now);
    userReviewDAO.create(ur1);

    UserReview ur2 = new UserReview();
    ur2.setReviewer(a1);
    ur2.setBooking(b2);
    ur2.setRating(4);
    ur2.setDescription("Dejó el alojamiento en buen estado.");
    ur2.setCreationDate(now);
    userReviewDAO.create(ur2);

    UserReview ur3 = new UserReview();
    ur3.setReviewer(a1);
    ur3.setBooking(b3);
    ur3.setRating(5);
    ur3.setDescription("Invitado ejemplar, muy recomendable.");
    ur3.setCreationDate(now);
    userReviewDAO.create(ur3);

    // Reviews de a2 para b4..b6
    UserReview ur10 = new UserReview();
    ur10.setReviewer(a2);
    ur10.setBooking(b4);
    ur10.setRating(5);
    ur10.setDescription("Comunicación fluida y todo en orden.");
    ur10.setCreationDate(now);
    userReviewDAO.create(ur10);

    UserReview ur11 = new UserReview();
    ur11.setReviewer(a2);
    ur11.setBooking(b5);
    ur11.setRating(3);
    ur11.setDescription("Cancelación fuera de tiempo, pero correcto.");
    ur11.setCreationDate(now);
    userReviewDAO.create(ur11);

    UserReview ur12 = new UserReview();
    ur12.setReviewer(a2);
    ur12.setBooking(b6);
    ur12.setRating(null);
    ur12.setDescription(null);
    ur12.setCreationDate(now);
    userReviewDAO.create(ur12);
  }

  private void loadPropertiesReviews() {
    Date now = new Date(System.currentTimeMillis());

    // PropertyReview para b1
    PropertyReview pr1 = new PropertyReview();
    pr1.setProperty(b1.getProperty());
    pr1.setReviewer(b1.getGuest());
    pr1.setRating(5);
    pr1.setDescription("Reseña de " + b1.getGuest().getName() + " para " + b1.getProperty().getTitle());
    pr1.setCleanliness(5);
    pr1.setHospitality(4);
    pr1.setLocation(5);
    pr1.setCreationDate(now);
    propertyReviewDAO.create(pr1);

    // PropertyReview para b2
    PropertyReview pr2 = new PropertyReview();
    pr2.setProperty(b2.getProperty());
    pr2.setReviewer(b2.getGuest());
    pr2.setRating(4);
    pr2.setDescription("Reseña de " + b2.getGuest().getName() + " para " + b2.getProperty().getTitle());
    pr2.setCleanliness(4);
    pr2.setHospitality(3);
    pr2.setLocation(4);
    pr2.setCreationDate(now);
    propertyReviewDAO.create(pr2);

    // PropertyReview para b3
    PropertyReview pr3 = new PropertyReview();
    pr3.setProperty(b3.getProperty());
    pr3.setReviewer(b3.getGuest());
    pr3.setDescription("Reseña de " + b3.getGuest().getName() + " para " + b3.getProperty().getTitle());
    pr3.setRating(3);
    pr3.setCleanliness(3);
    pr3.setHospitality(3);
    pr3.setLocation(3);
    pr3.setCreationDate(now);
    propertyReviewDAO.create(pr3);

    // PropertyReview para b4
    PropertyReview pr4 = new PropertyReview();
    pr4.setProperty(b4.getProperty());
    pr4.setReviewer(b4.getGuest());
    pr4.setRating(5);
    pr4.setDescription("Reseña de " + b4.getGuest().getName() + " para " + b4.getProperty().getTitle());
    pr4.setCleanliness(4);
    pr4.setHospitality(5);
    pr4.setLocation(5);
    pr4.setCreationDate(now);
    propertyReviewDAO.create(pr4);

    // PropertyReview para b5
    PropertyReview pr5 = new PropertyReview();
    pr5.setProperty(b5.getProperty());
    pr5.setReviewer(b5.getGuest());
    pr5.setDescription("Reseña de " + b5.getGuest().getName() + " para " + b5.getProperty().getTitle());
    pr5.setRating(4);
    pr5.setCleanliness(4);
    pr5.setHospitality(4);
    pr5.setLocation(4);
    pr5.setCreationDate(now);
    propertyReviewDAO.create(pr5);

    // PropertyReview para b6
    PropertyReview pr6 = new PropertyReview();
    pr6.setProperty(b6.getProperty());
    pr6.setReviewer(b6.getGuest());
    pr6.setRating(3);
    pr6.setDescription("Reseña de " + b6.getGuest().getName() + " para " + b6.getProperty().getTitle());
    pr6.setCleanliness(3);
    pr6.setHospitality(3);
    pr6.setLocation(3);
    pr6.setCreationDate(now);
    propertyReviewDAO.create(pr6);
  }
}
