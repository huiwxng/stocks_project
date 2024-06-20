package model.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;

import model.stock.BasicStock;
import model.stock.Stock;
import model.user.Transaction;

/**
 * Basic implementation of the {@link Portfolio} interface. In this implementation,
 * users can have a portfolio that they name. They can also see the value of the
 * portfolio on certain dates, buy stocks, sell stocks, rebalance stocks, and
 * see a performance graph.
 */
public class BasicPortfolio implements Portfolio {

  private final String name;
  private List<Stock> stocks;
  private List<Double> shares;
  private final List<Transaction> transactions;

  /**
   * Constructs a new portfolio object.
   *
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
      String decimalString = formatDouble(shares.get(i));
      res.add(String.format("%s: %s share(s)", stocks.get(i).getTicker(),
              decimalString));
    }
    return res;
  }

  /**
   * Gets the distribution of {@link Stock} objects, meaning all stocks along with their values
   * in the portfolio.
   *
   * @param date date of the distribution
   * @return a list of Strings representing the stocks and their values
   */
  @Override
  public List<String> getDistribution(String date) {
    List<String> res = new ArrayList<>();
    for (int i = 0; i < stocks.size(); i++) {
      // error checking in getClosingPrice
      double price = shares.get(i) * stocks.get(i).getClosingPrice(date);
      String ticker = stocks.get(i).getTicker();
      String str = String.format("%s: $%.2f", ticker, price);
      res.add(str);
    }
    return res;
  }

  /**
   * Adds a stock to the portfolio.
   *
   * @param ticker of the stock
   */
  @Override
  public void buyStock(String ticker, double amount, String date) {
    checkFuture(date);
    addToTransaction(true, ticker, amount, date);
    try {
      processTransactions(date);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Removes a stock from the portfolio.
   *
   * @param ticker of the stock
   */
  @Override
  public void sellStock(String ticker, double amount, String date) throws IllegalArgumentException {
    checkFuture(date);
    if (isEmpty(date)) {
      throw new IllegalArgumentException("There are no stocks in the portfolio.");
    }
    addToTransaction(false, ticker, amount, date);
    try {
      processTransactions(date);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  private void checkFuture(String date) {
    if (LocalDate.now().isBefore(LocalDate.parse(date))) {
      throw new IllegalArgumentException("Cannot trade a stock in the future.");
    }
  }

  /**
   * Checks if the portfolio has stocks inside.
   *
   * @return true if there are stocks within, false otherwise
   */
  @Override
  public boolean isEmpty(String date) {
    return getStocks(date).isEmpty();
  }

  /**
   * Saves the current portfolio to the designated directory.
   *
   * @return a string for success of portfolio creation
   */
  @Override
  public String save() {
    String dirPath = "res/portfolios/";
    String ext = ".csv";
    String path = dirPath + getName() + ext;
    String msg;

    if (Files.exists(Path.of(path))) {
      msg = "Portfolio [" + getName() + "] overwritten successfully and saved to " + path;
    } else {
      msg = "Portfolio [" + getName() + "] successfully saved to " + path;
    }

    try (FileWriter writer = new FileWriter(path)) {
      // write the header
      writer.write("Date,Type,Ticker,Amount\n");

      // write the contents
      for (Transaction transaction : transactions) {
        String date = transaction.getDate().toString();
        String type;
        if (transaction.getType()) {
          type = "BUY";
        } else {
          type = "SELL";
        }
        String ticker = transaction.getTicker();
        String amount = formatDouble(transaction.getShares());
        writer.write(String.format("%s,%s,%s,%s\n", date, type, ticker, amount));
      }
    } catch (IOException e) {
      System.err.println("Error saving the portfolio: " + e.getMessage());
    }

    return msg;
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
    LocalDate curr;
    try {
      curr = LocalDate.parse(date);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid date.");
    }
    Collections.sort(transactions);
    for (int i = 0; i < transactions.size(); i++) {
      transactions.get(i).setIndex(i);
    }
    for (Transaction transaction : transactions) {
      if (transaction.getDate().isBefore(curr) || transaction.getDate().isEqual(curr)) {
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

  private void addToTransaction(boolean type, String ticker, double amount, String date) {
    Transaction transaction;
    try {
      transaction = new Transaction(type, ticker, amount, date, transactions.size());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid transaction. " + e.getMessage());
    }
    transactions.add(transaction);
  }

  private String formatDouble(double num) {
    BigDecimal decimal = new BigDecimal(Double.toString(num));
    return decimal.stripTrailingZeros().toPlainString();
  }
}
