package uk.nhs.cdss.transform.ucdos.in;

import static org.hamcrest.Matchers.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import lombok.experimental.UtilityClass;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;

@UtilityClass
public class FhirMatchers {
  public Matcher<Object> isCodeableConcept(String system, String code, String display) {
    return both(hasProperty("text", equalTo(display)))
        .and(hasProperty("coding", contains(isCoding(system, code, display))));
  }
  public Matcher<Object> isCoding(String system, String code, String display) {
    return both(hasProperty("system", equalTo(system)))
        .and(hasProperty("code", equalTo(code)))
        .and(hasProperty("display", equalTo(display)));
  }

  public Matcher<ContactPoint> isContactPoint(
      int rank,
      ContactPointSystem system,
      ContactPointUse use,
      String value) {
    return is(both(Matchers.<ContactPoint>hasProperty("rank", equalTo(rank)))
        .and(hasProperty("system", equalTo(system)))
        .and(hasProperty("use", equalTo(use)))
        .and(hasProperty("value", isEmpty(value)
            ? isEmptyOrNullString()
            : equalTo(value))));
  }
}
