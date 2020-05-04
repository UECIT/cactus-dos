package uk.nhs.cdss.transform;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.HealthcareService;
import uk.nhs.cdss.model.Provision;
import uk.nhs.cdss.model.ServiceSpecialty;
import uk.nhs.cdss.model.ServiceType;

@Component
@AllArgsConstructor
public class HealthcareServiceTransformer implements Transformer<HealthcareService, org.hl7.fhir.dstu3.model.HealthcareService> {

  private EndpointTransformer endpointTransformer;
  private CodeableConceptTransformer codeableConceptTransformer;
  private LocationTransformer locationTransformer;
  private ContactPointTransformer contactPointTransformer;
  private AvailableTimeTransformer availableTimeTransformer;
  private NotAvailableTransformer notAvailableTransformer;

  @Override
  public org.hl7.fhir.dstu3.model.HealthcareService transform(HealthcareService from) {
    org.hl7.fhir.dstu3.model.HealthcareService healthcareService =
        new org.hl7.fhir.dstu3.model.HealthcareService();

    healthcareService.setId(String.valueOf(from.getId()));
    healthcareService.setActive(from.isActive())
        .setName(from.getName())
        .setAppointmentRequired(from.isAppointmentRequired());

    List<Resource> endpoints = endpointTransformer.transform(from.getEndpoints());

    healthcareService.setContained(endpoints);

    List<Reference> endpointReferences = endpoints.stream()
        .map(Reference::new)
        .collect(Collectors.toList());

    healthcareService.setEndpoint(endpointReferences)
        .setSpecialty(codeableConceptTransformer.transform(from.getServiceSpecialties(), ServiceSpecialty.class))
        .setType(codeableConceptTransformer.transform(from.getServiceTypes(), ServiceType.class))
        .setLocation(Collections.singletonList(new Reference(locationTransformer.transform(from.getAddress()))))
        .setExtraDetails(from.getExtraDetails())
        .setComment(from.getDescription())
        .setTelecom(contactPointTransformer.transform(from.getContacts()))
        .setServiceProvisionCode(codeableConceptTransformer.transform(from.getProvisions(), Provision.class))
        .setAvailableTime(availableTimeTransformer.transform(from.getAvailableTimes()))
        .setNotAvailable(notAvailableTransformer.transform(from.getUnavailableTimes()));

    return healthcareService;
  }
}
