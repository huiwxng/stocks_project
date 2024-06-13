package model.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.portfolio.Portfolio;
import model.user.UserData;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

public class PortfolioPerformanceCommand implements Command<String> {
  private String start;
  private String end;
  private LocalDate startDate;
  private LocalDate endDate;
  private StringBuilder graph;
  private String timescale;
  private List<LocalDate> times;
  private List<Double> values;
  private double scale;

  public PortfolioPerformanceCommand(String start, String end) {
    this.start = start;
    this.end = end;
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

    graph.append("weeks: ").append(weeks);
    graph.append("months: ").append(months);
    graph.append("years: ").append(years);

    if (startDate.isEqual(endDate)) {
      days = 1;
    } else {
      days = (int) DAYS.between(startDate, endDate.plusDays(1));
      if (days <= 30) {
        timescale = "DAYS";
      } else if (days <= 210) {
        timescale = "WEEKS";
      } else if (days <= 900) {
        timescale = "MONTHS";
      } else if (years <= 10950) {
        timescale = "YEARS";
      } else {
        throw new IllegalArgumentException("Cannot calculate the performance for over 30 years");
      }
    }

    double max = 0.0;
    switch (timescale) {
      case "DAYS":
        max = getMaxValue(days, user);
        break;
      case "WEEKS":
        max = getMaxValue(weeks, user);
        break;
      case "MONTHS":
        max = getMaxValue(months, user);
        break;
      case "YEARS":
        max = getMaxValue(years, user);
        break;
      default:
        break;
    }

    return max / 50;
  }

  private double getMaxValue(int num, UserData user) {
    Command<Double> getValue;
    double max = 0.0;
    double value = 0.0;
    if (timescale.equalsIgnoreCase("MONTH")) {
      startDate = startDate.with(lastDayOfMonth());
    } else if (timescale.equalsIgnoreCase("YEAR")) {
      startDate = startDate.with(lastDayOfYear());
    }

    for (int i = 0; i < num && startDate.isBefore(endDate); i++) {
      String date = startDate.toString();
      getValue = new PortfolioGetValueCommand(date);
      value = user.execute(getValue);
      if (value > max) {
        max = value;
      }
      switch (timescale) {
        case "DAYS":
          addToArrays(startDate, value);
          startDate = startDate.plusDays(1);
          break;
        case "WEEKS":
          addToArrays(startDate, value);
          startDate = startDate.plusWeeks(1);
          break;
        case "MONTHS":
          addToArrays(startDate, value);
          startDate = startDate.plusMonths(1);
          break;
        case "YEARS":
          addToArrays(startDate, value);
          startDate = startDate.plusYears(1);
          break;
        default:
          break;
      }
    }
    if (startDate.isAfter(endDate)) {
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
