package controller;

import java.util.Scanner;

import model.portfolio.Portfolio;
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
  private final ControllerState state;

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
    this.state = ControllerState.START_MENU;
  }

  /**
   * The main method that relinquishes control of the application to the controller.
   *
   * @throws IllegalStateException if the controller is unable to transmit output
   */
  public void control() throws IllegalStateException {
    Scanner sc = new Scanner(in);
    this.welcomeMessage();
    while (this.state != ControllerState.QUIT && sc.hasNext()) {

    }
  }

  private void processCommand(String userInstruction, Scanner sc, UserData userData) {
  }

  private void writeMessage(String message) {
    out.write(message);
  }

  private void printStartMenu() {
    writeMessage("1: View Portfolios\n");
    writeMessage("2: View Stocks\n");
    quitPrompt();
  }

  private void printPortfolioMenu() {
    writeMessage("1: Create Portfolio\n");
    int portfolioIndex = 1;
    for (Portfolio portfolio : userData.listPortfolios()) {
      writeMessage(portfolioIndex++ + ": " + portfolio.getName());
    }
    returnPrompt();
    quitPrompt();
  }

  private void printSpecificPortfolioMenu() {
    writeMessage("1: View Stocks\n");
    writeMessage("2: Portfolio Value\n");
    writeMessage("3: Add Stock\n");
    writeMessage("4: Remove Stock\n");
    writeMessage("5: Delete Portfolio\n");
    returnPrompt();
    quitPrompt();
  }

  private void printStockMenu() {
    writeMessage("1: Last Closing Price\n");
    writeMessage("2: Stock Value\n");
    writeMessage("3: Net Gain\n");
    writeMessage("4: X-Day Moving Average\n");
    writeMessage("5: X-Day Crossovers\n");
    returnPrompt();
    quitPrompt();
  }

  private void welcomeMessage() {
    writeMessage("Welcome to the virtual stocks program!\nChoose your input number.\n");
    printStartMenu();
  }

  private void farewellMessage() {
    writeMessage("Thanks for using our virtual stocks program!\n");
  }

  private void returnPrompt() {
    writeMessage("r or return to go back\n");
  }

  private void quitPrompt() {
    writeMessage("q or quit to quit\n");
  }
}
