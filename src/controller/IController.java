package controller;

public interface IController {
  /**
   * The main method that relinquishes control of the application to the controller.
   *
   * @throws IllegalStateException if the controller is unable to transmit output.
   * @throws InterruptedException if there is a problem with the Thread sleep.
   */
  void control() throws IllegalStateException, InterruptedException;
}
