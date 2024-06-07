package model.user;

import java.util.ArrayList;
import java.util.List;

import model.Date;
import model.stock.Stock;

/**
 * Command to get the x-days crossover.
 */
public class StockCrossoverCommand implements Command<List<String>> {

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
  public StockCrossoverCommand(String start, String end, int x) throws IllegalArgumentException {
    if (x < 0) {
      throw new IllegalArgumentException("x-days cannot be negative.");
    }
    this.start = start;
    this.end = end;
    this.x = x;
  }

  /**
   * Executes the command onto a {@link UserData} object.
   *
   * @param user {@link UserData} object
   */
  @Override
  public List<String> execute(UserData user) {

    Stock stock = user.getCurrentStock();
    if (stock == null) {
      throw new IllegalArgumentException("No current stock set.");
    }

    checkValidDates(start, end, stock);

    List<String> dates = stock.getAllDates();
    List<String> temp = new ArrayList<>();

    int startI = stock.getIndex(start);
    int endI = stock.getIndex(end);

    for (int i = startI; i > endI - 1; i--) {
      String curr = dates.get(i);
      if (isCrossover(curr, user)) {
        temp.add(curr);
      }
    }
    if (temp.isEmpty()) {
      temp.add("No x-day crossovers.");
    }

    return temp;
  }

  @Override
  public String getName() {
    return "crossover";
  }

  private boolean isCrossover(String date, UserData user) {
    Stock stock = user.getCurrentStock();

    Command<Double> movingAvg = new StockMovingAverageCommand(date, x);
    return stock.getClosingPrice(date) > user.execute(movingAvg);
  }

  private void checkValidDates(String start, String end, Stock stock) {
    List<String> dates = stock.getAllDates();
    String oldest = dates.get(dates.size() - 1);
    String newest = dates.get(0);
    Date newestDate = new Date(newest);

    Date startDate = new Date(start);
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

    if (startDate.isBefore(oldest)) {
      advanceStart(stock);
    }

    if (newestDate.isBefore(end)) {
      decreaseEnd(stock);
    }
  }

  private void advanceStart(Stock stock) {
    List<String> dates = stock.getAllDates();
    String oldest = dates.get(dates.size() - 1);
    Date startDate = new Date(start);

    while (startDate.isBefore(oldest)) {
      startDate.advance(1);
    }

    this.start = startDate.toString();
  }

  private void decreaseEnd(Stock stock) {
    List<String> dates = stock.getAllDates();
    String newest = dates.get(0);
    Date newestDate = new Date(newest);
    Date endDate = new Date(end);

    while (newestDate.isBefore(endDate.toString())) {
      endDate.advance(-1);
    }

    this.end = endDate.toString();
  }
}
