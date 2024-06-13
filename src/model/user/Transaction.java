package model.user;

import java.time.LocalDate;

import model.stock.BasicStock;
import model.stock.Stock;

public class Transaction implements Comparable<Transaction> {
  private final boolean buy;
  private final String ticker;
  private final Stock stock;
  private final double shares;
  private final LocalDate date;
  private int index;

  public Transaction(boolean buy, String ticker, double shares, String date, int index) {
    try {
      this.ticker = ticker.toUpperCase();
      this.stock = new BasicStock(ticker);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("A stock with this ticker does not exist.");
    }
    try {
      this.date = LocalDate.parse(date);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("This is not a valid date.");
    }
    if (shares < 0) {
      throw new IllegalArgumentException("Cannot buy/sell negative shares");
    }
    this.shares = shares;
    this.buy = buy;
    this.index = index;
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

  public LocalDate getDate() {
    return date;
  }

  public Stock getStock() {
    return stock;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  /**
   * Checks if the transactions occur before the other transaction.
   * @param other transaction
   * @return true if the transaction occurs before the other transaction
   */
  public boolean isBefore(Transaction other) {
    if (other == null) {
      throw new IllegalArgumentException("The compared object is null");
    }
    return getDate().isBefore(other.getDate());
  }

  /**
   * Checks if the transactions occur on the same date.
   * @param other transaction
   * @return true if the transactions occur on the same date, false otherwise
   */
  public boolean sameDay(Transaction other) {
    if (other == null) {
      throw new IllegalArgumentException("The compared object is null");
    }
    return getDate().isEqual(other.getDate());
  }

  /**
   * Compare the transaction with another transaction.
   * @param o the object to be compared.
   * @return -1 if the date is before, 1 if the date is after, if it is the same date, compare the
   *        indices
   */
  @Override
  public int compareTo(Transaction o) {
    if (o == null) {
      throw new IllegalArgumentException("Cannot compare null objects.");
    }

    if (isBefore(o)) {
      return -1;
    } else if (sameDay(o)) {
      return Integer.compare(getIndex(), o.getIndex());
    } else {
      return 1;
    }
  }
}
