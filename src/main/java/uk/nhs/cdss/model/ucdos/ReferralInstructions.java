package uk.nhs.cdss.model.ucdos;

import lombok.Data;

@Data
public class ReferralInstructions {
  private String publicInformation; // labelled 'callHandler'
  private String referralInformation; // labelled 'other'
}
