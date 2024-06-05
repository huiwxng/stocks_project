package model.stock;

/**
 * Interface for stock commands.
 */
public interface StockCommand {

  /**
   * Executes the command onto a {@link Stock} object.
   * @param stock {@link Stock} object
   */
  void execute(Stock stock);
}
