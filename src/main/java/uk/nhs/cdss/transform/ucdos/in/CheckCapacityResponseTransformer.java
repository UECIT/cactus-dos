package uk.nhs.cdss.transform.ucdos.in;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import uk.nhs.cdss.model.enums.Rag;
import uk.nhs.cdss.model.ucdos.wsdl.AgeRange;
import uk.nhs.cdss.model.ucdos.wsdl.ArrayOfServiceAgeRanges;
import uk.nhs.cdss.model.ucdos.wsdl.Capacity;
import uk.nhs.cdss.model.ucdos.wsdl.CheckCapacitySummaryResponse;
import uk.nhs.cdss.model.ucdos.wsdl.DayOfWeek;
import uk.nhs.cdss.model.ucdos.wsdl.Endpoint;
import uk.nhs.cdss.model.ucdos.wsdl.ServiceCareItemRotaSession;
import uk.nhs.cdss.model.ucdos.wsdl.ServiceCareSummaryDestination;
import uk.nhs.cdss.model.ucdos.wsdl.TimeOfDay;
import uk.nhs.cdss.transform.ucdos.in.bundle.EndpointBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.LocationBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.ServiceParameterBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.ServiceTypeBundle;
import uk.nhs.cdss.transform.ucdos.in.bundle.TelecomBundle;
import uk.nhs.cdss.util.NaivePeriod;

