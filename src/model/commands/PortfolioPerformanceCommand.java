package model.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.portfolio.Portfolio;
import model.user.UserData;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

public class PortfolioPerformanceCommand implements Command<String> {
  private LocalDate startDate;
  private LocalDate endDate;
  private StringBuilder graph;
  private String timescale;
  private List<LocalDate> times;
  private List<Double> values;
  private double scale;
  private UserData user;
  private double max;

  public PortfolioPerformanceCommand(String start, String end) {
    this.graph = new StringBuilder();
    this.timescale = "DAYS";
    this.times = new ArrayList<>();
    this.values = new ArrayList<>();
    try {
      this.startDate = LocalDate.parse(start);
      this.endDate = LocalDate.parse(end);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("Start date must be before end date.");
    }
  }

  /**
   * Executes the command onto a {@link UserData} object.
   *
   * @param user {@link UserData} object
   * @return a value given the command
   */
  @Override
  public String execute(UserData user) {
    this.user = user;
    Portfolio portfolio = user.getCurrentPortfolio();
    if (portfolio == null) {
      throw new IllegalArgumentException("No current portfolio set.");
    }

    String name = portfolio.getName();
    graph.append(String.format("Performance of portfolio %s from %s to %s\n\n", portfolio.getName(),
                    startDate.toString(), endDate.toString()));

    scale = getScale(user);
    draw();

    graph.append(String.format("\nScale: * = %.2f", scale));
    return graph.toString();
  }

  /**
   * Gets the name of the command being executed.
   *
   * @return the String for the name of the command.
   */
  @Override
  public String getName() {
    return "";
  }

  private double getScale(UserData user) {
    int days;
    int weeks = (int) WEEKS.between(startDate, endDate.plusWeeks(1));
    int months = (int) MONTHS.between(startDate, endDate.plusMonths(1));
    int years = (int) YEARS.between(startDate, endDate.plusYears(1));
    int decades = (int) DECADES.between(startDate, endDate.plusYears(10));

    if (startDate.isEqual(endDate)) {
      days = 1;
    } else {
      days = (int) DAYS.between(startDate, endDate.plusDays(1));
      if (days <= 30) {
        timescale = "DAYS";
      } else if (weeks <= 30) {
        timescale = "WEEKS";
      } else if (months <= 30) {
        timescale = "MONTHS";
      } else if (years <= 30) {
        timescale = "YEARS";
      } else if (decades <= 30) {
        timescale = "DECADES";
      }
      else {
        throw new IllegalArgumentException("Cannot calculate the performance for over 30 decades");
      }
    }

    double max = 0.0;
    switch (timescale) {
      case "DAYS":
        max = getMaxValue(days);
        break;
      case "WEEKS":
        max = getMaxValue(weeks);
        break;
      case "MONTHS":
        max = getMaxValue(months);
        break;
      case "YEARS":
        max = getMaxValue(years);
        break;
      case "DECADES":
        max = getMaxValue(decades);
        break;
      default:
        break;
    }

    return max / 50;
  }

  private double getMaxValue(int num) {
    Command<Double> getValue;
    max = 0.0;
    double value = 0.0;
    LocalDate date = startDate;
    if (timescale.equalsIgnoreCase("MONTHS")) {
      value = getValue(date);
      addToArrays(date, value);
      date = date.with(lastDayOfMonth());
    } else if (timescale.equalsIgnoreCase("YEARS")) {
      value = getValue(date);
      addToArrays(date, value);
      date = date.with(lastDayOfYear());
    }

    for (int i = 0; i < num && date.isBefore(endDate); i++) {
      value = getValue(date);
      switch (timescale) {
        case "DAYS":
          addToArrays(date, value);
          date = date.plusDays(1);
          break;
        case "WEEKS":
          addToArrays(date, value);
          date = date.plusWeeks(1);
          break;
        case "MONTHS":
          addToArrays(date, value);
          date = date.plusMonths(1);
          break;
        case "YEARS":
          addToArrays(date, value);
          date = date.plusYears(1);
          break;
        case "DECADES":
          addToArrays(date, value);
          date = date.plusYears(10);
        default:
          break;
      }
    }
    if (date.isAfter(endDate)) {
      getValue = new PortfolioGetValueCommand(endDate.toString());
      value = user.execute(getValue);
      if (value > max) {
        max = value;
      }
      addToArrays(endDate, value);
    }
    return max;
  }

  private void addToArrays(LocalDate date, double value) {
    times.add(date);
    values.add(value);
  }

  private double getValue(LocalDate date) {
    Command<Double> getValue = new PortfolioGetValueCommand(date.toString());
    double value = user.execute(getValue);
    if (value > max) {
      max = value;
    }
    return value;
  }

  private void draw() {
    for (int i = 0; i < times.size(); i++) {
      LocalDate date = times.get(i);
      String month = date.getMonth().name().substring(0, 3);
      int day = date.getDayOfMonth();
      int year = date.getYear();
      StringBuilder bar = new StringBuilder();
      int barLength = (int) (values.get(i) / scale);
      for (int j = 0; j < barLength; j++) {
        bar.append("*");
      }

      switch (timescale) {
        case "DAYS":
        case "WEEKS":
          graph.append(String.format("%s %02d %d: %s\n", month, day, year, bar));
          break;
        case "MONTHS":
        case "YEARS":
          graph.append(String.format("%s %d: %s\n", month, year, bar));
          break;
        default:
          break;
      }
    }
  }
}
