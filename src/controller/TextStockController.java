package controller;

import java.time.LocalDate;
import java.util.List;

import model.commands.PortfolioPerformanceCommand;
import model.commands.PortfolioRebalanceCommand;
import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.stock.Stock;
import model.commands.Command;
import model.commands.LoadPortfolioCommand;
import model.commands.PortfolioGetValueCommand;
import model.commands.StockCrossoverCommand;
import model.commands.StockMovingAverageCommand;
import model.commands.StockNetGainCommand;
import model.user.UserData;
import view.TextView;

/**
 * This class represents the controller of an interactive virtual stocks application.
 * This controller offers a simple text interface in which the user can select options
 * to view information on stocks or manage their virtual portfolios.
 *
 * <p>This controller works with any Readable to read its inputs and
 * any Appendable to transmit output. There is no official "view", so this
 * controller just uses an Appendable object.
 */
public class TextStockController implements IController {
  private final UserData userData;
  private final TextView view;
  private ControllerState state;

  /**
   * Create a controller that works with a specified UserData that contains
   * a list of Portfolios.
   *
   * @param userData the user/UserData to work with (model)
   * @param view the view that the controller tells what to do
   */
  public TextStockController(UserData userData, TextView view) {
    if ((userData == null) || (view == null)) {
      throw new IllegalArgumentException("UserData or view is null.");
    }
    this.userData = userData;
    this.view = view;
    this.state = ControllerState.START_MENU;
  }

  @Override
  public void control() throws IllegalStateException, InterruptedException {
    welcomeMessage();
    while (this.state != ControllerState.QUIT) {
      printCurrentMenu();
      view.showMessage("Select menu option (number): ");
      String userInput = view.getUserInput();
      processCommand(userInput);
    }
    farewellMessage();
  }

  private void processCommand(String userInput) {
    switch (state) {
      case START_MENU:
        helpStartMenu(userInput);
        break;
      case PORTFOLIO_MENU:
        helpPortfolioMenu(userInput);
        break;
      case SPECIFIC_PORTFOLIO_MENU:
        helpSpecificPortfolioMenu(userInput);
        break;
      case STOCK_MENU:
        helpStockMenu(userInput);
        break;
      default:
        break;
    }
  }

  private void helpStartMenu(String userInput) {
    switch (userInput) {
      case "1":
        state = ControllerState.PORTFOLIO_MENU;
        break;
      case "2":
        view.showMessage("Stock Ticker (to be viewed): ");
        String ticker = view.getUserInput();
        try {
          userData.setCurrentStock(ticker);
        } catch (Exception e) {
          lineSeparator();
          view.showMessage(e.getMessage() + "\n");
        }
        state = ControllerState.STOCK_MENU;
        break;
      case "q":
      case "quit":
        state = ControllerState.QUIT;
        break;
      default:
        lineSeparator();
        view.showMessage("Invalid input. Please try again.\n");
    }
  }

