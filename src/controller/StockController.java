package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.Date;
import model.user.Command;
import model.user.PortfolioGetValueCommand;
import model.user.StockCrossoverCommand;
import model.user.StockMovingAverageCommand;
import model.user.StockNetGainCommand;
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
public class StockController implements IController {
  private final UserData userData;
  private final Readable in;
  private final Appendable out;
  private ControllerState state;

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

  @Override
  public void control() throws IllegalStateException, InterruptedException {
    Scanner scanner = new Scanner(in);
    this.welcomeMessage();
    while (this.state != ControllerState.QUIT) {
      printCurrentMenu();
      writeMessage("Select menu option: ");
      String userInput = scanner.nextLine().trim();
      processCommand(userInput, scanner);
    }
    this.farewellMessage();
  }

  private void processCommand(String userInput, Scanner scanner) {
    switch (state) {
      case START_MENU:
        helpStartMenu(userInput, scanner);
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

  private void helpStartMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        state = ControllerState.PORTFOLIO_MENU;
        break;
      case "2":
        writeMessage("Stock Ticker (to be viewed): ");
        String ticker = scanner.nextLine().trim();
        try {
          userData.setCurrentStock(ticker);
        } catch (Exception e) {
          lineSeparator();
          writeMessage(e.getMessage() + "\n");
        }
        state = ControllerState.STOCK_MENU;
        break;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
      default:
        lineSeparator();
        writeMessage("Invalid input. Please try again.\n");
    }
  }

