package model.stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to get the x-days crossover.
 */
public class CrossoverCommand implements StockCommand<List<String>> {

  private String start;
  private String end;
  private int x;

  public CrossoverCommand(String start, String end, int x) throws IllegalArgumentException {
    if (x < 0) {
      throw new IllegalArgumentException("x-days cannot be negative.");
    }
    this.start = start;
    this.end = end;
    this.x = x;
  }

  /**
   * Executes the command onto a {@link Stock} object.
   *
   * @param stock {@link Stock} object
   */
  @Override
  public List<String> execute(Stock stock) {
    List<String> res = new ArrayList<>();
    int startI = stock.getIndex(start);
    int endI = stock.getIndex(end);
    if (startI == -1 || endI == -1) {
      throw new IllegalArgumentException("No data found on this date.");
    }
    if (startI < endI) {
      throw new IllegalArgumentException("The start date must be before the end date.");
    }

    List<String> dates = stock.getAllDates();

    for (int i = startI; i > endI - 1; i--) {
      String date = dates.get(i);
      StockCommand<Double> movingAvg = new MovingAverageCommand(date, x);
      if (stock.getClosingPrice(date) > movingAvg.execute(stock)) {
        res.add(date);
      }
    }
    return res;
  }
}
