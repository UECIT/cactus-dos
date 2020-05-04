package uk.nhs.cdss.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthcareService {

  private long id;
  private List<Endpoint> endpoints;
  private boolean active;
  private String name;
  private boolean appointmentRequired;
  private List<String> serviceTypes;
  private List<String> serviceSpecialties;
  private Address address;
  private String description;
  private String extraDetails;
  private List<ContactPoint> contacts;
  private List<String> provisions;
  private List<AvailableTime> availableTimes;
  private List<UnavailableTime> unavailableTimes;

}
