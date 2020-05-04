package uk.nhs.cdss.transform.ucdos.in;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static uk.nhs.cdss.transform.ucdos.in.FhirMatchers.*;

import java.util.Arrays;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.junit.Test;
import uk.nhs.cdss.constants.Systems;
import uk.nhs.cdss.model.ucdos.DosRestResponse;
import uk.nhs.cdss.model.ucdos.Service;

public class DosRestResponseTransformerTest
    extends ResponseTransformerTest<DosRestResponse> {

  @Override
  protected Class<DosRestResponse> getType() {
    return DosRestResponse.class;
  }

  private HealthcareService getTransformed(Service service) {
    return transformed.getParameters("services")
        .getPartsOf(service.getServiceId())
        .getResource("service", HealthcareService.class);
  }

  @Test
  public void transform_outputParameters() {
    var outputParameters = transformed.getParameters("outputParameters");

    assertEquals(
        initial.getTransactionId(),
        outputParameters.getString("TransactionID"));
    assertEquals(
        initial.getStatus(),
        outputParameters.getString("Status"));
    assertEquals(
        initial.getCode(),
        outputParameters.getString("Code"));
    assertEquals(
        initial.isCatchAll(),
        outputParameters.getBoolean("CatchAll"));
    assertEquals(
        initial.getServiceCount(),
        outputParameters.getInt("ServiceCount"));
  }

  @Test
  public void transform_distanceAndCapacity() {
    var services = transformed.getParameters("services");
    var service1 = services.getPartsOf("service#1");
    var service2 = services.getPartsOf("service#2");

    assertEquals("1.0", service1.getString("distance"));
    assertEquals("GREEN", service1.getString("capacityRag"));
    assertEquals("HIGH", service1.getString("capacityHuman"));
    assertEquals("idk", service1.getString("capacityHex"));

    assertEquals("2.0", service2.getString("distance"));
    assertEquals("AMBER", service2.getString("capacityRag"));
    assertEquals("LOW", service2.getString("capacityHuman"));
    assertEquals("idk", service2.getString("capacityHex"));
  }

  @Test
  public void transform_serviceInfo() {
    var services = transformed.getParameters("services");
    var service1 = services.getPartsOf("service#1")
        .getResource("service", HealthcareService.class);
    var service2 = services.getPartsOf("service#2")
        .getResource("service", HealthcareService.class);

    var odsIdentifier1 = service1.getProvidedByTarget()
        .getIdentifier()
        .stream()
        .filter(i -> Systems.ODS.equals(i.getSystem()))
        .findFirst()
        .orElseThrow();
    assertEquals("ods=1", odsIdentifier1.getValue());
    assertEquals("service number 1", service1.getName());

    var serviceType1 = service1.getTypeFirstRep();
    assertEquals("type#1", serviceType1.getCodingFirstRep().getCode());
    assertEquals("service type number 1", serviceType1.getCodingFirstRep().getDisplay());
    assertEquals("service type number 1", serviceType1.getText());

    var odsIdentifier2 = service2.getProvidedByTarget()
        .getIdentifier()
        .stream()
        .filter(i -> Systems.ODS.equals(i.getSystem()))
        .findFirst()
        .orElseThrow();
    assertEquals("ods=2", odsIdentifier2.getValue());
    assertEquals("service number 2", service2.getName());

    var serviceType2 = service2.getTypeFirstRep();
    assertEquals("type#2", serviceType2.getCodingFirstRep().getCode());
    assertEquals("service type number 2", serviceType2.getCodingFirstRep().getDisplay());
    assertEquals("service type number 2", serviceType2.getText());
  }

  @Test
  public void transform_location() {
    for (var initialService : initial.getServices()) {
      var service = getTransformed(initialService);
      var initialTelecom = initialService.getPhone();

      var workPhone = isContactPoint(
          1,
          ContactPointSystem.PHONE,
          ContactPointUse.WORK,
          initialTelecom.getPublicPhone());
      var mobilePhone = isContactPoint(
          2,
          ContactPointSystem.PHONE,
          ContactPointUse.MOBILE,
          initialTelecom.getNonPublicPhone());
      var fax = isContactPoint(
          3,
          ContactPointSystem.FAX,
          ContactPointUse.WORK,
          initialTelecom.getFax());
      var website = isContactPoint(
          4,
          ContactPointSystem.URL,
          ContactPointUse.WORK,
          initialService.getWebsite());

      assertThat(
          service.getTelecom(),
          contains(Arrays.asList(workPhone, mobilePhone, fax, website)));
    }
  }
}