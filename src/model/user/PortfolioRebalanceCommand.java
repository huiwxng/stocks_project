package model.user;

public class PortfolioRebalanceCommand implements Command<String> {
  public PortfolioRebalanceCommand(int... weights) {
    int sum = 0;
    for (int weight : weights) {
      
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
