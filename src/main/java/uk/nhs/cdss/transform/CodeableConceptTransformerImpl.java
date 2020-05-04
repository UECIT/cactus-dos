package uk.nhs.cdss.transform;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;

@Component
public class CodeableConceptTransformerImpl implements CodeableConceptTransformer {

  @Override
  public List<CodeableConcept> transform(List<String> types, Class<? extends uk.nhs.cdss.model.CodeableConcept> conceptClass) {
    return types.stream()
        .map(type -> {
          uk.nhs.cdss.model.CodeableConcept matchingType = Arrays.stream(conceptClass.getEnumConstants())
              .filter(serviceType -> serviceType.getDisplay().equals(type))
              .findFirst()
              .orElseThrow(IllegalArgumentException::new);

          Coding coding = new Coding()
              .setCode(matchingType.getCode())
              .setSystem(matchingType.getSystem())
              .setDisplay(matchingType.getDisplay());

          return new CodeableConcept()
              .addCoding(coding);
        })
        .collect(Collectors.toList());
  }
}
