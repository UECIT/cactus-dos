package uk.nhs.cdss.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.servlet.annotation.WebServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import uk.nhs.cdss.resourceProviders.CheckServicesProvider;
import uk.nhs.cdss.resourceProviders.HealthcareServiceProvider;

@WebServlet(urlPatterns = {"/fhir/*"}, displayName = "FHIR Server")
@Configuration
@RequiredArgsConstructor
public class FHIRRestfulServer extends RestfulServer {

  private static final long serialVersionUID = 1L;

  @Value("${dos.fhir.server}")
  private String dosFhirServer;

  private final HealthcareServiceProvider healthcareServiceProvider;
  private final CheckServicesProvider checkServicesProvider;
  private final FhirContext fhirContext;

  @Override
	protected void initialize() {

    setFhirContext(fhirContext);
    setETagSupport(ETagSupportEnum.ENABLED);
    setServerAddressStrategy(new HardcodedServerAddressStrategy(dosFhirServer));

    CorsConfiguration config = new CorsConfiguration();
    config.setMaxAge(10L);
    config.addAllowedOrigin("*");
    config.setAllowCredentials(Boolean.TRUE);
    config.setExposedHeaders(
        Arrays.asList(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    config.setAllowedMethods(
        Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name(), HttpMethod.PATCH.name()));
    config.setAllowedHeaders(Arrays.asList(HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_ENCODING,
        HttpHeaders.ACCEPT_LANGUAGE, HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpHeaders.AUTHORIZATION,
        HttpHeaders.CACHE_CONTROL,
        HttpHeaders.CONNECTION, HttpHeaders.CONTENT_LENGTH, HttpHeaders.CONTENT_TYPE,
        HttpHeaders.COOKIE,
        HttpHeaders.HOST, HttpHeaders.ORIGIN, HttpHeaders.PRAGMA, HttpHeaders.REFERER,
        HttpHeaders.USER_AGENT));

    registerInterceptor(new CorsInterceptor(config));
  }

  @PostConstruct
  public void setResourceProviders() {
    setProviders(healthcareServiceProvider, checkServicesProvider);
  }

}