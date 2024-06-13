package controller;

/**
 * Interface for the controller of our stocks simulation program.
 * Implementations of this interface "control" and manage the flow
 * between the model, view, and user input.
 */
public interface IController {
  /**
   * The main method that relinquishes control of the application to the controller.
   *
   * @throws IllegalStateException if the controller is unable to transmit output.
   * @throws InterruptedException if there is a problem with the Thread sleep.
   */
  void control() throws IllegalStateException, InterruptedException;
}
