package uk.nhs.cdss.builder;


import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;

public class ParametersBuilder {

  private final Parameters parameters;

  public ParametersBuilder() {
    parameters = new Parameters();
  }

  public ParametersBuilder add(String name, Resource resource) {
    if (resource != null) {
      add(name).setResource(resource);
    }
    return this;
  }

  public ParametersBuilder add(String name, String value) {
    return add(name, new StringType(value));
  }
  public ParametersBuilder add(String name, boolean value) {
    return add(name, new BooleanType(value));
  }
  public ParametersBuilder add(String name, Long value) {
    return add(name, new IntegerType(value));
  }
  public ParametersBuilder add(String name, Integer value) {
    return add(name, new IntegerType(value));
  }
  public ParametersBuilder add(String name, Type value) {
    if (value != null) {
      add(name).setValue(value);
    }
    return this;
  }

  public Parameters build() {
    return parameters;
  }

  private ParametersParameterComponent add(String name) {
    return parameters.addParameter().setName(name);
  }
}