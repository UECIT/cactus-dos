package uk.nhs.cdss.transform.ucdos.in.bundle;

import lombok.AllArgsConstructor;
import lombok.Value;
import uk.nhs.cdss.model.ucdos.Service;
import uk.nhs.cdss.model.ucdos.wsdl.ServiceCareSummaryDestination;

@Value
@AllArgsConstructor
public class LocationBundle {
  String postcode;
  String address;
  int easting;
  int northing;

  public LocationBundle(ServiceCareSummaryDestination destination) {
    this(
        destination.getPostcode(),
        destination.getAddress(),
        destination.getEastings(),
        destination.getNorthings());
  }

  public LocationBundle(Service service) {
    this(service.getPostcode(), service.getAddress(), service.getEasting(), service.getNorthing());
  }
}
