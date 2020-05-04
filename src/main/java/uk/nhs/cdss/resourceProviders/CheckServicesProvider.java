package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.param.NumberParam;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.builder.ParametersBuilder;
import uk.nhs.cdss.model.InputBundle;
import uk.nhs.cdss.service.HealthcareServiceService;
import uk.nhs.cdss.service.UCDOSService;

@AllArgsConstructor
@Component
public class CheckServicesProvider {

  private HealthcareServiceService healthcareServiceService;
  private UCDOSService ucdosService;

  @Operation(name = "$check-services")
  public Parameters searchForHealthcareServices(
      @OperationParam(name = "referralRequest", min = 1, max = 1) ReferralRequest referralRequest,
      @OperationParam(name = "patient", min = 1, max = 1) Patient patient,
      @OperationParam(name = "requestId", max = 1) IdType requestId,
      @OperationParam(name = "location", max = 1) Location location,
      @OperationParam(name = "requester", max = 1) IBaseResource requester,
      @OperationParam(name = "searchDistance", max = 1) NumberParam searchDistance,
      @OperationParam(name = "registeredGP", max = 1) Organization registeredGp,
      @OperationParam(name = "inputParameters", max = 1) Parameters inputParameters
  ) {
    String errorMessage = "%s is required";
    Preconditions.checkNotNull(referralRequest, errorMessage, "referralRequest");
    Preconditions.checkNotNull(patient, errorMessage, "patient");

    boolean forceSearchDistance = inputParameters.getParameter()
        .stream()
        .filter(p -> "forceSearchDistance".equals(p.getName()))
        .findFirst()
        .map(ParametersParameterComponent::getValue)
        .map(BooleanType.class::cast)
        .map(BooleanType::booleanValue)
        .orElse(false);

    var input = InputBundle.builder()
        .referralRequest(referralRequest)
        .patient(patient)
        .requestId(requestId != null ? requestId.getValue() : null)
        .location(location)
        .requester(requester)
        .searchDistance(searchDistance != null ? searchDistance.getValue().intValue() : null)
        .registeredGp(registeredGp)
        .forceSearchDistance(forceSearchDistance)
        .build();

    ucdosService.invokeUCDOS(input);

    var returnedServices = new Parameters();
    healthcareServiceService.getAll().stream()
        .map(service -> new ParametersParameterComponent()
            .setName(service.getId())
            .addPart()
              .setName("service")
              .setResource(service))
        .forEach(returnedServices::addParameter);


    return new ParametersBuilder().add("services", returnedServices).build();
  }
}
