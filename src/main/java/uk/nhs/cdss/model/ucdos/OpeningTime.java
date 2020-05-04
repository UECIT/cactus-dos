package uk.nhs.cdss.model.ucdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningTime {
  private int hours;
  private int minutes;
}
