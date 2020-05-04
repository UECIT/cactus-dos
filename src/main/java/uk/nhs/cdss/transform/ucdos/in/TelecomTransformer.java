package uk.nhs.cdss.transform.ucdos.in;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.ucdos.in.bundle.TelecomBundle;

@Component
public class TelecomTransformer implements Transformer<TelecomBundle, List<ContactPoint>> {

  @Override
  public List<ContactPoint> transform(TelecomBundle bundle) {
    var contactPoints = new ArrayList<ContactPoint>(3);

    contactPoints.add(new ContactPoint()
        .setSystem(ContactPointSystem.PHONE)
        .setRank(1)
        .setUse(ContactPointUse.WORK)
        .setValue(bundle.getPublicPhone()));
    contactPoints.add(new ContactPoint()
        .setSystem(ContactPointSystem.PHONE)
        .setRank(2)
        .setUse(ContactPointUse.MOBILE)
        .setValue(bundle.getNonPublicPhone()));
    contactPoints.add(new ContactPoint()
        .setSystem(ContactPointSystem.FAX)
        .setRank(3)
        .setUse(ContactPointUse.WORK)
        .setValue(bundle.getFax()));
    contactPoints.add(new ContactPoint()
        .setSystem(ContactPointSystem.URL)
        .setRank(4)
        .setUse(ContactPointUse.WORK)
        .setValue(bundle.getUrl()));

    return contactPoints;
  }
}
