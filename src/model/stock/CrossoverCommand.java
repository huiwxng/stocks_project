package model.stock;

import java.util.ArrayList;
import java.util.List;

import model.Date;

/**
 * Command to get the x-days crossover.
 */
public class CrossoverCommand implements StockCommand<List<String>> {

  private String start;
  private String end;
  private int x;

  /**
   * Constructs a net gain command that takes in a start date and an end date and calculates
   * the net gain/loss between those two days.
   * @param start start date
   * @param end end date
   * @param x x-days
   */
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

    checkValidDates(start, end, stock);

    List<String> dates = stock.getAllDates();
    List<String> temp = new ArrayList<>();

    int startI = stock.getIndex(start);
    int endI = stock.getIndex(end);

    for (int i = startI; i > endI - 1; i--) {
      String curr = dates.get(i);
      if (isCrossover(curr, stock)) {
        temp.add(curr);
      }
    }
    if (temp.isEmpty()) {
      temp.add("No x-day crossovers.");
    }

    return temp;
  }

  private boolean isCrossover(String date, Stock stock) {
    StockCommand<Double> movingAvg = new MovingAverageCommand(date, x);
    return stock.getClosingPrice(date) > movingAvg.execute(stock);
  }

  private void checkValidDates(String start, String end, Stock stock) {
    List<String> dates = stock.getAllDates();
    String oldest = dates.get(dates.size() - 1);
    String newest = dates.get(0);
    Date newestDate = new Date(newest);

    Date endDate = new Date(end);

    if (endDate.isBefore(oldest)) {
      throw new IllegalArgumentException("We do not have data before this end date.");
    }

    if (newestDate.isBefore(start)) {
      throw new IllegalArgumentException("We do not have data after this start date.");
    }

    if (endDate.isBefore(start)) {
      throw new IllegalArgumentException("The start date must be before the end date.");
    }
  }

  private String advanceStart(String start) {
    List<String> dates = stock.getAllDates();
    String oldest = dates.get(dates.size() - 1);
    Date startDate = new Date(start);

    while (startDate.isBefore(oldest)) {
      startDate.advance(1);
    }
  }
}
