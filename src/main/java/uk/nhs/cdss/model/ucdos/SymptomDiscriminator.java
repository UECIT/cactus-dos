package uk.nhs.cdss.model.ucdos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SymptomDiscriminator {

  PCR_WITH_FEATURES_OF_SUBSTANCE_OR_ALCOHOL_MISUSE(11018);

  private int id;
}
