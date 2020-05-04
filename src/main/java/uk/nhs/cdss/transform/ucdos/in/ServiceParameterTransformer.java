package uk.nhs.cdss.transform.ucdos.in;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.ucdos.in.bundle.ServiceParameterBundle;

@Component
public class ServiceParameterTransformer
    implements Transformer<ServiceParameterBundle, ParametersParameterComponent> {

  @Override
  public ParametersParameterComponent transform(ServiceParameterBundle bundle) {
    var servicePart = new ParametersParameterComponent()
        .setName("service")
        .setResource(bundle.getService());
    var distancePart = new ParametersParameterComponent()
        .setName("distance")
        .setValue(new StringType(bundle.getDistance()));
    var capacityRagPart = new ParametersParameterComponent()
        .setName("capacityRag")
        .setValue(new StringType(bundle.getCapacityRag().toString()));
    var capacityHumanPart = new ParametersParameterComponent()
        .setName("capacityHuman")
        .setValue(new StringType(bundle.getCapacityHuman()));
    var capacityHexPart = new ParametersParameterComponent()
        .setName("capacityHex")
        .setValue(new StringType(bundle.getCapacityHex()));

    return new ParametersParameterComponent()
        .setName(bundle.getService().getIdentifierFirstRep().getValue())
        .addPart(servicePart)
        .addPart(distancePart)
        .addPart(capacityRagPart)
        .addPart(capacityHumanPart)
        .addPart(capacityHexPart);
  }
}
