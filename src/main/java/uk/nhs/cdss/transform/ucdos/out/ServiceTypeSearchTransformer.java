package uk.nhs.cdss.transform.ucdos.out;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.InputBundle;
import uk.nhs.cdss.model.ucdos.ServiceTypeSearch;

@Component
@AllArgsConstructor
public class ServiceTypeSearchTransformer implements Transformer<InputBundle, ServiceTypeSearch> {

  private AgeGroupTransformer ageGroupTransformer;
  private GenderTransformer genderTransformer;
  private OdsCodeTransformer odsCodeTransformer;

  public ServiceTypeSearch transform(InputBundle inputBundle) {
    var builder = ServiceTypeSearch.builder()
        .caseId("TEST")
        .searchDistance(inputBundle.getSearchDistance())
        .postcode(inputBundle.getLocation().getAddress().getPostalCode())
        .age(ageGroupTransformer.transform(inputBundle.getPatient().getBirthDate()).intValue())
        .gender(genderTransformer.transform(inputBundle.getPatient().getGender()))
        .serviceTypes(new int[]{15}) //TODO: add tickets to do this
        .numberPerType(100); //??

    if (inputBundle.getRegisteredGp() != null) {
      // SYMBOL: represents calling the UCDOS byODSCode REST API and using the result
      builder = builder.gpPracticeId(
          odsCodeTransformer.transform(inputBundle.getRegisteredGp()).hashCode());
    }

    return builder.build();
  }
}
