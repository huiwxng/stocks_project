package model.stock;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Basic implementation of the {@link Stock} interface.
 */
public class BasicStock implements Stock {

  private String apiKey = "PV8JPCAV6GLG3Y73"; // api key for Alpha Vantage
  private String ticker; // stock symbol / ticker
  private URL url; // url of the csv file

  public Stock(String ticker) {
    
  }

  /**
   * Gets the data (either from local files or the Alpha Vantage API
   *
   * @param ticker Stock symbol (ticker)
   */
  @Override
  public void getData(String ticker) {
    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=full"
              + "&symbol"
              + "=" + stockSymbol + "&apikey="+apiKey+"&datatype=csv");
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("the alphavantage API has either changed or "
              + "no longer works");
    }

    InputStream in = null;
    StringBuilder output = new StringBuilder();
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
