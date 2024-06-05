package model.portfolio;

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
}
