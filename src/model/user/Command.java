package model.user;

/**
 * Interface for commands.
 */
public interface Command<T> {

  /**
   * Executes the command onto a {@link UserData} object.
   * @param user {@link UserData} object
   * @return a value given the command
   */
  T execute(UserData user);
}
