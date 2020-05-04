package uk.nhs.cdss.transform;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.ContactPoint;

@Component
public class ContactPointTransformer implements Transformer<List<ContactPoint>, List<org.hl7.fhir.dstu3.model.ContactPoint>> {

  @Override
  public List<org.hl7.fhir.dstu3.model.ContactPoint> transform(List<ContactPoint> contactPoints) {
    return contactPoints.stream()
        .map(contactPoint -> new org.hl7.fhir.dstu3.model.ContactPoint()
            .setSystem(ContactPointSystem.fromCode(contactPoint.getType()))
            .setValue(contactPoint.getValue())
            .setUse(ContactPointUse.fromCode(contactPoint.getUsage())))
        .collect(Collectors.toList());
  }

}
