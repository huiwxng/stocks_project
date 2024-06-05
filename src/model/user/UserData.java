package model.user;

import java.util.List;

import model.portfolio.Portfolio;

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
}
