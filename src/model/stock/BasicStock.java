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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of the {@link Stock} interface. In this implementation,
 * stocks are represented by their ticker, a list of dates, and prices correlated to
 * those dates.
 */
public class BasicStock implements Stock {

  private final String ticker; // stock symbol / ticker
  private List<String> dates;
  private List<Double> prices;
  private final String path;

  /**
   * Constructs a basic stock object.
   * @param ticker of the stock
   */
  public BasicStock(String ticker) {
    this.ticker = ticker;
    this.path = "data/" + ticker + ".csv";
    this.getData();
  }

  /**
   * Gets the ticker of the stock object.
   *
   * @return a String with the Stock Symbol or Ticker
   */
  @Override
  public String getTicker() {
    return ticker.toUpperCase();
  }

  /**
   * Gets a list of all the closing prices.
   * @return a list of the closing prices
   */
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
    LocalDate todayDate = LocalDate.now();
    String today = todayDate.toString();
    LocalDate current = LocalDate.now();
    if (current.isBefore(LocalDate.parse(date))) {
      throw new IllegalArgumentException("We cannot predict future stock price.");
    }
    int i = getIndex(date);
    if (i == -1) {
      throw new IllegalArgumentException("No data on this date.");
    }
    return prices.get(i);
  }

  /**
   * Gets the index of the closing prices list given the date.
   *
   * @param date specified date
   * @return the index of the closing prices list
   */
  public int getIndex(String date) {
    if (outOfRange(date)) {
      return -1;
    }

    String recent = getMostRecentDate(date);

    return dates.indexOf(recent);
  }

  private boolean outOfRange(String date) {
    LocalDate current = LocalDate.now();
    String oldest = dates.get(dates.size() - 1);
    return current.isBefore(LocalDate.parse(oldest));
  }

  private String getMostRecentDate(String date) {
    LocalDate recent = LocalDate.parse(date);
    if (dates.contains(recent.toString())) {
      return recent.toString();
    } else {
      recent = recent.minusDays(1);
      return getMostRecentDate(recent.toString());
    }
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

      if (response.contains("Error Message") || response.contains("Information")) {
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
    String path = "apikey.txt";  // Adjust this path as needed

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      apiKey = br.readLine().trim();
    } catch (IOException e) {
      System.err.println("Error reading API key file: " + e.getMessage());
      System.err.println("Please ensure that the file '" + path
              + "' exists and contains a valid API key.");
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
}
