package uk.nhs.cdss.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableTime {

  private List<String> day;
  private String startTime;
  private String endTime;

}
