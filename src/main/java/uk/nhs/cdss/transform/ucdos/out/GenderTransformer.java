package uk.nhs.cdss.transform.ucdos.out;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.springframework.stereotype.Component;

@Component
public class GenderTransformer implements Transformer<AdministrativeGender, String> {

  public String transform(AdministrativeGender gender) {
    switch (gender) {
      case MALE:
        return "M";
      case FEMALE:
        return "F";
      default:
        return "I";
    }
  }

}
