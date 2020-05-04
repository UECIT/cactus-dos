package uk.nhs.cdss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

  private String line1;
  private String city;
  private String postcode;
  private String country;

}
