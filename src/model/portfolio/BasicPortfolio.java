package model.portfolio;

import java.util.ArrayList;
import java.util.List;

import model.stock.BasicStock;
import model.stock.Stock;

/**
 * Basic implementation of the {@link Portfolio} interface.
 */
public class BasicPortfolio implements Portfolio {

  private String name;
  private List<Stock> stocks;
  private List<Integer> shares;

  public BasicPortfolio(String name) {
    this.name = name;
    stocks = new ArrayList<>();
    shares = new ArrayList<>();
  }

  /**
   * Takes in the object of PortfolioCommand and executes it onto a portfolio.
   *
   * @param cmd PortfolioCommand to be executed.
   */
  @Override
  public void execute(PortfolioCommand cmd) {
    cmd.execute(this);
  }

  /**
   * Gets the name of the portfolio.
   *
   * @return String for the name of the portfolio
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Gets the list of {@link Stock} objects within the portfolio.
   *
   * @return a list of Stocks inside the portfolio
   */
  @Override
  public List<Stock> getStocks() {
    return new ArrayList<>(stocks);
  }

  /**
   * Gets the list of {@link Stock} objects within the portfolio along with the amount of shares.
   *
   * @return a list of Strings representing the stocks and the shares
   */
  @Override
  public List<String> getStocksWithAmt() {
    List<String> res = new ArrayList<>();
    for (int i = 0; i < stocks.size(); i++) {
      res.add(String.format("%s: %d", stocks.get(i).getTicker(), shares.get(i)));
    }
    return res;
  }

  /**
   * Adds a stock to the portfolio.
   *
   * @param ticker of the stock
   */
  @Override
  public void addStock(String ticker, int shareAmt) {
    // gets the index of the stock
    int i = getIndex(ticker);

    // if the stock does not exist already in the portfolio, add the stock and the
    // share amount, otherwise, add to the existing share amount
    if (i == -1) {
      Stock stock = new BasicStock(ticker);
      stocks.add(stock);
      shares.add(shareAmt);
    } else {
      shares.set(i, shares.get(i) + shareAmt);
    }

  }

  /**
   * Removes a stock from the portfolio.
   *
   * @param ticker of the stock
   */
  @Override
  public void removeStock(String ticker, int shareAmt) throws IllegalArgumentException {
    // checks if the portfolio is empty
    if (isEmpty()) {
      throw new IllegalArgumentException("There are no stocks in the portfolio.");
    }

    // checks if the portfolio contains the specified stock
    int i = getIndex(ticker);
    if (i == -1) {
      throw new IllegalArgumentException("There is no such stock in the portfolio.");
    }

    // if the current stock shares amount is greater than the remove amount, remove
    // the amount from the current, otherwise, remove the stock completely
    if (shareAmt < shares.get(i)) {
      shares.set(i, shares.get(i) - shareAmt);
    } else {
      stocks.remove(i);
      shares.remove(i);
    }
  }

  /**
   * Checks if the portfolio has stocks inside.
   * @return true if there are stocks within, false otherwise
   */
  @Override
  public boolean isEmpty() {
    return stocks.isEmpty();
  }

  // loops through and gets the index of the stock with the same ticker,
  // if it doesn't exist, return -1
  private int getIndex(String ticker) {
    for (int i = 0; i < stocks.size(); i++) {
      if (stocks.get(i).getTicker().equalsIgnoreCase(ticker)) {
        return i;
      }
    }
    return -1;
  }

  public static void main(String args[]) {
    // tests to write:
    // run getters at every step
    // test for an empty portfolio:
        // running removeStock should throw error

    // test for adding a stock to portfolio:
        // test for adding to existing stock
        // test for adding to non-existing stock

    // test for removing a stock:
        // test for removing existing stock:
            // with enough shares
            // with not enough shares:
                // amount more than the existing shares
                // amount the same as the existing shares
        // test for removing non-existing stock should throw error

    Portfolio p = new BasicPortfolio("1");
    System.out.println(p.getName());
    System.out.println(p.getStocks().toString());
    System.out.println(p.getStocksWithAmt());
//    p.removeStock("AAPL", 10);
    p.addStock("AAPL", 10);
    System.out.println(p.getStocks().toString());
    System.out.println(p.getStocksWithAmt());
    p.addStock("AAPL", 2);
    System.out.println(p.getStocksWithAmt());
    p.removeStock("AAPL", 2);
    System.out.println(p.getStocksWithAmt());
    p.removeStock("AAPL", 9);
    System.out.println(p.getStocksWithAmt());
    p.removeStock("AAPL", 1);
    System.out.println(p.getStocksWithAmt());
    p.addStock("AAPL", 2);
    System.out.println(p.getStocksWithAmt());
    p.addStock("GOOG", 2);
    System.out.println(p.getStocksWithAmt());
    p.removeStock("AAPL", 3);
    System.out.println(p.getStocksWithAmt());
  }
}
