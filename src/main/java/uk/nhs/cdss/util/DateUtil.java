package uk.nhs.cdss.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtil {

  public int calculateAge(@NonNull Date dateOfBirth) {
    LocalDate localDate;
    if (dateOfBirth instanceof java.sql.Date) {
      localDate = ((java.sql.Date) dateOfBirth).toLocalDate();
    }
    else {
      localDate = dateOfBirth.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }

    return Period.between(localDate, LocalDate.now()).getYears();
  }

}
