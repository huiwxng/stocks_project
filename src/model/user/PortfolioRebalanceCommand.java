package model.user;

import java.util.List;

import model.portfolio.Portfolio;
import model.stock.Stock;

public class PortfolioRebalanceCommand implements Command<String> {
  private final String date;
  private final int[] weights;

  public PortfolioRebalanceCommand(String date, int... weights) {
    this.date = date;
    this.weights = weights;
  }

  /**
   * Executes the command onto a {@link UserData} object.
   *
   * @param user {@link UserData} object
   * @return a value given the command
   */
  @Override
  public String execute(UserData user) {
    // checks
    int sum = 0;
    for (int weight : weights) {
      if (weight < 0) {
        throw new IllegalArgumentException("Weights cannot be negative.");
      }
      sum += weight;
    }
    if (sum != 100) {
      throw new IllegalArgumentException("Weights must add up to 100.");
    }

    Portfolio pf = user.getCurrentPortfolio();
    Command<Double> getValue = new PortfolioGetValueCommand(date);
    double totalValue = user.execute(getValue);
    List<Stock> stocks = pf.getStocks(date);
    if (stocks.size() != weights.length) {
      throw new IllegalArgumentException("There are an uneven number of stocks and weights.");
    }
    List<Double> shares = pf.getShares(date);

    for (int i = 0; i < stocks.size(); i++) {
      Stock currentStock = stocks.get(i);
      double currentShares = shares.get(i);

      String ticker = currentStock.getTicker();
      double weight = (double) weights[i] / 100;
      double price = currentStock.getClosingPrice(date);
      double targetValue = weight * totalValue;
      double targetShares = targetValue / price;
      double diff = targetShares - currentShares;
      if (diff < 0 ) {
        diff *= -1;
        pf.sellStock(ticker, diff, date);
      } else if (diff > 0) {
        pf.buyStock(ticker, diff, date);
      }
    }
    return "Portfolio re-balanced successfully.";
  }

  /**
   * Gets the name of the command being executed.
   *
   * @return the String for the name of the command.
   */
  @Override
  public String getName() {
    return "re-balance portfolio";
  }
}
