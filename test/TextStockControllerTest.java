import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import controller.IController;
import controller.Interaction;
import controller.TextStockController;
import model.portfolio.Portfolio;
import model.user.MockUserData;
import model.user.UserData;
import view.TextView;

import static controller.Interaction.inputs;
import static controller.Interaction.prints;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * This class is for testing the TextStockController.
 */
public class TextStockControllerTest {
  private MockUserData model;

  @Before
  public void setup() {
    model = new MockUserData(new StringBuilder());
  }

  @Test
  public void testStartMenu() throws InterruptedException {
    run(model, prints(welcomeMessage()), prints(startMenu())
            // test invalid input for start menu
            , inputs("invalid input"), prints(invalidInputMessage())
            , prints(startMenu())
            // test quit for start menu
            , inputs("q"), prints(farewellMessage()));
    assertNotEquals(" ", model.getLog());
  }

  @Test
  public void testViewPortfolios() throws InterruptedException {
    run(model, prints(welcomeMessage()), prints(startMenu())
            , inputs("1"), prints(portfolioMenu())
            // test return for portfolio menu
            , inputs("r"), prints(startMenu())
            , inputs("1"), prints(portfolioMenu())
            // test invalid input for portfolio menu
            , inputs("invalid input"), prints(invalidInputMessage())
            , prints(portfolioMenu())
            , inputs("q"), prints(farewellMessage()));
    assertNotEquals(" ", model.getLog());
  }

  @Test
  public void testViewStocks() throws InterruptedException {
    run(model, prints(welcomeMessage()), prints(startMenu())
            , inputs("2"), prints(viewStocksPrompt())
            // test invalid ticker
            , inputs("invalid input"), prints(invalidTickerMessage("invalid input"))
            , prints(startMenu()), inputs("2"), prints(viewStocksPrompt())
            // test valid ticker
            , inputs("AMZN"), prints(stockMenu("AMZN"))
            , inputs("q"), prints(farewellMessage()));
    assertNotEquals("", model.getLog());
  }

  private void run(UserData model, Interaction... interactions)
          throws InterruptedException {
    StringBuilder userInput = new StringBuilder();
    StringBuilder expected = new StringBuilder();
    for (Interaction interaction : interactions) {
      interaction.apply(userInput, expected);
    }
    StringReader in = new StringReader(userInput.toString());
    StringBuilder out = new StringBuilder();
    IController controller = new TextStockController(model, new TextView(in, out));
    controller.control();
    assertEquals(expected.toString(), out.toString());
  }

  private String startMenu() {
    return lineSeparator() + "1: View Portfolios\n" + "2: View Stocks\n"
            + quitMessage() + selectMenuOptionPrompt();
  }

  private String portfolioMenu() {
    StringBuilder portfolioMenu = new StringBuilder(lineSeparator() + "1: Create Portfolio\n");
    portfolioMenu.append("2: Load Portfolio (from a CSV file)\n");
    int portfolioIndex = 3;
    for (Portfolio portfolio : model.listPortfolios()) {
      portfolioMenu.append(portfolioIndex++).append(": ").append(portfolio.getName()).append("\n");
    }
    portfolioMenu.append(returnMessage()).append(quitMessage()).append(selectMenuOptionPrompt());
    return portfolioMenu.toString();
  }

  private String specificPortfolioMenu(String portfolioName) {
    return lineSeparator() +
            portfolioName + "\n" +
            "1: View Stocks\n" +
            "2: Portfolio Value\n" +
            "3: Buy Stock(s)\n" +
            "4: Sell Stock(s)\n" +
            "5: Delete Portfolio\n" +
            returnMessage() +
            quitMessage() +
            selectMenuOptionPrompt();
  }

  private String stockMenu(String ticker) {
    return lineSeparator()
            + ticker + "\n"
            + "1: Last Closing Price\n" + "2: Closing Price\n"
            + "3: Net Gain\n" + "4: X-Day Moving Average\n"
            + "5: X-Day Crossovers\n" + returnMessage()
            + quitMessage() + selectMenuOptionPrompt();
  }

  private String welcomeMessage() {
    return lineSeparator() + "Welcome to the virtual stocks program!\n"
            + "In menus that are numbered, input your number option.\n"
            + "Otherwise, type string input if prompted.\n";
  }

  private String farewellMessage() {
    return lineSeparator() + "Thanks for using our virtual stocks program!\n" + lineSeparator();
  }

  private String returnMessage() {
    return "(r or return to go back)\n";
  }

  private String quitMessage() {
    return "(q or quit to quit)\n";
  }

  private String selectMenuOptionPrompt() {
    return "Select menu option (number): ";
  }

  private String viewStocksPrompt() {
    return "Stock Ticker (to be viewed): ";
  }

  private String namePortfolioPrompt() {
    return "Name your portfolio: ";
  }

  private String viewStocksInPortfolioMessage(String currentPortfolio, String date) {
    StringBuilder stocksList = new StringBuilder(lineSeparator()
            + "Stocks in current portfolio:\n");
    for (String stock : model.getCurrentPortfolio().getComposition(date)) {
      stocksList.append(stock).append("\n");
    }
    return stocksList.toString();
  }

  private String createdPortfolioMessage(String portfolioName) {
    return lineSeparator() + portfolioName + " portfolio created.\n";
  }

  private String invalidInputMessage() {
    return lineSeparator() + "Invalid input. Please try again.\n";
  }

  private String invalidTickerMessage(String ticker) {
    return lineSeparator() + "The ticker '" + ticker
            + "' is not available on Alpha Vantage API or you have ran out of API requests.\n"
            + lineSeparator() + "You are not currently viewing a stock. Please try again.\n";
  }

  private String lineSeparator() {
    return "-------------------------------------------------\n";
  }
}