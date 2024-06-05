package model.portfolio;

/**
 * Interface for portfolio commands.
 */
public interface PortfolioCommand<T> {

  /**
   * Executes the command onto the {@link Portfolio} object.
   * @param portfolio {@link Portfolio} object
   * @return a value given the command
   */
  T execute(Portfolio portfolio);
}
