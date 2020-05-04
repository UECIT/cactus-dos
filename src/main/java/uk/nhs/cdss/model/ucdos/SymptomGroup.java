package uk.nhs.cdss.model.ucdos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SymptomGroup {

  ACCIDENT_OR_EMERGENCY(300);

  private int id;

}
