package model.user;

import model.stock.Stock;

/**
 * Command to get the net gain of a stock.
 */
public class StockNetGainCommand implements Command<Double> {

  private String start;
  private String end;

  /**
   * Constructs a net gain command that takes in a start date and an end date and calculates
   * the net gain/loss between those two days.
   * @param start start date
   * @param end end date
   */
  public StockNetGainCommand(String start, String end) {
    this.start = start;
    this.end = end;
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

    int startI = stock.getIndex(start);
    int endI = stock.getIndex(end);
    if (startI == -1 || endI == -1) {
      throw new IllegalArgumentException("No data found on this date.");
    }
    // if the end date is earlier than the start date, throw error
    // since the arrays go from most recent to oldest, the sign is flipped
    if (startI < endI) {
      throw new IllegalArgumentException("The start date must be before the end date.");
    }
    double netGain = stock.getClosingPrice(end) - stock.getClosingPrice(start);
    String str = String.format("%.2f", netGain);
    return Double.valueOf(str);
  }

  @Override
  public String getName() {
    return "net gain";
  }
}
