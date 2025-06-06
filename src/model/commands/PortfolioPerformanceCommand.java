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

/**
 * Class that represent a command that creates a graph of the performance of a portfolio
 * over a certain period over of time.
 */
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

  /**
   * Constructs a command that gets the graph that serves as a performance visualizer.
   *
   * @param start the start date of the performance * @param end the end date of the performance
   */
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

    scale = getScale();
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

  private double getScale() {
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
      } else if (weeks <= 90) {
        timescale = "3-WEEKS";
      } else if (months <= 30) {
        timescale = "MONTHS";
      } else if (months <= 90) {
        timescale = "3-MONTHS";
      } else if (years <= 30) {
        timescale = "YEARS";
      } else if (years <= 90) {
        timescale = "3-YEARS";
      } else if (decades <= 30) {
        timescale = "DECADES";
      } else {
        throw new IllegalArgumentException("Cannot calculate the performance for over 30 decades");
      }
    }

    max = getMaxValue();

    return max / 50;
  }

  private double getMaxValue() {
    Command<Double> getValue;
    max = 0.0;
    double value = 0.0;
    LocalDate date = startDate;
    if (timescale.equalsIgnoreCase("MONTHS")) {
      value = getValue(date);
      addToArrays(date, value);
      if (date.isEqual(date.with(lastDayOfMonth()))) {
        date = date.plusMonths(1);
      } else {
        date = date.with(lastDayOfMonth());
      }
    } else if (timescale.equalsIgnoreCase("3-MONTHS")) {
      value = getValue(date);
      addToArrays(date, value);
      if (date.isEqual(date.with(lastDayOfMonth()))) {
        date = date.plusMonths(3);
      } else {
        date = date.with(lastDayOfMonth());
      }
    } else if (timescale.equalsIgnoreCase("YEARS")) {
      value = getValue(date);
      addToArrays(date, value);
      if (date.isEqual(date.with(lastDayOfYear()))) {
        date = date.plusYears(1);
      } else {
        date = date.with(lastDayOfYear());
      }
    } else if (timescale.equalsIgnoreCase("3-YEARS")) {
      value = getValue(date);
      addToArrays(date, value);
      if (date.isEqual(date.with(lastDayOfYear()))) {
        date = date.plusYears(3);
      } else {
        date = date.with(lastDayOfYear());
      }
    }

    while (date.isBefore(endDate)) {
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
        case "3-WEEKS":
          addToArrays(date, value);
          date = date.plusWeeks(3);
          break;
        case "MONTHS":
          addToArrays(date, value);
          date = date.plusMonths(1);
          break;
        case "3-MONTHS":
          addToArrays(date, value);
          date = date.plusMonths(3);
          break;
        case "YEARS":
          addToArrays(date, value);
          date = date.plusYears(1);
          break;
        case "3-YEARS":
          addToArrays(date, value);
          date = date.plusYears(3);
          break;
        case "DECADES":
          addToArrays(date, value);
          date = date.plusYears(10);
          break;
        default:
          break;
      }
    }
    if (date.isAfter(endDate)) {
      value = getValue(endDate);
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
        case "3-WEEKS":
        case "MONTHS":
        case "3-MONTHS":
          graph.append(String.format("%s %02d %d: %s\n", month, day, year, bar));
          break;
        case "YEARS":
        case "3-YEARS":
          graph.append(String.format("%s %d: %s\n", month, year, bar));
          break;
        default:
          break;
      }
    }
  }
}
