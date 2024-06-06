package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import model.portfolio.BasicPortfolio;
import model.portfolio.GetValueCommand;
import model.portfolio.Portfolio;
import model.portfolio.PortfolioCommand;
import model.stock.Stock;
import model.user.UserData;

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
  private final Appendable out;
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
  public StockController(UserData userData, Readable in, Appendable out) {
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
  public void control() throws IllegalStateException, InterruptedException {
    Scanner scanner = new Scanner(in);
    this.welcomeMessage();
    while (this.state != ControllerState.QUIT) {
      printCurrentMenu();
      writeMessage("Select menu option: ");
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
        break;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
      default:
        writeMessage("------------------------------\n");
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
        writeMessage("------------------------------\n");
        writeMessage(name + " portfolio created.\n");
        state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
        currentPortfolio = portfolio;
        break;
      case "r":
      case "return":
        state = ControllerState.START_MENU;
        break;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
      default:
        int userInputNum = 0;
        try {
          userInputNum = Integer.parseInt(userInput);
          if (userInputNum >= 2 && userInputNum <= userData.listPortfolios().size() + 1) {
            currentPortfolio = userData.listPortfolios().get(userInputNum - 2);
            state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
          } else {
            writeMessage("------------------------------\n");
            writeMessage("There is no portfolio with that number.\n");
          }
        } catch (NumberFormatException e) {
          writeMessage("------------------------------\n");
          writeMessage("Invalid menu option. Please try again.\n");
        }
        break;
    }
  }

  private void helpSpecificPortfolioMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        writeMessage("------------------------------\n");
        writeMessage("Stocks in current portfolio:\n");
        for (String stock : currentPortfolio.getStocksWithAmt()) {
          writeMessage(stock + "\n");
        }
        break;
      case "2":
        boolean validDate = false;
        while (!validDate) {
          writeMessage("Date (YYYY-MM-DD): ");
          String date = scanner.next();
          if (isValidDate(date)) {
            PortfolioCommand<Double> command = new GetValueCommand(date);
            try {
              double value = command.execute(currentPortfolio);
              writeMessage("------------------------------\n");
              writeMessage("Portfolio value: " + value + "\n");
            } catch (IllegalArgumentException e) {
              writeMessage(e.getMessage() + " Please try again.\n");
            }
            validDate = true;
          } else {
            writeMessage("------------------------------\n");
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
              writeMessage("Number of shares to add: ");
              int addedShareCount = Integer.parseInt(scanner.next());
              currentPortfolio.addStock(addedTicker, addedShareCount);
              writeMessage("------------------------------\n");
              writeMessage("Added " + addedShareCount + " " + addedTicker + " to your portfolio.\n");
              validShareCountToAdd = true;
              validTickerToAdd = true;
            } catch (NumberFormatException e) {
              writeMessage("------------------------------\n");
              writeMessage("Invalid stock share amount. Please try again.\n");
            } catch (IllegalArgumentException e) {
              writeMessage("------------------------------\n");
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
              writeMessage("Number of shares to remove: ");
              int removedShareCount = Integer.parseInt(scanner.next());
              int remove = currentPortfolio.removeStock(removedTicker, removedShareCount);
              writeMessage("------------------------------\n");
              writeMessage("Removed " + remove + " "
                      + removedTicker + " from your portfolio.\n");
              validShareCountToRemove = true;
              validTickerToRemove = true;
            } catch (NumberFormatException e) {
              writeMessage("------------------------------\n");
              writeMessage("Stock share amount must be a whole number. Please try again.\n");
            } catch (IllegalArgumentException e) {
              writeMessage("------------------------------\n");
              writeMessage(e.getMessage() + " Please try again.\n");
              break;
            }
          }
        }
        break;
      case "5":
        userData.removePortfolio(currentPortfolio);
        writeMessage("------------------------------\n");
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
        writeMessage("------------------------------\n");
        writeMessage("Invalid input. Please try again.\n");
    }
  }

  private void helpStockMenu(String userInput, Scanner scanner) {
    
  }

  private void writeMessage(String message) throws IllegalStateException {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  private void printCurrentMenu() throws InterruptedException {
    switch (state) {
      case START_MENU:
        printStartMenu();
        break;
      case PORTFOLIO_MENU:
        printPortfolioMenu();
        break;
      case SPECIFIC_PORTFOLIO_MENU:
        Thread.sleep(500);
        printSpecificPortfolioMenu();
        break;
      case STOCK_MENU:
        Thread.sleep(500);
        printStockMenu();
        break;
    }
  }

  private void printStartMenu() {
    writeMessage("------------------------------\n");
    writeMessage("1: View Portfolios\n");
    writeMessage("2: View Stocks\n");
    quitPrompt();
  }

  private void printPortfolioMenu() {
    writeMessage("------------------------------\n");
    writeMessage("1: Create Portfolio\n");
    int portfolioIndex = 2;
    for (Portfolio portfolio : userData.listPortfolios()) {
      writeMessage(portfolioIndex++ + ": " + portfolio.getName() + "\n");
    }
    returnPrompt();
    quitPrompt();
  }

  private void printSpecificPortfolioMenu() {
    writeMessage("------------------------------\n");
    writeMessage(currentPortfolio.getName() + "\n");
    writeMessage("1: View Stocks\n");
    writeMessage("2: Portfolio Value\n");
    writeMessage("3: Add Stock\n");
    writeMessage("4: Remove Stock\n");
    writeMessage("5: Delete Portfolio\n");
    returnPrompt();
    quitPrompt();
  }

  private void printStockMenu() {
    writeMessage("------------------------------\n");
    writeMessage(currentStock.getTicker() + "\n");
    writeMessage("1: Last Closing Price\n");
    writeMessage("2: Stock Value\n");
    writeMessage("3: Net Gain\n");
    writeMessage("4: X-Day Moving Average\n");
    writeMessage("5: X-Day Crossovers\n");
    returnPrompt();
    quitPrompt();
  }

  private void welcomeMessage() {
    writeMessage("------------------------------\n");
    writeMessage("Welcome to the virtual stocks program!\n" +
            "In menus that are numbered, input your number option.\n" +
            "Otherwise, type string input if prompted.\n");
  }

  private void farewellMessage() {
    writeMessage("------------------------------\n");
    writeMessage("Thanks for using our virtual stocks program!\n");
  }

  private void returnPrompt() {
    writeMessage("(r or return to go back)\n");
  }

  private void quitPrompt() {
    writeMessage("(q or quit to quit)\n");
  }

  private boolean isValidDate(String date) {
    try {
      LocalDate.parse(date);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}