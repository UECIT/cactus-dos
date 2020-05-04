package uk.nhs.cdss.model.ucdos;

import java.util.List;
import lombok.Data;

@Data
public class Service {
  private String serviceId;
  private String serviceName;
  private ServiceType serviceType;
  private String odsCode;
  private String address;
  private String postcode;
  private int easting;
  private int northing;
  private PhoneContactPoints phone;
  private String website;
  private List<OpeningTimeRota> openingTimeRotas;
  private ReferralInstructions referralInstructions;
  private List<String> endpoints;
  private CapacityStatus capacityStatus;
  private float distanceFromPatient;
}
