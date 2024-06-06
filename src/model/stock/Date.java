package model.stock;

/**
 * A class representing a date containing a day, month, and year.
 */
public class Date {

  private int day;
  private int month;
  private int year;
  private final int[] DAYS_IN_MONTH = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  /**
   * Constructs a date given a day, month, and year and
   * throws an IllegalArgumentException if the date is invalid.
   *
   * @param day   the day of the month
   * @param month the month of the year
   * @param year  the year
   * @throws IllegalArgumentException if the given date is invalid
   */
  public Date(int day, int month, int year) throws IllegalArgumentException {
    this.day = day;
    this.month = month;
    this.year = year;
    if (!isValidDate(day, month, year)) {
      throw new IllegalArgumentException();
    }
  }

  private boolean isValidDate(int day, int month, int year) {
    if (day < 1 || month < 1 || month > 12 || year < 0) {
      return false;
    } else if (isLeapYear(year) && month == 2) {
      return day <= 29;
    } else {
      return day <= DAYS_IN_MONTH[month];
    }
  }

  private boolean isLeapYear(int year) {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
  }

  /**
   * Progresses the date given a number of days. Recedes the date
   * if the given number of days is negative.
   *
   * @param days number of days to progress or recede
   */
  public void advance(int days) {
    if (days > 0) {
      advancePositive(days);
    } else if (days < 0) {
      advanceNegative(days);
    }
  }

  private void advancePositive(int days) {
    day += days;
    while (day > DAYS_IN_MONTH[month]) {
      if (isLeapYear(year) && month == 2) {
        if (day > 29) {
          day -= 29;
          month++;
        } else {
          break;
        }
      } else {
        day -= DAYS_IN_MONTH[month];
        month++;
      }
      if (month > 12) {
        month = 1;
        year++;
      }
    }
  }

  private void advanceNegative(int days) {
    day += days;
    int daysInMonth;
    while (day < 1) {
      month--;
      if (month < 1) {
        month = 12;
        year--;
      }
      if (isLeapYear(year) && month == 2) {
        daysInMonth = 29;
      } else {
        daysInMonth = DAYS_IN_MONTH[month];
      }
      day += daysInMonth;
    }
  }


  /**
   * Represents the date in a YYYY-MM-DD format.
   *
   * @return String representation of the date
   */
  public String toString() {
    return String.format("%04d-%02d-%02d", year, month, day);
  }
}
