package uk.nhs.cdss.model.ucdos;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceTypeSearch {

  String caseId;
  Integer searchDistance;
  String postcode;
  Integer gpPracticeId;
  Integer age;
  String gender;
  int[] serviceTypes;
  Integer numberPerType;

}
