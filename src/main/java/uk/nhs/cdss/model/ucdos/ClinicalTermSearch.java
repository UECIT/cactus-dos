package uk.nhs.cdss.model.ucdos;

import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClinicalTermSearch {

  String caseId;
  Integer searchDistance;
  String postcode;
  Integer gpPracticeId;
  Integer age;
  String gender;
  Map<SymptomGroup, SymptomDiscriminator> symptomGroupDiscriminatorCombos;
  Integer numberPerType;

}
