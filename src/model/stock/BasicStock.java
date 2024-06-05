package model.stock;

import java.util.List;

/**
 * Basic implementation of the {@link Stock} interface.
 */
public class BasicStock implements Stock {

  /**
   * Gets the data (either from local files or the Alpha Vantage API
   *
   * @param ticker Stock symbol (ticker)
   */
  @Override
  public void getData(String ticker) {
    
  }

  /**
   * Gets the ticker of the stock object.
   *
   * @return a String with the Stock Symbol or Ticker
   */
  @Override
  public String getTicker() {
    return "";
  }

  /**
   * Gets a list of all the closing prices with the corresponding dates.
   *
   * @return a list of the closing prices.
   */
  @Override
  public List<String> getAllClosingPrices() {
    return List.of();
  }

  /**
   * Gets the closing price of the stock on a specified date.
   *
   * @param date specified date
   * @return the closing price
   */
  @Override
  public double getClosingPrice(String date) {
    return 0;
  }

  /**
   * Gets the index of the closing prices list given the date.
   *
   * @param date specified date
   * @return the index of the closing prices list
   */
  @Override
  public int getIndex(String date) {
    return 0;
  }

  /**
   * Executes a given stock command.
   *
   * @param cmd stock command
   */
  @Override
  public void execute(StockCommand cmd) {

  }
}
