package model.commands;

import java.time.LocalDate;
import java.util.List;

import model.portfolio.Portfolio;
import model.stock.Stock;
import model.user.UserData;

/**
 * Command to get the value of the portfolio, meaning the sum of
 * the value of every stock.
 */
public class PortfolioGetValueCommand implements Command<Double> {

  private String date;

  /**
   * Constructs a command to get the value of the portfolio on a certain date.
   * @param date specified date
   */
  public PortfolioGetValueCommand(String date) {
    this.date = date;
    LocalDate today = LocalDate.now();
    if (today.isBefore(LocalDate.parse(date))) {
      throw new IllegalArgumentException("The program cannot predict future stock prices.");
    }
  }

  /**
   * Executes the command onto the {@link UserData} object.
   *
   * @param user {@link UserData} object
   */
  @Override
  public Double execute(UserData user) {
    Portfolio portfolio = user.getCurrentPortfolio();

    if (portfolio == null) {
      throw new IllegalArgumentException("No current portfolio set.");
    }

    double value = 0.0;

    List<Stock> stocks = portfolio.getStocks(date);
    List<Double> shares = portfolio.getShares(date);
    for (int i = 0; i < stocks.size(); i++) {
      Stock stock = stocks.get(i);
      int index = stock.getIndex(date);
      if (index == -1) {
        throw new IllegalArgumentException("No data found on this date.");
      }
      double price = stock.getClosingPrice(date);
      double share = shares.get(i);
      value += price * share;
    }

    String str = String.format("%.2f", value);
    return Double.valueOf(str);
  }

  @Override
  public String getName() {
    return "portfolio value";
  }
}
