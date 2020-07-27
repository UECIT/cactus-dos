package uk.nhs.cdss.resourceProviders;

import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.OPERATION_TYPE;

import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.param.NumberParam;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
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
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cdss.builder.ParametersBuilder;
import uk.nhs.cdss.model.InputBundle;
import uk.nhs.cdss.service.HealthcareServiceService;
import uk.nhs.cdss.service.UCDOSService;

@RequiredArgsConstructor
@Component
public class CheckServicesProvider {

  private static final String REQUEST_ID = "requestId";
  private static final String REFERRAL_REQUEST = "referralRequest";
  private static final String PATIENT = "patient";
  private static final String LOCATION = "location";
  private static final String REQUESTER = "requester";
  private static final String SEARCH_DISTANCE = "searchDistance";
  private static final String REGISTERED_GP = "registeredGP";
  private static final String INPUT_PARAMETERS = "inputParameters";

  private final HealthcareServiceService healthcareServiceService;
  private final UCDOSService ucdosService;
  private final AuditService auditService;

  @Operation(name = "$check-services")
  public Parameters searchForHealthcareServices(
      @OperationParam(name = REFERRAL_REQUEST, min = 1, max = 1) ReferralRequest referralRequest,
      @OperationParam(name = PATIENT, min = 1, max = 1) Patient patient,
      @OperationParam(name = REQUEST_ID, max = 1) IdType requestId,
      @OperationParam(name = LOCATION, max = 1) Location location,
      @OperationParam(name = REQUESTER, max = 1) IBaseResource requester,
      @OperationParam(name = SEARCH_DISTANCE, max = 1) NumberParam searchDistance,
      @OperationParam(name = REGISTERED_GP, max = 1) Organization registeredGp,
      @OperationParam(name = INPUT_PARAMETERS, max = 1) Parameters inputParameters
  ) {
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.CHECK_SERVICES.getName());
    auditService.addAuditProperty(INTERACTION_ID, requestId.getValue());

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
        .requestId(requestId.getValue())
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