  private void helpPortfolioMenu(String userInput) {
    switch (userInput) {
      case "1":
        createPortfolio();
        break;
      case "2":
        loadPortfolio();
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

  private void createPortfolio() {
    view.showMessage("Name your portfolio: ");
    String name = view.getUserInput();
    Portfolio portfolio = new BasicPortfolio(name);
    userData.addPortfolio(portfolio);
    lineSeparator();
    view.showMessage(name + " portfolio created.\n");
    state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
  }

  private void loadPortfolio() {
    view.showMessage("Filename (no .csv): ");
    String fileName = view.getUserInput();
    try {
      lineSeparator();
      Command<String> command = new LoadPortfolioCommand(fileName);
      view.showMessage(userData.execute(command) + "\n");
      state = ControllerState.SPECIFIC_PORTFOLIO_MENU;
    } catch (IllegalArgumentException e) {
      view.showMessage(e.getMessage() + " Please try again.\n");
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
        view.showMessage("There is no portfolio with that number.\n");
      }
    } catch (NumberFormatException e) {
      lineSeparator();
      view.showMessage("Invalid input. Please try again.\n");
    }
  }

  private void helpSpecificPortfolioMenu(String userInput) {
    switch (userInput) {
      case "1":
        viewStocks();
        break;
      case "2":
        portfolioValue();
        break;
      case "3":
        buyStocks();
        break;
      case "4":
        sellStocks();
        break;
      case "5":
        rebalancePortfolio();
        break;
      case "6":
        visualizePerformance();
        break;
      case "7":
        deletePortfolio();
        break;
      case "8":
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
        view.showMessage("Invalid input. Please try again.\n");
    }
  }

  private void viewStocks() {
    String date = formatDate(setDate());
    lineSeparator();
    view.showMessage("Stocks in " + userData.getCurrentPortfolio().getName() + " on " + date + ":\n");
    for (String stock : userData.getCurrentPortfolio().getComposition(date)) {
      view.showMessage(stock + "\n");
    }
  }

  private void portfolioValue() {
    String date = formatDate(setDate());
    List<String> distribution = userData.getCurrentPortfolio().getDistribution(date);
    lineSeparator();
    view.showMessage("Portfolio Distribution:\n");
    for (String str : distribution) {
      view.showMessage(str + "\n");
    }
    Command<Double> command = new PortfolioGetValueCommand(date);
    try {
      double value = userData.execute(command);
      lineSeparator();
      view.showMessage("Portfolio value: $" + formatDouble(value) + "\n");
    } catch (IllegalArgumentException e) {
      view.showMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void visualizePerformance() {
    String start = setStartDate();
    String end = setEndDate();
    lineSeparator();
    Command<String> command = new PortfolioPerformanceCommand(start, end);
    try {
      String graph = userData.execute(command);
      view.showMessage(graph + "\n");
    } catch (IllegalArgumentException e) {
      view.showMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void buyStocks() {
    String addedTicker = "";
    int addedShareCount = 0;
    boolean validTickerToAdd = false;
    while (!validTickerToAdd) {
      view.showMessage("Stock Ticker (to be added): ");
      addedTicker = view.getUserInput();
      boolean validShareCountToAdd = false;
      while (!validShareCountToAdd) {
        try {
          view.showMessage("Number of shares to add: ");
          addedShareCount = Integer.parseInt(view.getUserInput());
          String date = formatDate(setDate());
          try {
            userData.getCurrentPortfolio().buyStock(addedTicker, addedShareCount, date);
            lineSeparator();
            view.showMessage("Added " + addedShareCount + " " + addedTicker.toUpperCase()
                    + " to " + userData.getCurrentPortfolio().getName() + "\n");
          } catch (Exception e) {
            lineSeparator();
            view.showMessage(e.getMessage() + " Please try again.\n");
          }
          validShareCountToAdd = true;
          validTickerToAdd = true;
        } catch (NumberFormatException e) {
          lineSeparator();
          view.showMessage("Invalid stock share amount. Please try again.\n");
        } catch (IllegalArgumentException e) {
          lineSeparator();
          view.showMessage("Invalid ticker or stock share amount. Please try again.\n");
          break;
        }
      }
    }
  }

  private void sellStocks() {
    String removedTicker = "";
    int removedShareCount = 0;
    boolean validTickerToRemove = false;
    while (!validTickerToRemove) {
      view.showMessage("Stock Ticker (to be removed): ");
      removedTicker = view.getUserInput();
      boolean validShareCountToRemove = false;
      while (!validShareCountToRemove) {
        try {
          view.showMessage("Number of shares to remove: ");
          removedShareCount = Integer.parseInt(view.getUserInput());
          String date = formatDate(setDate());
          try {
            userData.getCurrentPortfolio().sellStock(removedTicker, removedShareCount, date);
            lineSeparator();
            view.showMessage("Removed " + removedShareCount + " " + removedTicker.toUpperCase()
                    + " from " + userData.getCurrentPortfolio().getName() + "\n");
          } catch (Exception e) {
            lineSeparator();
            view.showMessage(e.getMessage() + " Please try again.\n");
          }
          validShareCountToRemove = true;
          validTickerToRemove = true;
        } catch (NumberFormatException e) {
          lineSeparator();
          view.showMessage("Stock share amount must be a whole number. Please try again.\n");
        } catch (IllegalArgumentException e) {
          lineSeparator();
          view.showMessage(e.getMessage() + " Please try again.\n");
          break;
        }
      }
    }
  }

  private void rebalancePortfolio() {
    String date = formatDate(setDate());
    List<Stock> currentStocks = userData.getCurrentPortfolio().getStocks(date);
    int[] weights = new int[currentStocks.size()];
    for (int i = 0; i < currentStocks.size(); i++) {
      lineSeparator();
      view.showMessage("Weight for " + currentStocks.get(i).getTicker() + " (0%-100%): ");
      String weight = view.getUserInput();
      while (!isValidWeight(weight)) {
        lineSeparator();
        view.showMessage("Invalid weight. Please try again.\n");
        lineSeparator();
        view.showMessage("Weight for " + currentStocks.get(i).getTicker() + " (): ");
        weight = view.getUserInput();
      }
      weights[i] = Integer.parseInt(weight);
    }
    try {
      Command<String> command = new PortfolioRebalanceCommand(date, weights);
      userData.execute(command);
      lineSeparator();
      view.showMessage("Stocks for " + userData.getCurrentPortfolio().getName() + " on "
              + date + " rebalanced.\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      view.showMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void deletePortfolio() {
    userData.removePortfolio(userData.getCurrentPortfolio());
    lineSeparator();
    view.showMessage("Portfolio " + userData.getCurrentPortfolio().getName() + " deleted.\n");
    state = ControllerState.PORTFOLIO_MENU;
  }

  private void savePortfolio() {
    lineSeparator();
    view.showMessage(userData.getCurrentPortfolio().save() + "\n");
  }

  private void helpStockMenu(String userInput) {
    switch (userInput) {
      case "1":
        lastClosingPrice();
        break;
      case "2":
        closingPrice();
        break;
      case "3":
        netGain();
        break;
      case "4":
        xDayMovingAverage();
        break;
      case "5":
        xDayCrossovers();
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
        view.showMessage("Invalid input. Please try again.\n");
    }
  }

  private void lastClosingPrice() {
    lineSeparator();
    view.showMessage("Last Closing Price: $"
            + userData.getCurrentStock().getAllClosingPrices()
            .get(userData.getCurrentStock().getAllClosingPrices().size() - 1) + "\n");
  }

  private void closingPrice() {
    String date = formatDate(setDate());
    try {
      lineSeparator();
      view.showMessage("Closing Price for " + formatDate(date) + ": $"
              + userData.getCurrentStock().getClosingPrice(date) + "\n");
    } catch (IllegalArgumentException e) {
      view.showMessage(e.getMessage() + " Please try again.\n");
      lineSeparator();
    }
  }

  private void netGain() {
    String start = setStartDate();
    String end = setEndDate();
    try {
      Command<Double> command = new StockNetGainCommand(start, end);
      double netGain = userData.execute(command);
      lineSeparator();
      view.showMessage("Net Gain from " + start + " to " + end + ": $" + formatDouble(netGain) + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      view.showMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayMovingAverage() {
    String date = formatDate(setDate());
    String xDays = setXDays();
    try {
      Command<Double> command = new StockMovingAverageCommand(date, Integer.parseInt(xDays));
      double movingAverage = userData.execute(command);
      lineSeparator();
      view.showMessage("X-Day Moving Average on " + formatDate(date) + " for " + xDays + " days: $"
              + movingAverage + "\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      view.showMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private void xDayCrossovers() {
    String start = setStartDate();
    String end = setEndDate();
    String xDays = setXDays();
    try {
      Command<List<String>> command
              = new StockCrossoverCommand(start, end, Integer.parseInt(xDays));
      List<String> crossovers = userData.execute(command);
      lineSeparator();
      for (int i = 0; i < crossovers.size(); i++) {
        if (i % 5 == 0) {
          if (i != 0) {
            view.showMessage("\n");
          }
          view.showMessage(crossovers.get(i));
        } else {
          view.showMessage(", " + crossovers.get(i));
        }
      }
      view.showMessage("\n");
    } catch (IllegalArgumentException e) {
      lineSeparator();
      view.showMessage(e.getMessage() + " Please try again.\n");
    }
  }

  private String setDate() {
    StringBuilder date = new StringBuilder();
    String year = "";
    String month = "";
    String day = "";
    boolean validYear = false;
    boolean validMonth = false;
    boolean validDay = false;
    while (!validYear || !validMonth || !validDay) {
      if (!validYear) {
        lineSeparator();
        view.showMessage("Year: ");
        year = view.getUserInput();
        if (isValidYear(year)) {
          validYear = true;
        } else {
          view.showMessage("Invalid year. Please try again.\n");
        }
      }
      if (!validMonth) {
        lineSeparator();
        view.showMessage("Month (number): ");
        month = view.getUserInput();
        if (isValidMonth(month)) {
          validMonth = true;
        } else {
          view.showMessage("Invalid month. Please try again.\n");
        }
      }
      if (!validDay) {
        lineSeparator();
        view.showMessage("Day: ");
        day = view.getUserInput();
        if (isValidDay(day)) {
          validDay = true;
        } else {
          view.showMessage("Invalid day. Please try again.\n");
        }
      }
    }
    date.append(year).append('-')
            .append(String.format("%02d", Integer.parseInt(month))).append('-')
            .append(String.format("%02d", Integer.parseInt(day)));

    if (!isValidDate(date.toString())) {
      lineSeparator();
      view.showMessage("Invalid date. Please try again.\n");
      return setDate();
    }
    return date.toString();
  }

  private String setStartDate() {
    lineSeparator();
    view.showMessage("Start Date: \n");
    return setDate();
  }

  private String setEndDate() {
    lineSeparator();
    view.showMessage("End Date: \n");
    return setDate();
  }

  private String setXDays() {
    String xDays = "";
    boolean validXDays = false;
    while (!validXDays) {
      view.showMessage("X-Days: ");
      xDays = view.getUserInput();
      try {
        int xDaysNum = Integer.parseInt(xDays);
        if (xDaysNum <= 0) {
          throw new NumberFormatException();
        }
        validXDays = true;
      } catch (NumberFormatException e) {
        lineSeparator();
        view.showMessage("X-Days must be a positive integer. Please try again.\n");
      }
    }
    return xDays;
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
    view.showMessage("1: View Portfolios\n");
    view.showMessage("2: View Stocks\n");
    quitPrompt();
  }

  private void printPortfolioMenu() {
    lineSeparator();
    view.showMessage("1: Create Portfolio\n");
    view.showMessage("2: Load Portfolio (from a CSV file)\n");
    int portfolioIndex = 3;
    for (int i = 0; i < userData.getNumPortfolios(); i++) {
      view.showMessage(portfolioIndex++ + ": " + userData.getPortfolio(i).getName() + "\n");
    }
    returnPrompt();
    quitPrompt();
  }

  private void printSpecificPortfolioMenu() {
    lineSeparator();
    view.showMessage(userData.getCurrentPortfolio().getName() + "\n");
    view.showMessage("1: View Stocks\n");
    view.showMessage("2: Portfolio Value\n");
    view.showMessage("3: Buy Stock(s)\n");
    view.showMessage("4: Sell Stock(s)\n");
    view.showMessage("5: Rebalance Portfolio\n");
    view.showMessage("6: Visualize Performance\n");
    view.showMessage("7: Delete Portfolio\n");
    view.showMessage("8: Save Portfolio (to a CSV file)\n");
    returnPrompt();
    quitPrompt();
  }

  private void printStockMenu() {
    lineSeparator();
    try {
      String ticker = userData.getCurrentStock().getTicker();
      view.showMessage(ticker + "\n");
    } catch (IllegalArgumentException e) {
      view.showMessage("You are not currently viewing a stock. Please try again.\n");
      state = ControllerState.START_MENU;
      printStartMenu();
      return;
    }
    view.showMessage("1: Last Closing Price\n");
    view.showMessage("2: Closing Price\n");
    view.showMessage("3: Net Gain\n");
    view.showMessage("4: X-Day Moving Average\n");
    view.showMessage("5: X-Day Crossovers\n");
    returnPrompt();
    quitPrompt();
  }

  private void welcomeMessage() {
    lineSeparator();
    view.showMessage("Welcome to the virtual stocks program!\n" +
            "In menus that are numbered, input your number option.\n" +
            "Otherwise, type string input if prompted.\n");
  }

  private void farewellMessage() {
    lineSeparator();
    view.showMessage("Thanks for using our virtual stocks program!\n");
    lineSeparator();
  }

  private void returnPrompt() {
    view.showMessage("(r or return to go back)\n");
  }

  private void quitPrompt() {
    view.showMessage("(q or quit to quit)\n");
  }

  private boolean isValidDate(String date) {
    try {
      LocalDate valid = LocalDate.parse(date);
      return true;
    } catch (Exception e) {
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
    view.showMessage("-------------------------------------------------\n");
  }

  private String formatDouble(double num) {
    return String.format("%.2f", num);
  }
}