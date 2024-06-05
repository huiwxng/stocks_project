package model.user;

import java.util.ArrayList;
import java.util.List;

import model.portfolio.Portfolio;
import model.stock.Stock;

/**
 * Basic implementation of the {@link UserData} interface.
 */
public class BasicUserData implements UserData {

  List<Portfolio> portfolios;

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
}
