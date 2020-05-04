package uk.nhs.cdss.model.ucdos;

import lombok.Data;
import uk.nhs.cdss.model.enums.Rag;

@Data
public class CapacityStatus {
  private Rag rag;
  private String human;
  private String hex;
}
