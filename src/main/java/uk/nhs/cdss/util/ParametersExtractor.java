package uk.nhs.cdss.util;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;

@RequiredArgsConstructor
public class ParametersExtractor {

  private final List<ParametersParameterComponent> params;

  public ParametersExtractor(Parameters parameters) {
    this.params = parameters.getParameter();
  }

  public ParametersExtractor getParameters(String name) {
    return new ParametersExtractor(getResource(name, Parameters.class));
  }

  public <R extends Resource> R getResource(String name, Class<R> type) {
    return type.cast(getComponent(name).getResource());
  }

  public boolean getBoolean(String name) {
    return getValue(name, BooleanType.class).getValue();
  }
  public int getInt(String name) {
    return getValue(name, IntegerType.class).getValue();
  }
  public String getString(String name) {
    return getValue(name, StringType.class).getValue();
  }

  public <R extends Type> R getValue(String name, Class<R> type) {
    return type.cast(getComponent(name).getValue());
  }

  public ParametersExtractor getPartsOf(String name) {
    return new ParametersExtractor(getComponent(name).getPart());
  }

  public ParametersParameterComponent getComponent(String name){
    return params.stream()
        .filter(p -> name.equals(p.getName()))
        .findFirst()
        .orElseThrow();
  }
}
