package uk.nhs.cdss.transform.ucdos.in;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Endpoint;
import org.hl7.fhir.dstu3.model.Endpoint.EndpointStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.ucdos.in.bundle.EndpointBundle;

@Component
public class DosEndpointTransformer implements Transformer<EndpointBundle, Reference> {

  @Override
  public Reference transform(EndpointBundle bundle) {
    var serviceEndpoint = new Endpoint();

    serviceEndpoint.setStatus(EndpointStatus.ACTIVE);
    serviceEndpoint.setAddress(bundle.getAddress());

    if (bundle.getTransport() == null) {
      serviceEndpoint.setConnectionType(
          new Coding("DoS", "itk", "Default itk transport type"));
    } else {
      serviceEndpoint.setConnectionType(
          new Coding("DoS", bundle.getTransport(), bundle.getTransport()));
    }

    if (bundle.getFormat() == null) {
      var format = "111 format";
      serviceEndpoint.addPayloadType()
          .setText(format)
          .addCoding()
          .setSystem("DoS")
          .setCode(format)
          .setDisplay(format);
    } else {
      serviceEndpoint.addPayloadType()
          .setText(bundle.getFormat())
          .addCoding()
          .setSystem("DoS")
          .setCode(bundle.getFormat())
          .setDisplay(bundle.getFormat());
    }

    return new Reference(serviceEndpoint);
  }
}
