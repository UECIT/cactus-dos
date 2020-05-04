package uk.nhs.cdss.registry;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.HealthcareService;

@Component
public class HealthcareServiceRegistry {

  public List<HealthcareService> getAll() {

    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    try {
      Resource[] resources = resolver.getResources("/healthcareservice/*");
      return Arrays.stream(resources)
          .map(this::readValue)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new ResourceNotFoundException("Resources not found: " + e.getMessage());
    }

  }

  private HealthcareService readValue(Resource resource) {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      return objectMapper.readValue(resource.getInputStream(), HealthcareService.class);
    } catch (IOException e) {
      throw new ResourceNotFoundException("Resources not found: " + e.getMessage());
    }
  }

}
