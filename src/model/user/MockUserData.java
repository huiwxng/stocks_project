package model.user;

import java.util.List;

import model.portfolio.Portfolio;
import model.stock.Stock;

public class MockUserData extends BasicUserData {
  private final StringBuilder log;

  public MockUserData(StringBuilder log) {
    super();
    this.log = log;
  }

  /**
   * Adds a portfolio to the user's data.
   *
   * @param portfolio {@link Portfolio} object to add to the data
   */
  @Override
  public void addPortfolio(Portfolio portfolio) {
    super.addPortfolio(portfolio);
    log.append("add portfolio: ").append(portfolio.getName()).append("\n");
  }

  /**
   * Removes a portfolio from the user's data.
   *
   * @param portfolio {@link Portfolio} object to remove from the data
   */
  @Override
  public void removePortfolio(Portfolio portfolio) {
    super.removePortfolio(portfolio);
    log.append("remove portfolio: ").append(portfolio.getName()).append("\n");
  }

  /**
   * Gets a list of portfolios from the user's data.
   *
   * @return a list of {@link Portfolio} objects that the user holds.
   */
  @Override
  public List<Portfolio> listPortfolios() {
    log.append("view portfolios: ").append("\n");
    return super.listPortfolios();
  }

  /**
   * Sets the current portfolio.
   */
  @Override
  public void setCurrentPortfolio(Portfolio portfolio) {
    super.setCurrentPortfolio(portfolio);
    log.append("view specific portfolio: ").append(portfolio.getName()).append("\n");
  }

  /**
   * Gets the current portfolio.
   *
   * @return the current portfolio
   */
  @Override
  public Portfolio getCurrentPortfolio() {
    return super.getCurrentPortfolio();
  }

  /**
   * Gets a portfolio from the list given the index.
   *
   * @param index of the portfolio
   * @return the portfolio at the index
   */
  @Override
  public Portfolio getPortfolio(int index) {
    return super.getPortfolio(index);
  }

  /**
   * Sets the stock that the user is currently viewing.
   *
   * @param ticker of the stock
   */
  @Override
  public void setCurrentStock(String ticker) {
    super.setCurrentStock(ticker);
    log.append("view stock: ").append(ticker).append("\n");
  }

  /**
   * Gets a stock that the user is currently viewing.
   *
   * @return a stock object
   */
  @Override
  public Stock getCurrentStock() {
    return super.getCurrentStock();
  }

  /**
   * Executes a given command.
   *
   * @param cmd command
   */
  @Override
  public <T> T execute(Command<T> cmd) {
    log.append(cmd.getName()).append(": \n");
    return super.execute(cmd);
  }

  /**
   * Gets the log as a string.
   *
   * @return a string of the log
   */
  public String getLog() {
    return log.toString();
  }
}
