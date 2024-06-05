package stocks;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Gets stock data given the ticker either locally or through the Alpha Vantage API.
 */
public class StockDataGetter {
  private final String apiKey = "PV8JPCAV6GLG3Y73"; // api key for
  private String ticker; // ticker symbol
  private URL url;

  /**
   * Constructs a getter for the stock data.
   * @param ticker stock ticker
   */
  public StockDataGetter(String ticker) {
    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=full"
              + "&symbol"
              + "=" + ticker + "&apikey="
              +apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException("the Alpha Vantage API has either changed or "
              + "no longer works");
    }
  }
}
