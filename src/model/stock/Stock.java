package model.stock;

import java.util.List;

/**
 * Interface that represents a singular stock.
 */
public interface Stock {

  /**
   * Gets the ticker of the stock object.
   * @return a String with the Stock Symbol or Ticker
   */
  String getTicker();

  /**
   * Gets a list of all the closing prices with the corresponding dates.
   * @return a list of the closing prices
   */
  List<String> getAllClosingPricesWithDates();

  /**
   * Gets a list of all the closing prices.
   * @return a list of the closing prices
   */
  List<Double> getAllClosingPrices();

  /**
   * Gets a list of all the valid dates.
   * @return a list of dates
   */
  List<String> getAllDates();

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
}
