package model.stock;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Basic implementation of the {@link Stock} interface.
 */
public class BasicStock implements Stock {

  private String ticker; // stock symbol / ticker

  public BasicStock(String ticker) {
    this.ticker = ticker;
    this.getData();
  }

  /**
   * Gets the data (either from local files or the Alpha Vantage API
   *
   * @param ticker Stock symbol (ticker)
   */
  @Override
  public void getData() {

  }

  private getDataFromAPI() {
    String apiKey = "PV8JPCAV6GLG3Y73";
    URL url;

    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=full&symbol=" + ticker
              + "&apikey=" + apiKey + "&datatype=csv");
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("the Alpha Vantage API has either changed or "
              + "no longer works");
    }

    InputStream in = null;
    StringBuilder output = new StringBuilder();

    try {
      in = url.openStream();
      int b;

      while ((b=in.read())!=-1) {
        output.append((char)b);
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("No price data found for "+stockSymbol);
    }
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
