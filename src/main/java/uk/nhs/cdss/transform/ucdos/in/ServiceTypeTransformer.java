package uk.nhs.cdss.transform.ucdos.in;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.constants.Systems;
import uk.nhs.cdss.transform.ucdos.in.bundle.ServiceTypeBundle;

@Component
public class ServiceTypeTransformer implements Transformer<ServiceTypeBundle, CodeableConcept>
{
  @Override
  public CodeableConcept transform(ServiceTypeBundle bundle) {
    var serviceTypeConcept = new CodeableConcept();

    serviceTypeConcept.setText(bundle.getName());
    serviceTypeConcept.addCoding()
        .setSystem(Systems.ODS)
        .setCode(bundle.getId())
        .setDisplay(bundle.getName());

    return serviceTypeConcept;
  }
}
