package uk.nhs.cdss.model.ucdos;

import lombok.Data;

@Data
public class PhoneContactPoints {
  private String publicPhone;
  private String nonPublicPhone;
  private String fax;
}
