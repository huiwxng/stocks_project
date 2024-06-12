package model.user;

import model.Date;
import model.stock.BasicStock;
import model.stock.Stock;

public class Transaction {
  private final boolean buy;
  private final String ticker;
  private final Stock stock;
  private final double shares;
  private final Date date;

  public Transaction(boolean buy, String ticker, double shares, String date) {
    try {
      this.ticker = ticker.toUpperCase();
      this.stock = new BasicStock(ticker);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("A stock with this ticker does not exist.");
    }
    try {
      this.date = new Date(date);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("This is not a valid date.");
    }
    if (shares < 0) {
      throw new IllegalArgumentException("Cannot buy/sell negative shares");
    }
    this.shares = shares;
    this.buy = buy;
  }

  public boolean getType() {
    return buy;
  }

  public String getTicker() {
    return ticker;
  }

  public double getShares() {
    return shares;
  }

  public Date getDate() {
    return date;
  }

  public Stock getStock() {
    return stock;
  }
}
