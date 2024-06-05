package controller;

import java.util.Scanner;

import model.portfolio.BasicPortfolio;
import model.portfolio.GetValueCommand;
import model.portfolio.Portfolio;
import model.portfolio.PortfolioCommand;
import model.stock.Stock;
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
  private ControllerState state;
  private Stock currentStock;
  private Portfolio currentPortfolio;

  /**
   * Create a controller that works with a specified UserData that contains
   * a list of Portfolios.
   *
   * @param userData the user/UserData to work with (model)
   * @param in       the Readable object for inputs
   * @param out      the Appendable object to transmit output
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
    Scanner scanner = new Scanner(in);
    this.welcomeMessage();
    while (this.state != ControllerState.QUIT) {
      printCurrentMenu();
      writeMessage("Input number: ");
      String userInput = scanner.next();
      processCommand(userInput, scanner);
    }
    this.farewellMessage();
  }

  private void processCommand(String userInput, Scanner scanner) {
    switch (state) {
      case START_MENU:
        helpStartMenu(userInput);
        break;
      case PORTFOLIO_MENU:
        helpPortfolioMenu(userInput, scanner);
        break;
      case SPECIFIC_PORTFOLIO_MENU:
        helpSpecificPortfolioMenu(userInput, scanner);
        break;
      case STOCK_MENU:
        helpStockMenu(userInput, scanner);
        break;
    }
  }

  private void helpStartMenu(String userInput) {
    switch (userInput) {
      case "1":
        state = ControllerState.PORTFOLIO_MENU;
        break;
      case "2":
        state = ControllerState.STOCK_MENU;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
      default:
        writeMessage("Invalid input. Please try again.\n");
    }
  }

  private void helpPortfolioMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        writeMessage("Name your portfolio: ");
        String name = scanner.next();
        Portfolio portfolio = new BasicPortfolio(name);
        userData.addPortfolio(portfolio);
        writeMessage(name + " portfolio created.");
        break;
      case "r":
      case "return":
        state = ControllerState.START_MENU;
        break;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
    }

    int userInputNum = 0;
    try {
      userInputNum = Integer.parseInt(userInput);
    } catch (NumberFormatException e) {
      writeMessage("Invalid input. Please try again.\n");
    }

    if (userInputNum < 2 || userInputNum > userData.listPortfolios().size() + 1) {
      writeMessage("Invalid input. Please try again.\n");
    } else {
      currentPortfolio = userData.listPortfolios().get(userInputNum - 2);
      state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
    }
  }

  private void helpSpecificPortfolioMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        writeMessage("Stocks in current portfolio:\n");
        for (String stock : currentPortfolio.getStocksWithAmt()) {
          writeMessage(stock + System.lineSeparator());
        }
        break;
      case "2":
        boolean validDate = false;
        while (!validDate) {
          writeMessage("Date (YYYY-MM-DD): ");
          String date = scanner.next();
          PortfolioCommand<Double> command = new GetValueCommand(date);
          try {
            double value = command.execute(currentPortfolio);
            writeMessage("Current portfolio value: " + value + System.lineSeparator());
            validDate = true;
          } catch (IllegalArgumentException e) {
            writeMessage("Invalid date. Please try again.\n");
          }
        }
        break;
      case "3":
        boolean validTickerToAdd = false;
        while (!validTickerToAdd) {
          writeMessage("Stock Ticker (to be added): ");
          String addedTicker = scanner.next();
          boolean validShareCountToAdd = false;
          while (!validShareCountToAdd) {
            try {
              writeMessage("New Stock Share Count: ");
              int addedShareCount = Integer.parseInt(scanner.next());
              currentPortfolio.addStock(addedTicker, addedShareCount);
              writeMessage("Added " + addedShareCount + " " + addedTicker + " to your portfolio.\n");
              validShareCountToAdd = true;
              validTickerToAdd = true;
            } catch (NumberFormatException e) {
              writeMessage("Invalid stock share amount. Please try again.\n");
            } catch (IllegalArgumentException e) {
              writeMessage("Invalid ticker or stock share amount. Please try again.\n");
              break;
            }
          }
        }
        break;
      case "4":
        boolean validTickerToRemove = false;
        while (!validTickerToRemove) {
          writeMessage("Stock Ticker (to be removed): ");
          String removedTicker = scanner.next();
          boolean validShareCountToRemove = false;
          while (!validShareCountToRemove) {
            try {
              writeMessage("Removed Stock Share Count: ");
              int removedShareCount = Integer.parseInt(scanner.next());
              currentPortfolio.removeStock(removedTicker, removedShareCount);
              writeMessage("Removed " + removedShareCount + " "
                      + removedTicker + " from your portfolio.\n");
              validShareCountToRemove = true;
              validTickerToRemove = true;
            } catch (NumberFormatException e) {
              writeMessage("Invalid stock share amount. Please try again.\n");
            } catch (IllegalArgumentException e) {
              writeMessage("Invalid ticker or stock share amount. Please try again.\n");
              break;
            }
          }
        }
        break;
      case "5":
        userData.removePortfolio(currentPortfolio);
        writeMessage("Portfolio " + currentPortfolio.getName() + " deleted.\n");
        state = ControllerState.PORTFOLIO_MENU;
        break;
      case "r":
      case "return":
        state = ControllerState.PORTFOLIO_MENU;
        break;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
      default:
        writeMessage("Invalid input. Please try again.\n");
    }
  }

  

  private void writeMessage(String message) {
    out.write(message);
  }

  private void printCurrentMenu() {
    switch (state) {
      case START_MENU:
        printStartMenu();
        break;
      case PORTFOLIO_MENU:
        printPortfolioMenu();
        break;
      case SPECIFIC_PORTFOLIO_MENU:
        printSpecificPortfolioMenu();
        break;
      case STOCK_MENU:
        printStockMenu();
        break;
    }
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
    writeMessage(currentPortfolio.getName());
    writeMessage("1: View Stocks\n");
    writeMessage("2: Portfolio Value\n");
    writeMessage("3: Add Stock\n");
    writeMessage("4: Remove Stock\n");
    writeMessage("5: Delete Portfolio\n");
    returnPrompt();
    quitPrompt();
  }

  private void printStockMenu() {
    writeMessage(currentStock.getTicker());
    writeMessage("1: Last Closing Price\n");
    writeMessage("2: Stock Value\n");
    writeMessage("3: Net Gain\n");
    writeMessage("4: X-Day Moving Average\n");
    writeMessage("5: X-Day Crossovers\n");
    returnPrompt();
    quitPrompt();
  }

  private void welcomeMessage() {
    writeMessage("""
            Welcome to the virtual stocks program!
            In menus that are numbered, input your number option.
            Otherwise, type string input if prompted.
            """);
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