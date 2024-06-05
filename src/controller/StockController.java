package controller;

import java.util.Scanner;

import model.user.UserData;
import view.StocksView;

/**
 * This class represents the controller of an interactive virtual stocks application.
 * This controller offers a simple text interface in which the user can select options
 * to view information on stocks or manage their virtual portfolios.
 *
 * <p>This controller works with any Readable to read its inputs and
 * any Appendable to transmit output. There is no official "view", so this
 * controller just uses an Appendable object.
 */
public class StockController {
  private final UserData userData;
  private final Readable in;
  private final StocksView out;

  /**
   * Create a controller that works with a specified UserData that contains
   * a list of Portfolios.
   *
   * @param userData the user/UserData to work with (model)
   * @param in the Readable object for inputs
   * @param out the Appendable object to transmit output
   */
  public StockController(UserData userData, Readable in, StocksView out) {
    if ((userData == null) || (in == null) || (out == null)) {
      throw new IllegalArgumentException("Sheet, readable or appendable is null");
    }
    this.userData = userData;
    this.in = in;
    this.out = out;
  }

  /**
   * The main method that relinquishes control of the application to the controller.
   *
   * @throws IllegalStateException if the controller is unable to transmit output
   */
  public void control() throws IllegalStateException {
  }

  private void processCommand(String userInstruction, Scanner sc, UserData userData) {
  }

  private void writeMessage(String message) throws IllegalStateException {

  }

  private void welcomeMessage() throws IllegalStateException {

  }
}
