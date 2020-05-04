package uk.nhs.cdss.transform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.HealthcareService.HealthcareServiceNotAvailableComponent;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.UnavailableTime;

@Component
public class NotAvailableTransformer implements Transformer<List<UnavailableTime>, List<HealthcareServiceNotAvailableComponent>> {

  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

  @Override
  public List<HealthcareServiceNotAvailableComponent> transform(List<UnavailableTime> unavailableTimes) {
    return unavailableTimes.stream()
        .map(unavailableTime -> new HealthcareServiceNotAvailableComponent()
            .setDescription(unavailableTime.getDescription())
            .setDuring(new Period()
                .setStart(parseDate(unavailableTime.getStart()))
                .setEnd(parseDate(unavailableTime.getEnd()))))
        .collect(Collectors.toList());
  }

  private Date parseDate(String stringDate) {
    try {
      return TIME_FORMAT.parse(stringDate);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Could not parse " + stringDate + " in format" + TIME_FORMAT.toPattern());
    }
  }
}
