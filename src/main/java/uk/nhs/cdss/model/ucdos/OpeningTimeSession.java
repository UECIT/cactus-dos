package uk.nhs.cdss.model.ucdos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningTimeSession {
  private OpeningTime start;
  private OpeningTime end;
}
