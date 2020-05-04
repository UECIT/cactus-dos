package uk.nhs.cdss.transform;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.HealthcareService.DaysOfWeek;
import org.hl7.fhir.dstu3.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.AvailableTime;

@Component
public class AvailableTimeTransformer implements Transformer<List<AvailableTime>, List<HealthcareServiceAvailableTimeComponent>> {

  @Override
  public List<HealthcareServiceAvailableTimeComponent> transform(
      List<AvailableTime> availableTimes) {
    return availableTimes.stream()
        .map(availableTime -> {
          HealthcareServiceAvailableTimeComponent time = new HealthcareServiceAvailableTimeComponent();
          availableTime.getDay().stream()
              .map(DaysOfWeek::fromCode)
              .forEach(time::addDaysOfWeek);
          time.setAvailableStartTime(availableTime.getStartTime());
          time.setAvailableEndTime(availableTime.getEndTime());
          return time;
        })
        .collect(Collectors.toList());
  }
}
