package uk.nhs.cdss.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FhirConfig {

  private final List<IClientInterceptor> clientInterceptors;

  @Bean
  public FhirContext fhirContext() {
    FhirContext fhirContext = FhirContext.forDstu3();
    fhirContext.setParserErrorHandler(new StrictErrorHandler());

    return fhirContext;
  }

  @PostConstruct
  private void configureClientInterceptors() {
    ApacheRestfulClientFactory factory = new ApacheRestfulClientFactory() {
      @Override
      public synchronized IGenericClient newGenericClient(String theServerBase) {
        IGenericClient client = super.newGenericClient(theServerBase);

        for (IClientInterceptor interceptor : clientInterceptors) {
          client.registerInterceptor(interceptor);
        }
        return client;
      }
    };

    FhirContext fhirContext = fhirContext();
    factory.setFhirContext(fhirContext);
    fhirContext.setRestfulClientFactory(factory);
  }
}
