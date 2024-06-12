package model.portfolio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.stock.BasicStock;
import model.stock.Stock;
import model.user.Transaction;

/**
 * Basic implementation of the {@link Portfolio} interface.
 */
public class BasicPortfolio implements Portfolio {

  private final String name;
  private List<Stock> stocks;
  private List<Double> shares;
  private final List<Transaction> transactions;

  /**
   * Constructs a new portfolio object.
   * @param name of the portfolio
   */
  public BasicPortfolio(String name) {
    this.name = name;
    this.stocks = new ArrayList<>();
    this.shares = new ArrayList<>();
    this.transactions = new ArrayList<>();
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
  public List<Stock> getStocks(String date) {
    processTransactions(date);
    return new ArrayList<>(stocks);
  }

  /**
   * Gets the list of share amounts of the stocks within the portfolio.
   *
   * @return a list of share amounts inside the portfolio
   */
  @Override
  public List<Double> getShares(String date) {
    processTransactions(date);
    return new ArrayList<>(shares);
  }

  /**
   * Gets the list of {@link Stock} objects within the portfolio along with the amount of shares.
   *
   * @return a list of Strings representing the stocks and the shares
   */
  @Override
  public List<String> getComposition(String date) {
    processTransactions(date);
    List<String> res = new ArrayList<>();
    for (int i = 0; i < stocks.size(); i++) {
      res.add(String.format("%s: %f shares", stocks.get(i).getTicker(), shares.get(i)));
    }
    return res;
  }

  /**
   * Adds a stock to the portfolio.
   *
   * @param ticker of the stock
   */
  @Override
  public void buyStock(String ticker, int amount, String date) {
    processTransactions(date);
    addToTransaction(true, ticker, amount, date);
  }

  /**
   * Removes a stock from the portfolio.
   *
   * @param ticker of the stock
   */
  @Override
  public void sellStock(String ticker, int amount, String date) throws IllegalArgumentException {
    processTransactions(date);
    addToTransaction(false, ticker, amount, date);
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

  private void processTransactions(String date) {
    stocks = new ArrayList<>();
    shares = new ArrayList<>();
    transactions.sort(DATE_COMPARATOR);
    for (Transaction transaction : transactions) {
      if (transaction.getDate().isBefore(date) || transaction.getDate().sameDay(date)) {
        if (transaction.getType()) {
          buyStockHelper(transaction.getTicker(), transaction.getShares());
        } else {
          sellStockHelper(transaction.getTicker(), transaction.getShares());
        }
      }
    }
  }

  private void buyStockHelper(String ticker, double amount) {
    // gets the index of the stock
    int i = getIndex(ticker);

    // if the stock does not exist already in the portfolio, add the stock and the
    // share amount, otherwise, add to the existing share amount
    if (i == -1) {
      Stock stock;
      try {
        stock = new BasicStock(ticker);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("A stock with this ticker does not exist.");
      }
      stocks.add(stock);
      shares.add(amount);
    } else {
      shares.set(i, shares.get(i) + amount);
    }
  }

  private void sellStockHelper(String ticker, double amount) {
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
    if (amount < shares.get(i)) {
      shares.set(i, shares.get(i) - amount);
    } else {
      stocks.remove(i);
      shares.remove(i);
    }
  }

  private void addToTransaction(boolean type, String ticker, int amount, String date) {
    Transaction transaction;
    try {
      transaction = new Transaction(type, ticker, amount, date);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid transaction. " + e.getMessage());
    }
    transactions.add(transaction);
  }

  private final Comparator<Transaction> DATE_COMPARATOR = (t1, t2) -> {
    if (t1.getDate().isBefore(t2.getDate().toString())) {
      return -1;
    } else if (t1.getDate().sameDay(t2.getDate().toString())) {
      return 0;
    } else {
      return 1;
    }
  };
}
