package model.user;

public class PortfolioRebalanceCommand implements Command<String> {
  public PortfolioRebalanceCommand(String date, int... weights) {
    int sum = 0;
    for (int weight : weights) {
      sum += weight;
    }
    if (sum != 100) {
      throw new IllegalArgumentException("Weights must add up to 100.");
    }
  }

  /**
   * Executes the command onto a {@link UserData} object.
   *
   * @param user {@link UserData} object
   * @return a value given the command
   */
  @Override
  public String execute(UserData user) {
    return "";
  }

  /**
   * Gets the name of the command being executed.
   *
   * @return the String for the name of the command.
   */
  @Override
  public String getName() {
    return "";
  }
}
