package uk.nhs.cdss.transform.ucdos.in.bundle;

import lombok.AllArgsConstructor;
import lombok.Value;
import uk.nhs.cdss.model.ucdos.wsdl.ServiceType;

@Value
@AllArgsConstructor
public class ServiceTypeBundle {
  String id;
  String name;

  public ServiceTypeBundle(ServiceType serviceType) {
    this(Long.toString(serviceType.getId()), serviceType.getName());
  }
  public ServiceTypeBundle(uk.nhs.cdss.model.ucdos.ServiceType serviceType) {
    this(serviceType.getId(), serviceType.getName());
  }
}
