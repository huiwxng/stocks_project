package model.commands;

import model.Date;
import model.portfolio.Portfolio;
import model.user.UserData;

public class PortfolioPerformanceCommand implements Command<String> {
  private String start;
  private String end;
  private Date startDate;
  private Date endDate;

  PortfolioPerformanceCommand(String start, String end) {
    this.start = start;
    this.end = end;
    try {
      this.startDate = new Date(start);
      this.endDate = new Date(end);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    if (endDate.isBefore(start)) {
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
    String graph = "";
    Portfolio portfolio = user.getCurrentPortfolio();
    if (portfolio == null) {
      throw new IllegalArgumentException("No current portfolio set.");
    }

    String name = portfolio.getName();
    graph += String.format("Performance of portfolio %s from %s to %s\n", portfolio.getName(),
            startDate.toString(), endDate.toString());

//    double scale;
//
//    graph += String.format("Scale: * = %02f", scale);
    return graph;
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
}
