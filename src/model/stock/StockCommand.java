package model.stock;

/**
 * Interface for stock commands.
 */
public interface StockCommand<T> {

  /**
   * Executes the command onto a {@link Stock} object.
   * @param stock {@link Stock} object
   * @return a value given the command
   */
  T execute(Stock stock);
}
