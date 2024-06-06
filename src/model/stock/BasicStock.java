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
  private List<Double> prices;
  private String path;

  public BasicStock(String ticker) {
    this.ticker = ticker;
    this.path = "res/data/" + ticker + ".csv";
    this.getData();
  }

  /**
   * Gets the ticker of the stock object.
   *
   * @return a String with the Stock Symbol or Ticker
   */
  @Override
  public String getTicker() {
    return ticker;
  }

  /**
   * Gets a list of all the closing prices with the corresponding dates.
   *
   * @return a list of the closing prices.
   */
  @Override
  public List<String> getAllClosingPricesWithDates() {
    List<String> res = new ArrayList<>();
    for (int i = 0; i < prices.size(); i++) {
      res.add(String.format("%s: %.2f", dates.get(i), prices.get(i)));
    }
    return res;
  }

  @Override
  public List<Double> getAllClosingPrices() {
    return new ArrayList<>(prices);
  }

  /**
   * Gets a list of all the valid dates.
   *
   * @return a list of dates
   */
  @Override
  public List<String> getAllDates() {
    return new ArrayList<>(dates);
  }

  /**
   * Gets the closing price of the stock on a specified date.
   *
   * @param date specified date
   * @return the closing price
   */
  @Override
  public double getClosingPrice(String date) throws IllegalArgumentException {
    int i = getIndex(date);
    if (i == -1) {
      throw new IllegalArgumentException("No data on this date.");
    }
    return prices.get(i);
  }

  /**
   * Gets the index of the closing prices list given the date.
   * @param date specified date
   * @return the index of the closing prices list
   */
  public int getIndex(String date) {
    if (outOfRange(date)) {
      throw new IllegalArgumentException("There is no data for this date");
    }

    String recent = getMostRecentDate(date);

    for (int i = 0; i < dates.size(); i++) {
      if (dates.get(i).equals(recent)) {
        return i;
      }
    }
    return -1;
  }

  private boolean outOfRange(String date) {
    Date current = new Date(date);
    return current.isBefore(dates.get(dates.size() - 1));
  }

  private String getMostRecentDate(String date) {
    Date recent = new Date(date);
    if (dates.contains(recent.toString())) {
      return recent.toString();
    } else {
      recent.advance(-1);
      return getMostRecentDate(recent.toString());
    }
  }

  /**
   * Executes a given stock command.
   * @param cmd stock command
   * @param <T> return type of the command
   */
  @Override
  public <T> void executeReturn(StockCommand<T> cmd) {
    cmd.execute(this);
  }

  private void getData() {
    if (!Files.exists(Path.of(path))) {
      getDataFromAPI();
    }
    readCSV();
  }

  private void getDataFromAPI() {
    String apiKey = getAPIKey();
    URL url;

    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=full&symbol=" + ticker
              + "&apikey=" + apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException("the Alpha Vantage API has either changed or "
              + "no longer works");
    }

    InputStream in = null;
    StringBuilder output = new StringBuilder();

    try {
      in = url.openStream();
      int b;

      while ((b = in.read()) != -1) {
        output.append((char) b);
      }

      // Check if the API returned an error
      String response = output.toString();

      if (response.contains("Error Message")) {
        throw new IllegalArgumentException("The ticker '" + ticker
                + "' is not available on Alpha Vantage API or you have ran out of API requests.");
      }

      try (FileWriter writer = new FileWriter(path)) {
        writer.write(response);
      } catch (IOException e) {
        System.err.println("Error writing to file: " + e.getMessage());
      }

    } catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + ticker);
    }

    try (FileWriter writer = new FileWriter(path)) {
      writer.write(output.toString());
    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
    }
  }

  private void readCSV() {
    dates = new ArrayList<>();
    prices = new ArrayList<>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(path));

      String line = br.readLine();
      String[] headers = line.split(",");
      int dateIndex = findIndex(headers, "timestamp");
      int closingIndex = findIndex(headers, "close");

      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        dates.add(values[dateIndex]);
        prices.add(Double.parseDouble(values[closingIndex]));
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    } catch (NumberFormatException e) {
      System.err.println("Error parsing number: " + e.getMessage());
    }
  }

  private String getAPIKey() {
    String apiKey = null;
    String path = "res/apikey.txt";  // Adjust this path as needed

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      apiKey = br.readLine().trim();
    } catch (IOException e) {
      System.err.println("Error reading API key file: " + e.getMessage());
      System.err.println("Please ensure that the file '" + path + "' exists and contains a valid API key.");
      throw new RuntimeException("Failed to read API key", e);
    }

    if (apiKey == null || apiKey.isEmpty()) {
      throw new RuntimeException("API key is empty or not found in '" + path + "'");
    }

    return apiKey;
  }

  private int findIndex(String[] strList, String str) {
    for (int i = 0; i < strList.length; i++) {
      if (strList[i].equalsIgnoreCase(str)) {
        return i;
      }
    }
    return -1;
  }

  public static void main(String[] args) {
    Stock stock = new BasicStock("asdfasdf");

    Stock Apple = new BasicStock("AAPL");
    Stock Google = new BasicStock("GOOG");
    Stock Nvidia = new BasicStock("NVDA");
    Stock Amazon = new BasicStock("AMZN");
    Stock Tesla = new BasicStock("TSLA");
    Stock Meta = new BasicStock("META");
    Stock Microsoft = new BasicStock("MSFT");
    Stock JPMorgan = new BasicStock("JPM");
    Stock HomeDepot = new BasicStock("HD");
    Stock TSM = new BasicStock("TSM");
    Stock Walmart = new BasicStock("WMT");
    for (int i = 0; i < 10; i++) {
      System.out.println(Apple.getAllClosingPricesWithDates().get(i));
    }
    System.out.println(Apple.getClosingPrice("2002-01-10"));
    System.out.println(Apple.getClosingPrice("2024-06-01"));
    System.out.println(Apple.getClosingPrice("204-06-01"));
//    System.out.println(Google.getClosingPrice("2024-05-21"));
//    System.out.println(Nvidia.getClosingPrice("2024-05-20"));
//    System.out.println(Amazon.getClosingPrice("2024-05-20"));
//    System.out.println(Tesla.getClosingPrice("2023-04-21"));
//    System.out.println(Meta.getClosingPrice("2023-04-21"));
//    System.out.println(Microsoft.getClosingPrice("2020-01-23"));
//    System.out.println(JPMorgan.getClosingPrice("2024-05-21"));
//    System.out.println(HomeDepot.getClosingPrice("2024-05-21"));
//    System.out.println(TSM.getClosingPrice("2024-05-21"));
//    System.out.println(Walmart.getClosingPrice("2024-05-21"));
  }
}