@Component
@RequiredArgsConstructor
public class CheckCapacityResponseTransformer
    implements Transformer<CheckCapacitySummaryResponse, Parameters> {

  private final ServiceTypeTransformer serviceTypeTransformer;
  private final OdsOrganisationTransformer odsOrganisationTransformer;
  private final DosLocationTransformer locationTransformer;
  private final TelecomTransformer telecomTransformer;
  private final DosEndpointTransformer endpointTransformer;
  private final ServiceParameterTransformer serviceParameterTransformer;

  @Override
  public Parameters transform(CheckCapacitySummaryResponse from) {
    var outputParametersBuilder = new ParametersBuilder()
        .add("TransactionID", from.getTransactionId())
        .add("RequestedAtDateTime", from.getRequestedAtDateTime())
        .add("SearchDateTime", from.getSearchDateTime())
        .add("SearchDistance", from.getSearchDistance())
        .add("SearchDistanceUsedSource", from.getSearchDistanceUsedSource().value());
    if (from.getCalculatedAgeInDays() != null) {
      outputParametersBuilder.add(
          "CalculatedAgeInDays",
          from.getCalculatedAgeInDays().longValue());
    }

    var services = new Parameters();
    from.getCheckCapacitySummaryResult()
        .getServiceCareSummaryDestination()
        .stream()
        .map(this::transformService)
        .forEach(services::addParameter);

    return new ParametersBuilder()
        .add("services", services)
        .add("outputParameters", outputParametersBuilder.build())
        .build();
  }

  private ParametersParameterComponent transformService(ServiceCareSummaryDestination destination) {
    var service = new HealthcareService();

    service.addIdentifier().setSystem(Systems.ODS).setValue(Integer.toString(destination.getId()));
    service.setName(destination.getName());
    service.addProgramName(destination.getPublicName());
    service.setComment(destination.getPublicFacingInformation());
    service.addType(serviceTypeTransformer.transform(
        new ServiceTypeBundle(destination.getServiceType())));
    service.setTelecom(telecomTransformer.transform(new TelecomBundle(destination)));
    service.setProvidedByTarget(odsOrganisationTransformer.transform(destination.getOdsCode()));
    service.addLocation(locationTransformer.transform(new LocationBundle(destination)));
    service.setExtraDetails(destination.getReferralInformation());
    service.setAvailableTime(transformAvailability(destination));
    service.setEligibilityNote(transformEligibility(destination.getServiceAgeRange()));
    service.setAvailabilityExceptions(transformAvailabilityExceptions(destination));
    destination.getServiceEndpoints()
        .getEndpoint()
        .stream()
        .sorted(Comparator.comparingInt(Endpoint::getEndpointOrder))
        .map(EndpointBundle::new)
        .map(endpointTransformer::transform)
        .forEachOrdered(service::addEndpoint);

    // destination.capacityAttributes - will not be returned
    // destination.attributes - will not be returned

    var serviceParameterBundle = ServiceParameterBundle.builder()
        .service(service)
        .distance(destination.getDistance())
        .capacityHuman(destination.getCapacity().value())
        .capacityRag(transformCapacity(destination.getCapacity()))
        .build();

    return serviceParameterTransformer.transform(serviceParameterBundle);
  }

  private Rag transformCapacity(Capacity capacity) {
    switch (capacity) {
      case HIGH: return Rag.GREEN;
      case LOW: return Rag.AMBER;
      default: return Rag.RED;
    }
  }

  private String transformEligibility(ArrayOfServiceAgeRanges ageRange) {
    var ageRanges = ageRange.getAgeRange()
        .stream()
        .map(this::transformAgeRange);

    return Stream.concat(Stream.of("Patient's age must be within one of the ranges:"), ageRanges)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  private String transformAgeRange(AgeRange range) {
    var start = NaivePeriod.ofDays((int) range.getDaysFrom());
    var end = NaivePeriod.ofDays((int) range.getDaysTo());
    return String.format("%s old to %s old", start.toDateString(), end.toDateString());
  }

  private List<HealthcareServiceAvailableTimeComponent> transformAvailability(
      ServiceCareSummaryDestination destination) {
    return destination.getRotaSessions()
        .getServiceCareItemRotaSession()
        .stream()
        .map(this::transformRota)
        .peek(r -> r.setAllDay(destination.isOpenAllHours()))
        .collect(Collectors.toUnmodifiableList());
  }

  private String transformAvailabilityExceptions(ServiceCareSummaryDestination destination) {
    var specificSessions = destination.getOpenTimeSpecifiedSessions()
        .getOpenTimeSpecified()
        .stream();
    var bankHolidays = destination.getRotaSessions()
        .getServiceCareItemRotaSession()
        .stream()
        .filter(r -> r.getStartDayOfWeek() == DayOfWeek.BANKHOLIDAY
            || r.getEndDayOfWeek() == DayOfWeek.BANKHOLIDAY)
        .findFirst()
        .map(r -> String.format(
            "On bank holidays: from %s to %s",
            transformTime(r.getStartTime()),
            transformTime(r.getEndTime())))
        .stream();

    return Stream.concat(specificSessions, bankHolidays)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  private HealthcareServiceAvailableTimeComponent transformRota(
      ServiceCareItemRotaSession rota) {
    var availableTime = new HealthcareServiceAvailableTimeComponent();

    getDaysBetween(rota.getStartDayOfWeek(), rota.getEndDayOfWeek())
        .forEach(availableTime::addDaysOfWeek);
    availableTime.setAvailableStartTime(transformTime(rota.getStartTime()));
    availableTime.setAvailableEndTime(transformTime(rota.getEndTime()));

    return availableTime;
  }

  private String transformTime(TimeOfDay time) {
    return String.format("%02d:%02d", time.getHours(), time.getMinutes());
  }

  private DaysOfWeek transformDayOfWeek(DayOfWeek day) {
    switch (day) {
      case MONDAY: return DaysOfWeek.MON;
      case TUESDAY: return DaysOfWeek.TUE;
      case WEDNESDAY: return DaysOfWeek.WED;
      case THURSDAY: return DaysOfWeek.THU;
      case FRIDAY: return DaysOfWeek.FRI;
      case SATURDAY: return DaysOfWeek.SAT;
      case SUNDAY: return DaysOfWeek.SUN;
      default: return DaysOfWeek.NULL;
    }
  }

  private List<DaysOfWeek> getDaysBetween(DayOfWeek start, DayOfWeek end) {
    // for an ideal mapping Bank Holiday availability can be added to
    // HealthcareService.availabilityExceptions or HealthcareService.notAvailable
    if (start == DayOfWeek.BANKHOLIDAY || end == DayOfWeek.BANKHOLIDAY) {
      return singletonList(DaysOfWeek.NULL);
    }
    if (start == end) {
      return singletonList(transformDayOfWeek(start));
    }

    var ordinals = new ArrayList<Integer>(7);
    for (var i = start.ordinal(); i != end.ordinal(); i =  (i + 1) % 7) {
      ordinals.add(i);
    }
    DayOfWeek[] possibleDays = DayOfWeek.values();
    return ordinals.stream()
        .map(i -> possibleDays[i])
        .map(this::transformDayOfWeek)
        .collect(Collectors.toUnmodifiableList());
  }
}
