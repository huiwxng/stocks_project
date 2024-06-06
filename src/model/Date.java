package model.stock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a date containing a day, month, and year.
 */
public class Date {

  private String date;
  private int day;
  private int month;
  private int year;
  private final int[] DAYS_IN_MONTH = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  /**
   * Constructs a date given a string in the format of 'YYYY-MM-DD' and
   * throws an IllegalArgumentException if the date is invalid.
   *
   * @param date String of the given day
   * @throws IllegalArgumentException if the date is invalid or the date is before the current date
   */
  public Date(String date) throws IllegalArgumentException {
    List<Integer> arr = parseDate(date);
    this.date = date;
    this.year = arr.get(0);
    this.month = arr.get(1);
    this.day = arr.get(2);

    LocalDate today = LocalDate.now();
    String td = today.toString();
    if (!(isBefore(td) || sameDay(td))) {
      throw new IllegalArgumentException("We cannot check for days in the future.");
    }
  }

  private List<Integer> parseDate(String date) {
    String[] parts = date.split("-");

    int year = Integer.parseInt(parts[0]);
    int month = Integer.parseInt(parts[1]);
    int day = Integer.parseInt(parts[2]);

    if (parts.length != 3) {
      throw new IllegalArgumentException("Invalid date.");
    }

    if (!isValidDate(year, month, day)) {
      throw new IllegalArgumentException("This is not a valid date.");
    }

    List<Integer> res = new ArrayList<>();
    res.add(year);
    res.add(month);
    res.add(day);

    return res;
  }

  public boolean isBefore(String date) {
    List<Integer> curr = parseDate(date);
    int currYear = curr.get(0);
    int currMonth = curr.get(1);
    int currDay = curr.get(2);

    if (year < currYear) {
      return true;
    } else if (year == currYear) {
      if (month < currMonth) {
        return true;
      } else if (month == currMonth) {
        return day < currDay;
      }
      return false;
    }
    return false;
  }

  public boolean sameDay(String date) {
    return this.date.equals(date);
  }

  private boolean isValidDate(int year, int month, int day) {
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
