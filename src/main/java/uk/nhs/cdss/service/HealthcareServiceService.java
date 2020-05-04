package uk.nhs.cdss.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.registry.HealthcareServiceRegistry;
import uk.nhs.cdss.transform.HealthcareServiceTransformer;

@Service
@AllArgsConstructor
public class HealthcareServiceService {

  private HealthcareServiceRegistry healthcareServiceRegistry;
  private HealthcareServiceTransformer healthcareServiceTransformer;

  public List<HealthcareService> getAll() {
    return healthcareServiceRegistry.getAll().stream()
        .map(healthcareServiceTransformer::transform)
        .collect(Collectors.toList());
  }
}
