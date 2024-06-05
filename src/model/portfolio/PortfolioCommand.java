package model.portfolio;

/**
 * Interface for portfolio commands.
 */
public interface PortfolioCommand {

  /**
   * Executes the command onto the {@link Portfolio} object.
   * @param portfolio {@link Portfolio} object
   */
  void execute(Portfolio portfolio);
}
