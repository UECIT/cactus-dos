package uk.nhs.cdss.transform.ucdos.in.bundle;

import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.HealthcareService;
import uk.nhs.cdss.model.enums.Rag;

@Value
@Builder
public class ServiceParameterBundle {

  HealthcareService service;
  String distance;
  Rag capacityRag;
  String capacityHuman;
  String capacityHex;

}
