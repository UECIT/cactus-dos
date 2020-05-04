package uk.nhs.cdss.transform;

import java.util.List;
import org.hl7.fhir.dstu3.model.CodeableConcept;

public interface CodeableConceptTransformer {

  List<CodeableConcept> transform(List<String> strings, Class<? extends uk.nhs.cdss.model.CodeableConcept> conceptClass);

}
