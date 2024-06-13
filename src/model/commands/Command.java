package model.commands;

import model.user.UserData;

/**
 * Interface for commands, ranging from commands conducted on portfolios, stocks,
 * or anything else to be implemented in the program.
 */
public interface Command<T> {

  /**
   * Executes the command onto a {@link UserData} object.
   * @param user {@link UserData} object
   * @return a value given the command
   */
  T execute(UserData user);

  /**
   * Gets the name of the command being executed.
   * @return the String for the name of the command.
   */
  String getName();
}
