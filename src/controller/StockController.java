package controller;

import java.io.IOException;

import java.util.List;
import java.util.Scanner;

import model.commands.PortfolioRebalanceCommand;
import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.Date;
import model.stock.Stock;
import model.commands.Command;
import model.commands.LoadPortfolioCommand;
import model.commands.PortfolioGetValueCommand;
import model.commands.StockCrossoverCommand;
import model.commands.StockMovingAverageCommand;
import model.commands.StockNetGainCommand;
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
      writeMessage("Select menu option (number): ");
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
      default:
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
      case "2":
        loadPortfolio(scanner);
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

  private void loadPortfolio(Scanner scanner) {
    writeMessage("Filename (no .csv): ");
    String fileName = scanner.nextLine();
    try {
      Command<String> command = new LoadPortfolioCommand(fileName);
      command.execute(userData);
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void selectPortfolio(String userInput) {
    int userInputNum;
    try {
      userInputNum = Integer.parseInt(userInput);
      if (userInputNum >= 3 && userInputNum <= userData.getNumPortfolios() + 2) {
        userData.setCurrentPortfolio(userData.getPortfolio(userInputNum - 3));
        state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
      } else {
        lineSeparator();
        writeMessage("There is no portfolio with that number.\n");
      }
    } catch (NumberFormatException e) {
      lineSeparator();
      writeMessage("Invalid input. Please try again.\n");
    }
  }

  private void helpSpecificPortfolioMenu(String userInput, Scanner scanner) {
    switch (userInput) {
      case "1":
        viewStocks(scanner);
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
        rebalancePortfolio(scanner);
        break;
      case "6":
        deletePortfolio();
        break;
      case "7":
        savePortfolio();
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

  private void viewStocks(Scanner scanner) {
    String date = formatDate(setDate(scanner));
    lineSeparator();
    writeMessage("Stocks in " + userData.getCurrentPortfolio().getName() + " on " + date + ":\n");
    for (String stock : userData.getCurrentPortfolio().getComposition(date)) {
      writeMessage(stock + "\n");
    }
  }

  private void portfolioValue(Scanner scanner) {
    String date = formatDate(setDate(scanner));
    List<String> distribution = userData.getCurrentPortfolio().getDistribution(date);
    writeMessage("Portfolio Distribution:\n");
    for (String str : distribution) {
      writeMessage(str + "\n");
    }
    Command<Double> command = new PortfolioGetValueCommand(date);
    try {
      double value = userData.execute(command);
      lineSeparator();
      writeMessage("Portfolio value: $" + value + "\n");
    } catch (IllegalArgumentException e) {
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void addStocks(Scanner scanner) {
    String addedTicker = "";
    int addedShareCount = 0;
    boolean validTickerToAdd = false;
    while (!validTickerToAdd) {
      writeMessage("Stock Ticker (to be added): ");
      addedTicker = scanner.nextLine().trim();
      boolean validShareCountToAdd = false;
      while (!validShareCountToAdd) {
        try {
          writeMessage("Number of shares to add: ");
          addedShareCount = Integer.parseInt(scanner.nextLine().trim());
          String date = formatDate(setDate(scanner));
          try {
            userData.getCurrentPortfolio().buyStock(addedTicker, addedShareCount, date);
            lineSeparator();
            writeMessage("Added " + addedShareCount + " " + addedTicker.toUpperCase()
                    + " to " + userData.getCurrentPortfolio().getName() + "\n");
          } catch (Exception e) {
            lineSeparator();
            writeMessage(e.getMessage() + " Please try again.\n");
          }
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
    String removedTicker = "";
    int removedShareCount = 0;
    boolean validTickerToRemove = false;
    while (!validTickerToRemove) {
      writeMessage("Stock Ticker (to be removed): ");
      removedTicker = scanner.nextLine().trim();
      boolean validShareCountToRemove = false;
      while (!validShareCountToRemove) {
        try {
          writeMessage("Number of shares to remove: ");
          removedShareCount = Integer.parseInt(scanner.nextLine().trim());
          String date = formatDate(setDate(scanner));
          try {
            userData.getCurrentPortfolio().sellStock(removedTicker, removedShareCount, date);
            lineSeparator();
            writeMessage("Removed " + removedShareCount + " " + removedTicker.toUpperCase()
                    + " from " + userData.getCurrentPortfolio().getName() + "\n");
          } catch (Exception e) {
            lineSeparator();
            writeMessage(e.getMessage() + " Please try again.\n");
          }
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

  private void rebalancePortfolio(Scanner scanner) {
    String date = formatDate(setDate(scanner));
    List<Stock> currentStocks = userData.getCurrentPortfolio().getStocks(date);
    int[] weights = new int[currentStocks.size()];
    for (int i = 0; i < currentStocks.size(); i++) {
      writeMessage("Weight for " + currentStocks.get(i).getTicker() + " (0%-100%): ");
      String weight = scanner.nextLine().trim();
      while (!isValidWeight(weight)) {
        lineSeparator();
        writeMessage("Invalid weight. Please try again.\n");
        lineSeparator();
        writeMessage("Weight for " + currentStocks.get(i).getTicker() + " (): ");
        weight = scanner.nextLine().trim();
      }
      weights[i] = Integer.parseInt(weight);
    }
    Command<String> command = new PortfolioRebalanceCommand(date, weights);
    try {
      command.execute(userData);
      lineSeparator();
      writeMessage("Stocks for " + userData.getCurrentPortfolio().getName() + " on "
              + date + " rebalanced.\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
      lineSeparator();
    }
  }

  private void deletePortfolio() {
    userData.removePortfolio(userData.getCurrentPortfolio());
    lineSeparator();
    writeMessage("Portfolio " + userData.getCurrentPortfolio().getName() + " deleted.\n");
    state = ControllerState.PORTFOLIO_MENU;
  }

  private void savePortfolio() {
    lineSeparator();
    writeMessage(userData.getCurrentPortfolio().save() + "\n");
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
    writeMessage("Last Closing Price: $"
            + userData.getCurrentStock().getAllClosingPrices()
            .get(userData.getCurrentStock().getAllClosingPrices().size() - 1) + "\n");
  }

  private void closingPrice(Scanner scanner) {
    String date = formatDate(setDate(scanner));
    try {
      lineSeparator();
      writeMessage("Closing Price for " + formatDate(date) + ": $"
              + userData.getCurrentStock().getClosingPrice(date) + "\n");
    } catch (IllegalArgumentException e) {
      writeMessage(e.getMessage() + " Please try again.\n");
      lineSeparator();
    }
  }

  private void netGain(Scanner scanner) {
    String start = setStartDate(scanner);
    String end = setEndDate(scanner, start);
    try {
      Command<Double> command = new StockNetGainCommand(start, end);
      double netGain = userData.execute(command);
      lineSeparator();
      writeMessage("Net Gain from " + start + " to " + end + ": $" + netGain + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      writeMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayMovingAverage(Scanner scanner) {
    String date = formatDate(setDate(scanner));
    String xDays = setXDays(scanner);
    try {
      Command<Double> command = new StockMovingAverageCommand(date, Integer.parseInt(xDays));
      double movingAverage = userData.execute(command);
      lineSeparator();
      writeMessage("X-Day Moving Average on " + formatDate(date) + " for " + xDays + " days: $"
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

  private String setDate(Scanner scanner) {
    StringBuilder date = new StringBuilder();
    String year;
    String month;
    String day;
    boolean validYear = false;
    boolean validMonth = false;
    boolean validDay = false;
    boolean validDate = false;
    while (!validDate) {
      lineSeparator();
      writeMessage("Date: \n");
      while (!validYear) {
        writeMessage("Year: ");
        year = scanner.nextLine().trim();
        if (isValidYear(year)) {
          validYear = true;
          date.append(year);
        } else {
          lineSeparator();
          writeMessage("Invalid year. Please try again.\n");
          lineSeparator();
        }
      }
      while (!validMonth) {
        writeMessage("Month: ");
        month = scanner.nextLine().trim();
        if (isValidMonth(month)) {
          validMonth = true;
          date.append('-').append(month);
        } else {
          lineSeparator();
          writeMessage("Invalid month. Please try again.\n");
          lineSeparator();
        }
      }
      while (!validDay) {
        writeMessage("Day: ");
        day = scanner.nextLine().trim();
        if (isValidDay(day)) {
          validDay = true;
          date.append('-').append(day);
        } else {
          lineSeparator();
          writeMessage("Invalid day. Please try again.\n");
          lineSeparator();
        }
      }
      if (isValidDate(date.toString())) {
        validDate = true;
      } else {
        lineSeparator();
        writeMessage("Invalid date. Please try again.\n");
      }
    }
    return date.toString();
  }

  private String setStartDate(Scanner scanner) {
    lineSeparator();
    writeMessage("Start Date: \n");
    return setDate(scanner);
  }

  private String setEndDate(Scanner scanner, String startDateString) {
    lineSeparator();
    writeMessage("End Date: \n");
    return setDate(scanner);
  }

  private String setXDays(Scanner scanner) {
    String xDays = "";
    boolean validXDays = false;
    while (!validXDays) {
      writeMessage("X-Days: ");
      xDays = scanner.nextLine().trim();
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
      default:
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
    writeMessage("2: Load Portfolio (from a CSV file)\n");
    int portfolioIndex = 3;
    for (int i = 0; i < userData.getNumPortfolios(); i++) {
      writeMessage(portfolioIndex++ + ": " + userData.getPortfolio(i).getName() + "\n");
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
    writeMessage("5: Rebalance Portfolio\n");
    writeMessage("6: Delete Portfolio\n");
    writeMessage("7: Save Portfolio (to a CSV file)\n");
    returnPrompt();
    quitPrompt();
  }

  private void printStockMenu() {
    lineSeparator();
    try {
      String ticker = userData.getCurrentStock().getTicker();
      writeMessage(ticker + "\n");
    } catch (IllegalArgumentException e) {
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
    lineSeparator();
  }

  private void returnPrompt() {
    writeMessage("(r or return to go back)\n");
  }

  private void quitPrompt() {
    writeMessage("(q or quit to quit)\n");
  }

  private boolean isValidDate(String date) {
    try {
      Date valid = new Date(date);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private String formatDate(String date) {
    String[] arr = date.split("-");
    Integer[] intArr = new Integer[3];
    for (int i = 0; i < arr.length; i++) {
      intArr[i] = Integer.parseInt(arr[i]);
    }
    return String.format("%d-%02d-%02d", intArr[0], intArr[1], intArr[2]);
  }

  private boolean isValidWeight(String weight) {
    try {
      int weightNum = Integer.parseInt(weight);
      if (weightNum >= 0 && weightNum <= 100) {
        return true;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return false;
  }

  private boolean isValidYear(String year) {
    try {
      int yearNum = Integer.parseInt(year);
      if (yearNum > 0) {
        return true;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return false;
  }

  private boolean isValidMonth(String month) {
    try {
      int monthNum = Integer.parseInt(month);
      if (monthNum > 0 && monthNum < 13) {
        return true;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return false;
  }

  private boolean isValidDay(String day) {
    try {
      int dayNum = Integer.parseInt(day);
      if (dayNum > 0 && dayNum < 31) {
        return true;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return false;
  }

  private void lineSeparator() {
    writeMessage("-------------------------------------------------\n");
  }
}