package uk.nhs.cdss.transform.ucdos.out;

import java.util.Date;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.util.DateUtil;

@Component
public class AgeGroupTransformer implements Transformer<Date, Short> {

  public Short transform(Date dob) {
    if (dob == null) {
      return null;
    }

    int age = DateUtil.calculateAge(dob);
    if (age < 0) {
      throw new IllegalArgumentException("Age " + age + " less than 0");
    }
    if (age < 1) {
      return 4;
    }
    if (age < 5) {
      return 3;
    }
    if (age < 16) {
      return 2;
    }
    if (age < 65) { // 'Adults' and 'Older People' both include ages 65-129!?
      return 1;
    }
    return 8;
  }

}
