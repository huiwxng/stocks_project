package model.user;

import java.nio.file.Files;
import java.nio.file.Path;

public class LoadPortfolioCommand implements Command<String> {
  private final String dirPath = "res/portfolios/";
  private final String ext = ".csv";
  private final String filename;
  private final String path;

  public LoadPortfolioCommand(String filename) {
    this.filename = filename;
    this.path = dirPath + filename + ext;

    if (!Files.exists(Path.of(path))) {
      throw new IllegalArgumentException("This file does not exist.");
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
    return null;
  }

  /**
   * Gets the name of the command being executed.
   *
   * @return the String for the name of the command.
   */
  @Override
  public String getName() {
    return "load portfolio";
  }
}
