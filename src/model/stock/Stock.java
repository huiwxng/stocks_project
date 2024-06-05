package model.stock;

import java.util.List;

/**
 * Interface that represents a singular stock.
 */
public interface Stock {

  /**
   * Gets the data (either from local files or the Alpha Vantage API
   */
  void getData();

  /**
   * Gets the ticker of the stock object.
   * @return a String with the Stock Symbol or Ticker
   */
  String getTicker();

  /**
   * Gets a list of all the closing prices with the corresponding dates.
   * @return a list of the closing prices.
   */
  List<String> getAllClosingPrices();

  /**
   * Gets the closing price of the stock on a specified date.
   * @param date specified date
   * @return the closing price
   */
  double getClosingPrice(String date);

  /**
   * Gets the index of the closing prices list given the date.
   * @param date specified date
   * @return the index of the closing prices list
   */
  int getIndex(String date);

  /**
   * Executes a given stock command.
   * @param cmd stock command
   */
  void execute(StockCommand cmd);
}
