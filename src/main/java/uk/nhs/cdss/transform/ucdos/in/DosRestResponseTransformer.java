package uk.nhs.cdss.transform.ucdos.in;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.HealthcareService.DaysOfWeek;
import org.hl7.fhir.dstu3.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.builder.ParametersBuilder;
import uk.nhs.cdss.constants.Systems;
import uk.nhs.cdss.model.ucdos.DosRestResponse;
import uk.nhs.cdss.model.ucdos.OpeningTime;
import uk.nhs.cdss.model.ucdos.OpeningTimeRota;
import uk.nhs.cdss.model.ucdos.ReferralInstructions;
import uk.nhs.cdss.model.ucdos.Service;
import uk.nhs.cdss.transform.ucdos.in.bundle.EndpointBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.LocationBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.ServiceParameterBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.ServiceTypeBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.TelecomBundle;

@Component
@RequiredArgsConstructor
public class DosRestResponseTransformer implements Transformer<DosRestResponse, Parameters> {

  private final ServiceTypeTransformer serviceTypeTransformer;
  private final OdsOrganisationTransformer odsOrganisationTransformer;
  private final DosLocationTransformer locationTransformer;
  private final TelecomTransformer telecomTransformer;
  private final DosEndpointTransformer endpointTransformer;
  private final ServiceParameterTransformer serviceParameterTransformer;

  @Override
  public Parameters transform(DosRestResponse from) {
    var outputParameters = new ParametersBuilder()
        .add("TransactionID", from.getTransactionId())
        .add("Status", from.getStatus())
        .add("Code", from.getCode())
        .add("CatchAll", from.isCatchAll())
        .add("ServiceCount", from.getServiceCount())
        .build();

    var services = new Parameters();
    from.getServices()
        .stream()
        .map(this::transformService)
        .forEach(services::addParameter);

    return new ParametersBuilder()
        .add("services", services)
        .add("outputParameters", outputParameters)
        .build();
  }

  private ParametersParameterComponent transformService(Service from) {
    var service = new HealthcareService();

    service.addIdentifier().setSystem(Systems.ODS).setValue(from.getServiceId());
    service.setName(from.getServiceName());
    service.addType(serviceTypeTransformer.transform(new ServiceTypeBundle(from.getServiceType())));
    service.setProvidedByTarget(odsOrganisationTransformer.transform(from.getOdsCode()));
    service.addLocation(locationTransformer.transform(new LocationBundle(from)));
    service.setExtraDetails(transformReferralInfo(from.getReferralInstructions()));
    service.setTelecom(telecomTransformer.transform(
        new TelecomBundle(from.getPhone(), from.getWebsite())));
    from.getOpeningTimeRotas()
        .stream()
        .map(this::transformRota)
        .forEach(service::addAvailableTime);
    service.setAvailabilityExceptions(transformAvailabilityExceptions(from.getOpeningTimeRotas()));
    from.getEndpoints()
        .stream()
        .map(EndpointBundle::new)
        .map(endpointTransformer::transform)
        .forEachOrdered(service::addEndpoint);

    var serviceParameterBundle = ServiceParameterBundle.builder()
        .service(service)
        .distance(Float.toString(from.getDistanceFromPatient()))
        .capacityHuman(from.getCapacityStatus().getHuman())
        .capacityRag(from.getCapacityStatus().getRag())
        .capacityHex(from.getCapacityStatus().getHex())
        .build();

    return serviceParameterTransformer.transform(serviceParameterBundle);
  }

  private String transformReferralInfo(ReferralInstructions info) {
    return String.join(
        System.lineSeparator(),
        "For call handlers:",
        info.getPublicInformation(),
        System.lineSeparator(),
        "For anyone else:",
        info.getReferralInformation());
  }

  private String transformAvailabilityExceptions(List<OpeningTimeRota> rotas) {
    return rotas.stream()
        .filter(r -> r.getSpecifiedDates() != null)
        .map(r -> String.format(
            "Only available from %s to %s on the dates %s:",
            transformTime(r.getSession().getStart()),
            transformTime(r.getSession().getEnd()),
            r.getSpecifiedDates()))
        .collect(Collectors.joining(System.lineSeparator()));
  }

  private HealthcareServiceAvailableTimeComponent transformRota(OpeningTimeRota rota) {
    var availableTime = new HealthcareServiceAvailableTimeComponent();

    availableTime.setAllDay(rota.isOpenAllHours());
    if (rota.getDay() != null) {
      availableTime.addDaysOfWeek(DaysOfWeek.fromCode(rota.getDay().toLowerCase().substring(0, 3)));
    }
    availableTime.setAvailableStartTime(transformTime(rota.getSession().getStart()));
    availableTime.setAvailableEndTime(transformTime(rota.getSession().getEnd()));

    return availableTime;
  }

  private String transformTime(OpeningTime time) {
    return String.format("%02d:%02d", time.getHours(), time.getMinutes());
  }
}
