package model.user;

import java.util.List;

import model.commands.Command;
import model.portfolio.Portfolio;
import model.stock.Stock;

/**
 * Interface that represents all of a user's portfolios.
 */
public interface UserData {

  /**
   * Adds a portfolio to the user's data.
   *
   * @param portfolio {@link Portfolio} object to add to the data
   */
  void addPortfolio(Portfolio portfolio);

  /**
   * Removes a portfolio from the user's data.
   *
   * @param portfolio {@link Portfolio} object to remove from the data
   */
  void removePortfolio(Portfolio portfolio);

  /**
   * Gets a list of portfolios from the user's data.
   * @return a list of {@link Portfolio} objects that the user holds.
   */
  List<Portfolio> listPortfolios();

  /**
   * Gets the number of portfolios the user holds.
   * @return the number of portfolios
   */
  int getNumPortfolios();

  /**
   * Sets the current portfolio.
   *
   * @param portfolio to set as current portfolio
   */
  void setCurrentPortfolio(Portfolio portfolio);

  /**
   * Gets the current portfolio.
   * @return the current portfolio
   */
  Portfolio getCurrentPortfolio();

  /**
   * Gets a portfolio from the list given the index.
   * @param index of the portfolio
   * @return the portfolio at the index
   */
  Portfolio getPortfolio(int index);

  /**
   * Sets the stock that the user is currently viewing.
   * @param ticker of the stock
   */
  void setCurrentStock(String ticker);

  /**
   * Gets a stock that the user is currently viewing.
   *
   * @return a stock object
   */
  Stock getCurrentStock();

  /**
   * Executes a given command.
   * @param cmd command
   * @param <T> return type of the command
   * @return the value from the command
   */
  <T> T execute(Command<T> cmd);
}
