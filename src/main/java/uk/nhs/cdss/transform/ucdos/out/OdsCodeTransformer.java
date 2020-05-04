package uk.nhs.cdss.transform.ucdos.out;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;

@Component
public class OdsCodeTransformer implements Transformer<Organization, String> {

  private final static String ODS_CODE_SYSTEM = "https://fhir.nhs.uk/Id/ods-organization-code";

  public String transform(Organization organisation) {
    if (organisation == null) {
      return null;
    }

    return organisation.getIdentifier()
        .stream()
        .filter(i -> ODS_CODE_SYSTEM.equals(i.getSystem()))
        .findFirst()
        .map(Identifier::getValue)
        .orElse(null);
  }

}
