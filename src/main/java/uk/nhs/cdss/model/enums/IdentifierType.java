package uk.nhs.cdss.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.constants.Systems;

@Getter
@RequiredArgsConstructor
public enum IdentifierType implements Concept {
  SDS("SDS user id", "https://fhir.nhs.uk/Id/sds-user-id"),
  SDSR("SDS role", "https://fhir.nhs.uk/Id/sds-role-profile-id"),
  OC("ODS organisation code", Systems.ODS);

  private final String value = name();
  private final String display;
  private final String system;
}
