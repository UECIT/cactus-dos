package uk.nhs.cdss.model.ucdos;

import java.util.List;
import lombok.Data;

@Data

public class DosRestResponse {
  private String status;
  private String code;
  private String transactionId;
  private boolean catchAll;
  private int serviceCount;
  private List<Service> services;
}
