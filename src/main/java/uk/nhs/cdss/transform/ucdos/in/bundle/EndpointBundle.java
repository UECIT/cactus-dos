package uk.nhs.cdss.transform.ucdos.in.bundle;

import lombok.AllArgsConstructor;
import lombok.Value;
import uk.nhs.cdss.model.ucdos.wsdl.Endpoint;

@Value
@AllArgsConstructor
public class EndpointBundle {
  String address;
  String transport;
  String format;

  public EndpointBundle(Endpoint endpoint) {
    this(endpoint.getAddress(), endpoint.getTransport(), endpoint.getFormat());
  }

  public EndpointBundle(String website) {
    this(website, null, null);
  }
}
