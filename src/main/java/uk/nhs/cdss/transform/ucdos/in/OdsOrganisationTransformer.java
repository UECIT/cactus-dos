package uk.nhs.cdss.transform.ucdos.in;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.enums.IdentifierType;

@Component
public class OdsOrganisationTransformer implements Transformer<String, Organization> {

  @Override
  public Organization transform(String odsCode) {

    var odsIdentifier = new Identifier()
        .setType(IdentifierType.OC.toCodeableConcept())
        .setSystem(IdentifierType.OC.getSystem())
        .setUse(IdentifierUse.OFFICIAL)
        .setValue(odsCode);

    return new Organization().addIdentifier(odsIdentifier);
  }
}
