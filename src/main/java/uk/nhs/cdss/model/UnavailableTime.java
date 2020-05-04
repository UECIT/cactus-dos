package uk.nhs.cdss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnavailableTime {

  private String description;
  private String start;
  private String end;

}
