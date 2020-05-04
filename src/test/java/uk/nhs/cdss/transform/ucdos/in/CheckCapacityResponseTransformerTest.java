package uk.nhs.cdss.transform.ucdos.in;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static uk.nhs.cdss.transform.ucdos.in.FhirMatchers.*;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.junit.Test;
import uk.nhs.cdss.constants.Systems;
import uk.nhs.cdss.model.ucdos.wsdl.CheckCapacitySummaryResponse;
import uk.nhs.cdss.model.ucdos.wsdl.ServiceCareSummaryDestination;

public class CheckCapacityResponseTransformerTest
    extends ResponseTransformerTest<CheckCapacitySummaryResponse> {

  @Override
  protected Class<CheckCapacitySummaryResponse> getType() {
    return CheckCapacitySummaryResponse.class;
  }

  private List<ServiceCareSummaryDestination> getInitialServices() {
    return initial.getCheckCapacitySummaryResult().getServiceCareSummaryDestination();
  }
  private HealthcareService getTransformed(ServiceCareSummaryDestination service) {
    return transformed.getParameters("services")
        .getPartsOf(Integer.toString(service.getId()))
        .getResource("service", HealthcareService.class);
  }

  @Test
  public void transform_outputParameters() {
    var outputParameters = transformed.getParameters("outputParameters");

    assertEquals(initial.getTransactionId(), outputParameters.getString("TransactionID"));
    assertEquals(
        initial.getRequestedAtDateTime(),
        outputParameters.getString("RequestedAtDateTime"));
    assertEquals(initial.getSearchDateTime(), outputParameters.getString("SearchDateTime"));
    assertEquals(initial.getSearchDistance(), outputParameters.getInt("SearchDistance"));
    assertEquals(
        initial.getSearchDistanceUsedSource().value(),
        outputParameters.getString("SearchDistanceUsedSource"));
  }

  @Test
  public void transform_distanceAndCapacity() {
    var services = transformed.getParameters("services");

    for (var initialService : getInitialServices()) {
      var service = services.getPartsOf(Integer.toString(initialService.getId()));
      assertEquals(initialService.getDistance(), service.getString("distance"));
      assertEquals(
          initialService.getCapacity().toString(),
          service.getString("capacityHuman").toUpperCase());
      assertNull(service.getString("capacityHex"));
    }
  }

  @Test
  public void transform_serviceInfo() {
    for (var initialService : getInitialServices()) {
      var service = getTransformed(initialService);

      var odsIdentifier = service.getProvidedByTarget()
          .getIdentifier()
          .stream()
          .filter(i -> Systems.ODS.equals(i.getSystem()))
          .findFirst()
          .orElseThrow();
      assertEquals(odsIdentifier.getValue(), initialService.getOdsCode());
      assertEquals(service.getName(), initialService.getName());
      if (StringUtils.isNotEmpty(initialService.getPublicName())) {
        assertThat(service.getProgramName(), contains(initialService.getPublicName()));
      }

      var type = initialService.getServiceType();
      assertThat(
          service.getType(),
          contains(isCodeableConcept(Systems.ODS, Long.toString(type.getId()), type.getName())));

      assertThat(
          service.getExtraDetails(),
          containsString(initialService.getReferralInformation()));

      if (StringUtils.isNotEmpty(initialService.getPublicFacingInformation())) {
        assertThat(
            service.getComment(),
            containsString(initialService.getPublicFacingInformation()));
      }
    }
  }

  @Test
  public void transform_telecom() {
    for (var initialService : getInitialServices()) {
      var service = getTransformed(initialService);

      var workPhone = isContactPoint(
          1,
          ContactPointSystem.PHONE,
          ContactPointUse.WORK,
          initialService.getContactDetails());
      var mobilePhone = isContactPoint(
          2,
          ContactPointSystem.PHONE,
          ContactPointUse.MOBILE,
          initialService.getNonPublicTelephoneNo());
      var fax = isContactPoint(
          3,
          ContactPointSystem.FAX,
          ContactPointUse.WORK,
          initialService.getFax());
      var website = isContactPoint(
          4,
          ContactPointSystem.URL,
          ContactPointUse.WORK,
          initialService.getUrl());

      assertThat(
          service.getTelecom(),
          contains(Arrays.asList(workPhone, mobilePhone, fax, website)));
    }
  }
}