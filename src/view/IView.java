package view;

/**
 * This interface represents a view for our virtual stocks program. Implementations of this
 * interface deal with presenting a user interface for the users to see and interact with.
 */
public interface IView {

  /**
   * Shows a message on the interface to the user.
   * @param message the message to be shown
   */
  void showMessage(String message);
}
