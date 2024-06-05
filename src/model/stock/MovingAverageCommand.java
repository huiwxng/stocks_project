package model.stock;

import java.util.List;

/**
 * Command to get the x-days moving average.
 */
public class MovingAverageCommand implements StockCommand<Double> {

  private String date;
  private int x;

  /**
   * Constructs a moving average command that takes in a start date and x-days and calculates
   * the average value of the stock in that range of days.
   * @param date start date
   * @param x x-days to go back for the range
   * @throws IllegalArgumentException if x-days is negative
   */
  public MovingAverageCommand(String date, int x) throws IllegalArgumentException {
    if (x < 0) {
      throw new IllegalArgumentException("x-days cannot be negative.");
    }
    this.date = date;
    this.x = x;
  }

  /**
   * Executes the command onto a {@link Stock} object.
   *
   * @param stock {@link Stock} object
   */
  @Override
  public Double execute(Stock stock) {
    int start = stock.getIndex(date);
    if (start == -1) {
      throw new IllegalArgumentException("Data does not exist on this date.");
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
}
