package model.portfolio;

import java.util.List;

import model.stock.Stock;

/**
 * Command to get the value of all the stocks in the portfolio.
 */
public class GetValueCommand implements PortfolioCommand<Double> {

  private String date;

  /**
   * Constructs a command to get the value of the portfolio on a certain date.
   * @param date specified date
   */
  public GetValueCommand(String date) {
    this.date = date;
  }

  /**
   * Executes the command onto the {@link Portfolio} object.
   *
   * @param portfolio {@link Portfolio} object
   */
  @Override
  public Double execute(Portfolio portfolio) {
    if (portfolio.isEmpty()) {
      throw new IllegalArgumentException("No stocks in portfolio.");
    }

    double value = 0.0;

    List<Stock> stocks = portfolio.getStocks();
    List<Integer> shares = portfolio.getShares();
    for (int i = 0; i < stocks.size(); i++) {
      Stock stock = stocks.get(i);
      int index = stock.getIndex(date);
      if (index == -1) {
        throw new IllegalArgumentException("No data found on this date.");
      }
      double price = stock.getClosingPrice(date);
      int share = shares.get(i);
      value += price * share;
    }

    String str = String.format("%.2f", value);
    return Double.valueOf(str);
  }
}
