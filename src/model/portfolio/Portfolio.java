package model.portfolio;

/**
 * Interface that represents a portfolio of stocks.
 */
public interface Portfolio {

  /**
   *
   * @param cmd
   */
  void execute(PortfolioCommand cmd);
}
