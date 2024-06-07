package model.user;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Port;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.stock.BasicStock;
import model.stock.Stock;

/**
 * Basic implementation of the {@link UserData} interface.
 */
public class BasicUserData implements UserData {

  List<Portfolio> portfolios;
  Portfolio currentPortfolio;

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
   * Gets the current portfolio.
   *
   * @return the current portfolio
   */
  @Override
  public Portfolio getCurrentPortfolio() {
    return currentPortfolio;
  }

  /**
   * Sets the current portfolio.
   */
  @Override
  public void setCurrentPortfolio(String name) {
    currentPortfolio = new BasicPortfolio(name);
  }

  /**
   * Gets a stock that the user is currently viewing.
   *
   * @param ticker of the stock
   * @return a stock object
   */
  @Override
  public Stock viewStock(String ticker) {
    return new BasicStock(ticker);
  }
}
