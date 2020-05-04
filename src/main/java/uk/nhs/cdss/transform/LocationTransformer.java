package uk.nhs.cdss.transform;

import java.util.Collections;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationMode;
import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.Address;

@Component
public class LocationTransformer implements Transformer<Address, Location> {

  @Override
  public Location transform(Address address) {
    return new Location()
        .setName("Default Location")
        .setStatus(LocationStatus.ACTIVE)
        .setDescription("A place you can go")
        .setMode(LocationMode.INSTANCE)
        .setAddress(new org.hl7.fhir.dstu3.model.Address()
            .setLine(Collections.singletonList(new StringType(address.getLine1())))
            .setCity(address.getCity())
            .setPostalCode(address.getPostcode())
            .setCountry(address.getCountry()));
  }
}
