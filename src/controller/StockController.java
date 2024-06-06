package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import model.portfolio.BasicPortfolio;
import model.portfolio.GetValueCommand;
import model.portfolio.Portfolio;
import model.portfolio.PortfolioCommand;
import model.stock.BasicStock;
import model.stock.CrossoverCommand;
import model.stock.Date;
import model.stock.MovingAverageCommand;
import model.stock.NetGainCommand;
import model.stock.Stock;
import model.stock.StockCommand;
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
        String ticker = scanner.next();
        try {
          currentStock = new BasicStock(ticker);
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
    String name = scanner.next();
    Portfolio portfolio = new BasicPortfolio(name);
    userData.addPortfolio(portfolio);
    lineSeparator();
    writeMessage(name + " portfolio created.\n");
    state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
    currentPortfolio = portfolio;
  }

  private void selectPortfolio(String userInput) {
    int userInputNum;
    try {
      userInputNum = Integer.parseInt(userInput);
      if (userInputNum >= 2 && userInputNum <= userData.listPortfolios().size() + 1) {
        currentPortfolio = userData.listPortfolios().get(userInputNum - 2);
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
        changeStocks(scanner, true);
        break;
      case "4":
        changeStocks(scanner, false);
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
    for (String stock : currentPortfolio.getStocksWithAmt()) {
      writeMessage(stock + "\n");
    }
  }

  private void portfolioValue(Scanner scanner) {
    boolean validDate = false;
    while (!validDate) {
      writeMessage("Date (YYYY-MM-DD): ");
      String date = scanner.next();
      if (isValidDate(date)) {
        PortfolioCommand<Double> command = new GetValueCommand(date);
        try {
          double value = command.execute(currentPortfolio);
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

  private void changeStocks(Scanner scanner, boolean add) {
    boolean validTicker = false;
    while (!validTicker) {
      if (add) {
        writeMessage("Stock Ticker (to be added): ");
      } else {
        writeMessage("Stock Ticker (to be removed): ");
      }
      String ticker = scanner.next();
      boolean validSharesAmount = false;
      while (!validSharesAmount) {
        try {
          if (add) {
            writeMessage("Number of shares to add: ");
            int addedShareCount = Integer.parseInt(scanner.next());
            currentPortfolio.addStock(ticker, addedShareCount);
            lineSeparator();
            writeMessage("Added " + addedShareCount + " " + ticker
                    + " to your portfolio.\n");
          } else {
            writeMessage("Number of shares to remove: ");
            int removedShareCount = Integer.parseInt(scanner.next());
            int remove = currentPortfolio.removeStock(ticker, removedShareCount);
            lineSeparator();
            writeMessage("Removed " + remove + " "
                    + ticker + " from your portfolio.\n");
          }
          validSharesAmount = true;
          validTicker = true;
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

  private void deletePortfolio() {
    userData.removePortfolio(currentPortfolio);
    lineSeparator();
    writeMessage("Portfolio " + currentPortfolio.getName() + " deleted.\n");
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
            + currentStock.getAllClosingPrices()
            .get(currentStock.getAllClosingPrices().size() - 1) + "\n");
  }

  private void closingPrice(Scanner scanner) {
    String date;
    boolean validDate = false;
    while (!validDate) {
      writeMessage("Date (YYYY-MM-DD): ");
      date = scanner.next();
      if (isValidDate(date)) {
        lineSeparator();
        writeMessage("Closing Price for " + date + ": "
                + currentStock.getClosingPrice(date) + "\n");
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
  }

  private void netGain(Scanner scanner) {
    String start = "";
    String end = "";
    Date startDate;
    Date endDate;
    boolean validStart = false;
    boolean validEnd = false;
    StockCommand<Double> command;
    while (!validStart) {
      writeMessage("Start Date (YYYY-MM-DD): ");
      start = scanner.next();
      if (isValidDate(start)) {
        validStart = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
    while (!validEnd) {
      writeMessage("End Date (YYYY-MM-DD): ");
      end = scanner.next();
      try {
        startDate = new Date(start);
        endDate = new Date(end);
        if (isValidDate(end) && startDate.isBefore(endDate.toString())) {
          validEnd = true;
        } else {
          lineSeparator();
          writeMessage("Invalid date. Please try again.\n");
        }
      } catch (IllegalArgumentException e) {
        lineSeparator();
        writeMessage(e.getMessage() + " Please try again.\n");
      }
    }
    try {
      command = new NetGainCommand(start, end);
      double netGain = command.execute(currentStock);
      lineSeparator();
      writeMessage("Net Gain from " + start + " to " + end + ": " + netGain + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayMovingAverage(Scanner scanner) {
    String date = "";
    String xDays = "";
    boolean validXDays = false;
    boolean validDate = false;
    StockCommand<Double> command;
    while (!validDate) {
      writeMessage("Date (YYYY-MM-DD): ");
      date = scanner.next();
      if (isValidDate(date)) {
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
    while (!validXDays) {
      writeMessage("X-Days: ");
      xDays = scanner.next();
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
    try {
      command = new MovingAverageCommand(date, Integer.parseInt(xDays));
      double movingAverage = command.execute(currentStock);
      lineSeparator();
      writeMessage("X-Day Moving Average on " + date + " for " + xDays + " days: "
              + movingAverage + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayCrossovers(Scanner scanner) {
    String start = "";
    String end = "";
    Date startDate;
    Date endDate;
    boolean validStart = false;
    boolean validEnd = false;
    String xDays = "";
    boolean validXDays = false;
    StockCommand<List<String>> command;
    while (!validStart) {
      writeMessage("Start Date (YYYY-MM-DD): ");
      start = scanner.next();
      if (isValidDate(start)) {
        validStart = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
    while (!validEnd) {
      writeMessage("End Date (YYYY-MM-DD): ");
      end = scanner.next();
      try {
        startDate = new Date(start);
        endDate = new Date(end);
        if (isValidDate(end) && startDate.isBefore(endDate.toString())) {
          validEnd = true;
        } else {
          lineSeparator();
          writeMessage("Invalid date. Please try again.\n");
        }
      } catch (IllegalArgumentException e) {
        lineSeparator();
        writeMessage(e.getMessage() + " Please try again.\n");
      }
    }
    while (!validXDays) {
      writeMessage("X-Days: ");
      xDays = scanner.next();
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
    try {
      command = new CrossoverCommand(start, end, Integer.parseInt(xDays));
      List<String> crossovers = command.execute(currentStock);
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
    lineSeparator();
    try {
      writeMessage(currentStock.getTicker() + "\n");
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