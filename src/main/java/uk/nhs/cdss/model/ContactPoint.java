package uk.nhs.cdss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactPoint {

  private String type;
  private String value;
  private String usage;

}
