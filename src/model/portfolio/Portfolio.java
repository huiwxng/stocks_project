package model.portfolio;

import java.util.List;

import model.stock.Stock;

/**
 * Interface that represents a portfolio of stocks.
 */
public interface Portfolio {

  /**
   * Takes in the object of PortfolioCommand and executes it onto a portfolio.
   *
   * @param cmd PortfolioCommand to be executed.
   */
  void execute(PortfolioCommand cmd);

  /**
   * Gets the name of the portfolio.
   *
   * @return String for the name of the portfolio
   */
  String getName();

  /**
   * Gets the list of {@link Stock} objects within the portfolio.
   * @return a list of Stocks inside the portfolio
   */
  List<Stock> getStocks();

  /**
   * Gets the list of {@link Stock} objects within the portfolio along with the amount of shares.
   * @return a list of Strings representing the stocks and the shares
   */
  List<String> getStocksWithAmt();

  /**
   * Adds a stock to the portfolio.
   * @param ticker of the stock
   * @param shareAmt amount of shares to add
   */
  void addStock(String ticker, int shareAmt);

  /**
   * Removes a stock from the portfolio.
   * @param ticker of the stock
   * @param shareAmt amount of shares to remove
   */
  void removeStock(String ticker, int shareAmt);

  /**
   * Checks if the portfolio has stocks inside.
   * @return true if there are stocks within, false otherwise
   */
  boolean isEmpty();
}
