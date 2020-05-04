package uk.nhs.cdss.transform.ucdos.in.bundle;

import lombok.AllArgsConstructor;
import lombok.Value;
import uk.nhs.cdss.model.ucdos.PhoneContactPoints;
import uk.nhs.cdss.model.ucdos.wsdl.ServiceCareSummaryDestination;

@Value
@AllArgsConstructor
public class TelecomBundle {
  String publicPhone;
  String nonPublicPhone;
  String fax;
  String url;

  public TelecomBundle(ServiceCareSummaryDestination destination) {
    this(
        destination.getContactDetails(),
        destination.getNonPublicTelephoneNo(),
        destination.getFax(),
        destination.getUrl());
  }

  public TelecomBundle(PhoneContactPoints contact, String websiteUrl) {
    this(contact.getPublicPhone(), contact.getNonPublicPhone(), contact.getFax(), websiteUrl);
  }
}
