package uk.nhs.cdss.transform.ucdos.in;

import java.util.Arrays;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationPositionComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.ucdos.in.bundle.LocationBundle;

@Component
public class DosLocationTransformer implements Transformer<LocationBundle, Reference> {

  @Override
  public Reference transform(LocationBundle bundle) {
    var address = new Address();
    Arrays.stream(bundle.getAddress().split("\n")).forEach(address::addLine);
    address.setPostalCode(bundle.getPostcode());

    var location = new Location();
    location.setAddress(address);

    // The actual coordinate transformation is out of scope/not worth it
    location.setPosition(new LocationPositionComponent(
        // pretend these values were transformed
        new DecimalType(123.4),   // from destination.easting
        new DecimalType(567.8))); // and  destination.northing

    return new Reference(location);
  }
}
