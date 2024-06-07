package model.user;

import java.util.ArrayList;
import java.util.List;

import model.portfolio.Portfolio;
import model.stock.BasicStock;
import model.stock.Stock;

/**
 * Basic implementation of the {@link UserData} interface.
 */
public class BasicUserData implements UserData {

  List<Portfolio> portfolios;
  Portfolio currentPortfolio;
  Stock currentStock;

  /**
   * Constructs a user with an empty portfolio list.
   */
  public BasicUserData() {
    portfolios = new ArrayList<>();
  }

  /**
   * Adds a portfolio to the user's data.
   *
   * @param portfolio {@link Portfolio} object to add to the data
   */
  @Override
  public void addPortfolio(Portfolio portfolio) {
    portfolios.add(portfolio);
  }

  /**
   * Removes a portfolio from the user's data.
   *
   * @param portfolio {@link Portfolio} object to remove from the data
   */
  @Override
  public void removePortfolio(Portfolio portfolio) {
    portfolios.remove(portfolio);
  }

  /**
   * Gets a list of portfolios from the user's data.
   *
   * @return a list of {@link Portfolio} objects that the user holds.
   */
  @Override
  public List<Portfolio> listPortfolios() {
    return new ArrayList<>(portfolios);
  }

  /**
   * Gets the number of portfolios the user holds.
   *
   * @return the number of portfolios
   */
  @Override
  public int getNumPortfolios() {
    return listPortfolios().size();
  }

  /**
   * Sets the current portfolio.
   */
  @Override
  public void setCurrentPortfolio(Portfolio portfolio) {
    if (!listPortfolios().contains(portfolio)) {
      throw new IllegalArgumentException("This portfolio is not available.");
    }
    currentPortfolio = portfolio;
  }

  /**
   * Gets the current portfolio.
   *
   * @return the current portfolio
   */
  @Override
  public Portfolio getCurrentPortfolio() {
    return currentPortfolio;
  }

  /**
   * Gets a portfolio from the list given the index.
   *
   * @param index of the portfolio
   * @return the portfolio at the index
   */
  @Override
  public Portfolio getPortfolio(int index) {
    return portfolios.get(index);
  }

  /**
   * Sets the stock that the user is currently viewing.
   *
   * @param ticker of the stock
   */
  @Override
  public void setCurrentStock(String ticker) {
    currentStock = new BasicStock(ticker);
  }

  /**
   * Gets a stock that the user is currently viewing.
   *
   * @return a stock object
   */
  @Override
  public Stock getCurrentStock() {
    if (currentStock == null) {
      throw new IllegalArgumentException("Not currently viewing a stock.");
    }
    return new BasicStock(currentStock.getTicker());
  }

  /**
   * Executes a given command.
   *
   * @param cmd command
   */
  @Override
  public <T> T execute(Command<T> cmd) {
    return cmd.execute(this);
  }
}