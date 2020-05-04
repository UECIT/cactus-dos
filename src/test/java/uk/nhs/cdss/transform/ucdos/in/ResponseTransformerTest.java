package uk.nhs.cdss.transform.ucdos.in;

import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Parameters;
import org.junit.Before;
import uk.nhs.cdss.model.ucdos.DosRestResponse;
import uk.nhs.cdss.model.ucdos.wsdl.CheckCapacitySummaryResponse;
import uk.nhs.cdss.registry.CheckSummaryResponseRegistry;
import uk.nhs.cdss.registry.DosRestResponseRegistry;
import uk.nhs.cdss.util.ParametersExtractor;

@SuppressWarnings("unchecked")
public abstract class ResponseTransformerTest<T> {

  protected abstract Class<T> getType();

  protected T initial;
  protected ParametersExtractor transformed;

  @Before
  public void performTransformation() {
    initial = getSupplier().get();
    transformed = new ParametersExtractor(getTransformer().transform(initial));
  }

  @SneakyThrows
  private Supplier<T> getSupplier() {
    var type = getType();
    if (DosRestResponse.class.equals(type)) {
      return (Supplier<T>) new DosRestResponseRegistry();
    }
    if (CheckCapacitySummaryResponse.class.equals(type)) {
      return (Supplier<T>) new CheckSummaryResponseRegistry();
    }

    throw new ClassNotFoundException("No supplier found for type " + type.getSimpleName());
  }

  @SneakyThrows
  private Transformer<T, Parameters> getTransformer() {
    var type = getType();
    var serviceTypeTransformer = new ServiceTypeTransformer();
    var odsTransformer = new OdsOrganisationTransformer();
    var locationTransformer = new DosLocationTransformer();
    var telecomTransformer = new TelecomTransformer();
    var endpointTransformer = new DosEndpointTransformer();
    var serviceParameterTransformer = new ServiceParameterTransformer();

    if (DosRestResponse.class.equals(type)) {
      return (Transformer<T, Parameters>) new DosRestResponseTransformer(
          serviceTypeTransformer,
          odsTransformer,
          locationTransformer,
          telecomTransformer,
          endpointTransformer,
          serviceParameterTransformer);
    }
    if (CheckCapacitySummaryResponse.class.equals(type)) {
      return (Transformer<T, Parameters>) new CheckCapacityResponseTransformer(
          serviceTypeTransformer,
          odsTransformer,
          locationTransformer,
          telecomTransformer,
          endpointTransformer,
          serviceParameterTransformer);
    }

    throw new ClassNotFoundException("No transformer found for type " + type.getSimpleName());
  }
}
