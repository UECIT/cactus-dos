package uk.nhs.cdss.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirConfig {
  @Bean
  public FhirContext fhirContext() {
    FhirContext fhirContext = FhirContext.forDstu3();
    fhirContext.setParserErrorHandler(new StrictErrorHandler());

    return fhirContext;
  }

  @Bean
  public IParser fhirParser() {
    return fhirContext().newJsonParser();
  }
}