  private void helpPortfolioMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        createPortfolio(scanner);
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
        selectPortfolio(userInput);
        break;
    }
  }

  private void createPortfolio(Scanner scanner) {
    writeMessage("Name your portfolio: ");
    String name = scanner.nextLine();
    Portfolio portfolio = new BasicPortfolio(name);
    userData.addPortfolio(portfolio);
    lineSeparator();
    writeMessage(name + " portfolio created.\n");
    state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
    userData.setCurrentPortfolio(portfolio);
  }

  private void selectPortfolio(String userInput) {
    int userInputNum;
    try {
      userInputNum = Integer.parseInt(userInput);
      if (userInputNum >= 2 && userInputNum <= userData.listPortfolios().size() + 1) {
        userData.setCurrentPortfolio(userData.getPortfolio(userInputNum - 2));
        state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
      } else {
        lineSeparator();
        writeMessage("There is no portfolio with that number.\n");
      }
    } catch (NumberFormatException e) {
      lineSeparator();
      writeMessage("Invalid menu option. Please try again.\n");
    }
  }

  private void helpSpecificPortfolioMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        viewStocks();
        break;
      case "2":
        portfolioValue(scanner);
        break;
      case "3":
        addStocks(scanner);
        break;
      case "4":
        removeStocks(scanner);
        break;
      case "5":
        deletePortfolio();
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
        lineSeparator();
        writeMessage("Invalid input. Please try again.\n");
    }
  }

  private void viewStocks() {
    lineSeparator();
    writeMessage("Stocks in current portfolio:\n");
    for (String stock : userData.getCurrentPortfolio().getStocksWithAmt()) {
      writeMessage(stock + "\n");
    }
  }

  private void portfolioValue(Scanner scanner) {
    boolean validDate = false;
    while (!validDate) {
      writeMessage("Date (YYYY-MM-DD): ");
      String date = scanner.nextLine().trim();
      if (isValidDate(date)) {
        Command<Double> command = new PortfolioGetValueCommand(date);
        try {
          double value = userData.execute(command);
          lineSeparator();
          writeMessage("Portfolio value: " + value + "\n");
        } catch (IllegalArgumentException e) {
          writeMessage(e.getMessage() + " Please try again.\n");
        }
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
  }

  private void addStocks(Scanner scanner) {
    boolean validTickerToAdd = false;
    while (!validTickerToAdd) {
      writeMessage("Stock Ticker (to be added): ");
      String addedTicker = scanner.nextLine().trim();
      boolean validShareCountToAdd = false;
      while (!validShareCountToAdd) {
        try {
          writeMessage("Number of shares to add: ");
          int addedShareCount = Integer.parseInt(scanner.nextLine().trim());
          userData.getCurrentPortfolio().addStock(addedTicker, addedShareCount);
          lineSeparator();
          writeMessage("Added " + addedShareCount + " " + addedTicker.toUpperCase()
                  + " to your portfolio.\n");
          validShareCountToAdd = true;
          validTickerToAdd = true;
        } catch (NumberFormatException e) {
          lineSeparator();
          writeMessage("Invalid stock share amount. Please try again.\n");
        } catch (IllegalArgumentException e) {
          lineSeparator();
          writeMessage("Invalid ticker or stock share amount. Please try again.\n");
          break;
        }
      }
    }
  }

  private void removeStocks(Scanner scanner) {
    boolean validTickerToRemove = false;
    while (!validTickerToRemove) {
      writeMessage("Stock Ticker (to be removed): ");
      String removedTicker = scanner.nextLine().trim();
      boolean validShareCountToRemove = false;
      while (!validShareCountToRemove) {
        try {
          writeMessage("Number of shares to remove: ");
          int removedShareCount = Integer.parseInt(scanner.nextLine().trim());
          int remove = userData.getCurrentPortfolio().removeStock(removedTicker, removedShareCount);
          lineSeparator();
          writeMessage("Removed " + remove + " "
                  + removedTicker.toUpperCase() + " from your portfolio.\n");
          validShareCountToRemove = true;
          validTickerToRemove = true;
        } catch (NumberFormatException e) {
          lineSeparator();
          writeMessage("Stock share amount must be a whole number. Please try again.\n");
        } catch (IllegalArgumentException e) {
          lineSeparator();
          writeMessage(e.getMessage() + " Please try again.\n");
          break;
        }
      }
    }
  }

  private void deletePortfolio() {
    userData.removePortfolio(userData.getCurrentPortfolio());
    lineSeparator();
    writeMessage("Portfolio " + userData.getCurrentPortfolio().getName() + " deleted.\n");
    state = ControllerState.PORTFOLIO_MENU;
  }

  private void helpStockMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        lastClosingPrice();
        break;
      case "2":
        closingPrice(scanner);
        break;
      case "3":
        netGain(scanner);
        break;
      case "4":
        xDayMovingAverage(scanner);
        break;
      case "5":
        xDayCrossovers(scanner);
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
        lineSeparator();
        writeMessage("Invalid input. Please try again.\n");
    }
  }

  private void lastClosingPrice() {
    lineSeparator();
    writeMessage("Last Closing Price: "
            + userData.getCurrentStock().getAllClosingPrices()
            .get(userData.getCurrentStock().getAllClosingPrices().size() - 1) + "\n");
  }

  private void closingPrice(Scanner scanner) {
    String date;
    boolean validDate = false;
    while (!validDate) {
      writeMessage("Date (YYYY-MM-DD): ");
      date = scanner.nextLine().trim();
      if (isValidDate(date)) {
        lineSeparator();
        writeMessage("Closing Price for " + date + ": "
                + userData.getCurrentStock().getClosingPrice(date) + "\n");
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
  }

  private void netGain(Scanner scanner) {
    String start = setStartDate(scanner);
    String end = setEndDate(scanner, start);
    try {
      Command<Double> command = new StockNetGainCommand(start, end);
      double netGain = userData.execute(command);
      lineSeparator();
      writeMessage("Net Gain from " + start + " to " + end + ": " + netGain + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayMovingAverage(Scanner scanner) {
    String date = "";
    boolean validDate = false;
    while (!validDate) {
      writeMessage("Date (YYYY-MM-DD): ");
      date = scanner.nextLine().trim();
      if (isValidDate(date)) {
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
    String xDays = setXDays(scanner);
    try {
      Command<Double> command = new StockMovingAverageCommand(date, Integer.parseInt(xDays));
      double movingAverage = userData.execute(command);
      lineSeparator();
      writeMessage("X-Day Moving Average on " + date + " for " + xDays + " days: "
              + movingAverage + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayCrossovers(Scanner scanner) {
    String start = setStartDate(scanner);
    String end = setEndDate(scanner, start);
    String xDays = setXDays(scanner);
    try {
      Command<List<String>> command
              = new StockCrossoverCommand(start, end, Integer.parseInt(xDays));
      List<String> crossovers = userData.execute(command);
      lineSeparator();
      for (int i = 0; i < crossovers.size(); i++) {
        if (i % 5 == 0) {
          if (i != 0) {
            writeMessage("\n");
          }
          writeMessage(crossovers.get(i));
        } else {
          writeMessage(", " + crossovers.get(i));
        }
      }
      writeMessage("\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private String setStartDate(Scanner scanner) {
    String date = "";
    boolean validDate = false;
    while (!validDate) {
      writeMessage("Start Date (YYYY-MM-DD): ");
      date = scanner.nextLine();
      if (isValidDate(date)) {
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
    return date;
  }

  private String setEndDate(Scanner scanner, String startDateString) {
    String date = "";
    boolean validDate = false;
    while (!validDate) {
      writeMessage("End Date (YYYY-MM-DD): ");
      date = scanner.nextLine();
      try {
        Date startDate = new Date(startDateString);
        if (isValidDate(date) && (startDate.isBefore(date) || startDate.sameDay(date))) {
          validDate = true;
        } else {
          lineSeparator();
          writeMessage("Invalid date. Please try again.\n");
        }
      } catch (IllegalArgumentException e) {
        lineSeparator();
        writeMessage(e.getMessage() + " Please try again.\n");
      }
    }
    return date;
  }

  private String setXDays(Scanner scanner) {
    String xDays = "";
    boolean validXDays = false;
    while (!validXDays) {
      writeMessage("X-Days: ");
      xDays = scanner.nextLine();
      try {
        int xDaysNum = Integer.parseInt(xDays);
        if (xDaysNum <= 0) {
          throw new NumberFormatException();
        }
        validXDays = true;
      } catch (NumberFormatException e) {
        lineSeparator();
        writeMessage("X-Days must be a positive integer. Please try again.\n");
      }
    }
    return xDays;
  }

  private void writeMessage(String message) throws IllegalStateException {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  private void printCurrentMenu() throws IllegalStateException, InterruptedException {
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
    lineSeparator();
    writeMessage("1: View Portfolios\n");
    writeMessage("2: View Stocks\n");
    quitPrompt();
  }

  private void printPortfolioMenu() {
    lineSeparator();
    writeMessage("1: Create Portfolio\n");
    int portfolioIndex = 2;
    for (Portfolio portfolio : userData.listPortfolios()) {
      writeMessage(portfolioIndex++ + ": " + portfolio.getName() + "\n");
    }
    returnPrompt();
    quitPrompt();
  }

  private void printSpecificPortfolioMenu() {
    lineSeparator();
    writeMessage(userData.getCurrentPortfolio().getName() + "\n");
    writeMessage("1: View Stocks\n");
    writeMessage("2: Portfolio Value\n");
    writeMessage("3: Buy Stock(s)\n");
    writeMessage("4: Sell Stock(s)\n");
    writeMessage("5: Delete Portfolio\n");
    returnPrompt();
    quitPrompt();
  }

  private void printStockMenu() {
    lineSeparator();
    try {
      String ticker = userData.getCurrentStock().getTicker();
      writeMessage(ticker + "\n");
    } catch (NullPointerException e) {
      writeMessage("You are not currently viewing a stock. Please try again.\n");
      state = ControllerState.START_MENU;
      printStartMenu();
      return;
    }
    writeMessage("1: Last Closing Price\n");
    writeMessage("2: Closing Price\n");
    writeMessage("3: Net Gain\n");
    writeMessage("4: X-Day Moving Average\n");
    writeMessage("5: X-Day Crossovers\n");
    returnPrompt();
    quitPrompt();
  }

  private void welcomeMessage() {
    lineSeparator();
    writeMessage("Welcome to the virtual stocks program!\n" +
            "In menus that are numbered, input your number option.\n" +
            "Otherwise, type string input if prompted.\n");
  }

  private void farewellMessage() {
    lineSeparator();
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

  private void lineSeparator() {
    writeMessage("-------------------------------------------------\n");
  }
}