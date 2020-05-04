package uk.nhs.cdss.model;


import static uk.nhs.cdss.model.ServiceSpecialty.Systems.SNOMED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceSpecialty implements CodeableConcept {

  FAMILY_PRACTICE("419772000", "Family Practice", SNOMED),
  GENERAL_MEDICINE("394802001", "General Medicine", SNOMED);

  private String code;
  private String display;
  private String system;

  static class Systems {
    public static final String SNOMED = "http://www.snomed.org/";
  }
}
