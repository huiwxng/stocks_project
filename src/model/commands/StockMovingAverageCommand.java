package model.commands;

import java.util.List;

import model.stock.Stock;
import model.user.UserData;

/**
 * Command to get the x-days moving average, which is the average of the closing
 * prices of the last x-days.
 */
public class StockMovingAverageCommand implements Command<Double> {

  private String date;
  private int x;

  /**
   * Constructs a moving average command that takes in a start date and x-days and calculates
   * the average value of the stock in that range of days.
   * @param date start date
   * @param x x-days to go back for the range
   * @throws IllegalArgumentException if x-days is negative
   */
  public StockMovingAverageCommand(String date, int x) throws IllegalArgumentException {
    if (x < 0) {
      throw new IllegalArgumentException("X-Days cannot be negative.");
    }
    this.date = date;
    this.x = x;
  }

  /**
   * Executes the command onto a {@link UserData} object.
   *
   * @param user {@link UserData} object
   */
  @Override
  public Double execute(UserData user) {
    Stock stock = user.getCurrentStock();
    if (stock == null) {
      throw new IllegalArgumentException("No current stock set.");
    }

    int start = stock.getIndex(date);
    if (start == -1) {
      throw new IllegalArgumentException("No data found on this date.");
    }

    double total = 0;
    int count = 0;
    List<Double> prices = stock.getAllClosingPrices();
    for (int i = start; i < prices.size() && x > 0; i++) {
      total += prices.get(i);
      count++;
      x--;
    }
    double movingAvg = total / count;
    String str = String.format("%.2f", movingAvg);
    return Double.valueOf(str);
  }

  @Override
  public String getName() {
    return "moving average";
  }
}
