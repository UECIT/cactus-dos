package uk.nhs.cdss.model;

import static uk.nhs.cdss.model.Provision.Systems.PROVISION_CONDITIONS;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provision implements CodeableConcept{

  FREE("free", "Free", PROVISION_CONDITIONS),
  DISCOUNT("disc", "Discounts Available", PROVISION_CONDITIONS),
  COST("cost", "Fees apply", PROVISION_CONDITIONS);

  private String code;
  private String display;
  private String system;

  static class Systems {
    public static final String PROVISION_CONDITIONS = "http://hl7.org/fhir/stu3/codesystem-service-provision-conditions.html";
  }

}
