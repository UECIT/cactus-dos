package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.service.HealthcareServiceService;

@AllArgsConstructor
@Component
public class HealthcareServiceProvider implements IResourceProvider {

  private HealthcareServiceService healthcareServiceService;

  @Read
  public HealthcareService get(@IdParam IdType id) {
    return healthcareServiceService.getAll().stream()
        .filter(hcs -> hcs.getId().equals(id.getIdPart()))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(id));
  }


  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return HealthcareService.class;
  }
}
