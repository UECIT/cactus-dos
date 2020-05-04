package uk.nhs.cdss.model.ucdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningTimeRota {
  private boolean openAllHours;
  private String day;
  private OpeningTimeSession session;
  private String specifiedDates;
}
