package uk.nhs.cdss.model;

import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;

@Value
@Builder
public class InputBundle {

  ReferralRequest referralRequest;
  Patient patient;
  String requestId;
  Location location;
  IBaseResource requester; //Practitioner | Patient | Responsible Person
  Integer searchDistance;
  Organization registeredGp;
  boolean forceSearchDistance;

}
