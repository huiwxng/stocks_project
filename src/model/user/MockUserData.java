package model.user;

import java.util.List;
import java.util.Objects;

import model.portfolio.Portfolio;
import model.stock.Stock;

/**
 * This class represents a mock of the UserData class so that the controller can be
 * tested easily.
 */
public class MockUserData implements UserData {
  final StringBuilder log;

  /**
   * Constructor for our MockUserData that initializes a log.
   * @param log log of operations
   */
  public MockUserData(StringBuilder log) {
    this.log = Objects.requireNonNull(log);
  }

  @Override
  public void addPortfolio(Portfolio portfolio) {
    log.append("add portfolio:" ).append(portfolio.getName()).append("\n");
  }

  @Override
  public void removePortfolio(Portfolio portfolio) {
    log.append("removePortfolio: ").append(portfolio.getName()).append("\n");
  }

  @Override
  public List<Portfolio> listPortfolios() {
    log.append("listPortfolios\n");
    return List.of();
  }

  /**
   * Gets a stock that the user is currently viewing.
   *
   * @param ticker of the stock
   * @return a stock object
   */
  @Override
  public Stock viewStock(String ticker) {
    return null;
  }
}
