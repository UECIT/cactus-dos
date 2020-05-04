package uk.nhs.cdss.util;
public class NaivePeriod {
  private static final int DAYS_IN_YEAR = 365;
  private static final int DAYS_IN_MONTH = 31;

  private final int days;

  private NaivePeriod(int days) {
    this.days = days;
  }

  public int getYearsPart() {
    return days / DAYS_IN_YEAR;
  }

  public int getMonthsPart() {
    return (days % DAYS_IN_YEAR) / DAYS_IN_MONTH;
  }

  public int getDaysPart() {
    return days % DAYS_IN_MONTH;
  }

  public String toDateString() {
    return String.format(
        "%d years, %d months, %d days",
        getYearsPart(),
        getMonthsPart(),
        getDaysPart());
  }

  public static NaivePeriod ofDays(int days) {
    return new NaivePeriod(days);
  }

  public static NaivePeriod ofYears(int years) {
    return new NaivePeriod(years * DAYS_IN_YEAR);
  }
}
