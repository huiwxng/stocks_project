import org.junit.Before;
import org.junit.Test;

import model.Date;

import static org.junit.Assert.*;

/**
 * Test class for {@link Date} class.
 */
public class DateTest {
  Date birthday;
  Date leapYear;
  Date newYears;
  Date notLeapYear;
  Date tooManyDays;
  Date tooFewDays;
  Date negative;
  Date autograder;
  Date autograder2;

  @Before
  public void setup() {
    birthday = new Date("2005-10-10");
    leapYear = new Date("2000-02-29");
    newYears = new Date("2000-01-01");
    autograder = new Date("2000-02-28");
    autograder2 = new Date("2012-01-28");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionNotLeapYear() {
    notLeapYear = new Date("2001-02-29");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionTooManyDays() {
    tooManyDays = new Date("2005-10-32");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionTooFewDays() {
    tooFewDays = new Date("2005-10-00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorExceptionNegativeInput() {
    negative = new Date("-2005-10-10");
  }

  @Test
  public void testConstructor() {
    assertEquals("2005-10-10", birthday.toString());
    assertEquals("2000-02-29", leapYear.toString());
  }

  @Test
  public void testAdvanceZero() {
    birthday.advance(0);
    assertEquals("2005-10-10", birthday.toString());
  }

  @Test
  public void testAdvancePositive() {
    birthday.advance(10);
    assertEquals("2005-10-20", birthday.toString());
    birthday.advance(20);
    assertEquals("2005-11-09", birthday.toString());
    newYears.advance(-1);
    newYears.advance(10);
    assertEquals("2000-01-10", newYears.toString());
    newYears.advance(365);
    assertEquals("2001-01-09", newYears.toString());
    autograder2.advance(32);
    assertEquals("2012-02-29", autograder2.toString());
  }

  @Test
  public void testAdvanceNegative() {
    birthday.advance(-9);
    assertEquals("2005-10-01", birthday.toString());
    birthday.advance(-9);
    assertEquals("2005-09-22", birthday.toString());
    newYears.advance(-1);
    assertEquals("1999-12-31", newYears.toString());
    newYears.advance(-365);
    assertEquals("1998-12-31", newYears.toString());
  }

  @Test
  public void testAdvanceLeapYearPositive() {
    leapYear.advance(-9);
    leapYear.advance(20);
    assertEquals("2000-03-11", leapYear.toString());
    autograder.advance(1);
    assertEquals("2000-02-29", autograder.toString());
  }

  @Test
  public void testAdvanceLeapYearNegative() {
    leapYear.advance(10);
    leapYear.advance(-20);
    assertEquals("2000-02-19", leapYear.toString());
  }

  @Test
  public void testToString() {
    assertEquals("2005-10-10", birthday.toString());
  }

  @Test
  public void isBefore() {
    assertTrue(birthday.isBefore("2024-06-04"));
    assertFalse(birthday.isBefore("2005-10-10"));
    assertFalse(birthday.isBefore("2005-10-09"));
  }

  @Test
  public void sameDay() {
    assertTrue(birthday.sameDay("2005-10-10"));
    assertTrue(leapYear.sameDay("2000-02-29"));
    assertTrue(newYears.sameDay("2000-01-01"));
    assertTrue(autograder.sameDay("2000-02-28"));
    assertTrue(autograder2.sameDay("2012-01-28"));
  }
}