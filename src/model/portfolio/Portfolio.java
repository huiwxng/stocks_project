package model.portfolio;

import java.util.List;

import model.stock.Stock;

/**
 * Interface that represents a portfolio of stocks.
 */
public interface Portfolio {

  /**
   * Gets the name of the portfolio.
   *
   * @return String for the name of the portfolio
   */
  String getName();

  /**
   * Gets the list of {@link Stock} objects within the portfolio.
   *
   * @return a list of Stocks inside the portfolio
   */
  List<Stock> getStocks(String date);

  /**
   * Gets the list of share amounts of the stocks within the portfolio.
   *
   * @return a list of share amounts inside the portfolio
   */
  List<Double> getShares(String date);

  /**
   * Gets the list of {@link Stock} objects within the portfolio along with the amount of shares.
   *
   * @return a list of Strings representing the stocks and the shares
   */
  List<String> getComposition(String date);

  /**
   * Gets the distribution of {@link Stock} objects, meaning all stocks along with their values
   * in the portfolio.
   *
   * @param date date of the distribution
   * @return a list of Strings representing the stocks and their values
   */
  List<String> getDistribution(String date);

  /**
   * Adds a stock to the portfolio.
   *
   * @param ticker of the stock
   * @param amount of shares to add
   * @param date the date to add the stock
   */
  void buyStock(String ticker, double amount, String date);

  /**
   * Removes a stock from the portfolio.
   *
   * @param ticker of the stock
   * @param amount of shares to remove
   * @param date the date to remove the stock
   */
  void sellStock(String ticker, double amount, String date);

  /**
   * Checks if the portfolio has stocks inside.
   *
   * @param date of the portfolio to check
   * @return true if there are stocks within, false otherwise
   */
  boolean isEmpty(String date);

  /**
   * Saves the current portfolio to the designated directory.
   * @return a string for success of portfolio creation
   */
  String save();

  /*
   * DESIGN CHANGES FOR ASSIGNMENT 5:
   *
   * We changed the type for stocks amount to double to account for fractional shares. We changed
   * the implementation for adding and removing stocks (from directly adding to a list of stocks
   * and shares) to adding to a list of transactions and calculating all the transactions in that
   * list to get the list of stocks and shares. We also switched from using our own Date class to
   * using LocalDate. We continued using the Command Design Pattern for rebalance and visualizing
   * the performance, but we added a new method for getting the distribution of portfolio value.
   */
}
