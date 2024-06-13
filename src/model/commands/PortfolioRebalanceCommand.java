package model.commands;

import java.util.List;

import model.portfolio.Portfolio;
import model.stock.Stock;
import model.user.UserData;

/**
 * Command to properly rebalance the stocks in a portfolio given
 * desired weights.
 */
public class PortfolioRebalanceCommand implements Command<String> {
  private final String date;
  private final int[] weights;

  /**
   *
   * @param date specified date
   * @param weights weights for the dates
   */
  public PortfolioRebalanceCommand(String date, int... weights) {
    this.date = date;
    this.weights = weights;
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
    
    Command<Double> getValue = new PortfolioGetValueCommand(date);
    double totalValue = user.execute(getValue);
    List<Stock> stocks = portfolio.getStocks(date);
    if (stocks.size() != weights.length) {
      throw new IllegalArgumentException("There are an uneven number of stocks and weights.");
    }
    List<Double> shares = portfolio.getShares(date);

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
        portfolio.sellStock(ticker, diff, date);
      } else if (diff > 0) {
        portfolio.buyStock(ticker, diff, date);
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
