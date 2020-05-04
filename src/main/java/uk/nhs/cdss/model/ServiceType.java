package uk.nhs.cdss.model;

import static uk.nhs.cdss.model.ServiceType.Systems.SERVICE_TYPE;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceType implements CodeableConcept {

  MEALS_ON_WHEELS("6", "Delivered Meals (Meals On Wheels)", SERVICE_TYPE),
  FOOD("344", "Food", SERVICE_TYPE),
  MUSIC_THERAPY("591", "Music Therapy", SERVICE_TYPE);

  private String code;
  private String display;
  private String system;

  static class Systems {
    public static final String SERVICE_TYPE = "http://hl7.org/fhir/service-type";
  }

}
