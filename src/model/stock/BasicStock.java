package model.stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of the {@link Stock} interface.
 */
public class BasicStock implements Stock {

  private String ticker; // stock symbol / ticker
  private List<String> dates;
  private List<Double> closingPrices;

  public BasicStock(String ticker) {
    this.ticker = ticker;
    this.getData();
  }

  /**
   * Gets the data (either from local files or the Alpha Vantage API
   */
  @Override
  public void getData() {
    String path = "/res/data/" + ticker + ".csv";
    if (!Files.exists(Path.of(path))) {
      getDataFromAPI();
    }
    readCSV(path);
  }

  private void getDataFromAPI() {
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

      while ((b = in.read())!=-1) {
        output.append((char) b);
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + ticker);
    }

    try (FileWriter writer = new FileWriter("/res/data/" + ticker + ".csv")) {
      writer.write(output.toString());
    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
    }
  }

  private void readCSV(String path) {
    dates = new ArrayList<>();
    closingPrices = new ArrayList<>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(path);

      String line = br.readLine();
      String[] headers = line.split(",");
      int dateIndex = findIndex(headers, "timestamp");
      int closingIndex = findIndex(headers, "close");

      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        dates.add(values[dateIndex]);
        closingPrices.add(Double.parseDouble(values[closingIndex]));
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    } catch (NumberFormatException e) {
      System.err.println("Error parsing number: " + e.getMessage());
    }
  }

  private int findIndex(String[] headers, String label) {
    for (int i = 0; i < headers.length; i++) {
      if (headers[i].equalsIgnoreCase(label)) {
        return i;
      }
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
