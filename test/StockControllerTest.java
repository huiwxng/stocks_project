import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import controller.StockController;
import model.portfolio.Portfolio;
import model.user.MockUserData;
import model.user.UserData;

import static org.junit.Assert.assertEquals;

/**
 * This class is a tester for the StockController.
 */
public class StockControllerTest {
  private UserData model;
  private Appendable out;

  @Before
  public void setup() {
    out = new StringBuilder();
  }

  @Test
  public void testStartMenuQuit() throws InterruptedException {
    mockUserInput("q\n");
    String output = out.toString();
    String expected = welcomePrompt() +  startMenu() + farewellPrompt();
    assertEquals(expected, output);
  }

  @Test
  public void testStartMenuInvalidInput() throws InterruptedException {
    mockUserInput("invalid\nq\n");
    String output = out.toString();
    String expected = welcomePrompt() + startMenu() + invalidInputPrompt() + startMenu()
            + farewellPrompt();
    assertEquals(expected, output);
  }

  @Test
  public void testStartMenu1() throws InterruptedException {
    mockUserInput("1\nq\n");
    String output = out.toString();
    String expected = welcomePrompt() + startMenu() + portfolioMenu() + farewellPrompt();
    assertEquals(expected, output);
  }

  @Test
  public void testStartMenu2() throws InterruptedException {
    mockUserInput("2\nAMZN\nq\n");
    String output = out.toString();
    String expected = welcomePrompt() + startMenu() + viewStocksPrompt() + stockMenu()
            + farewellPrompt();
  }

  private void mockUserInput(String userInput) throws InterruptedException {
    Readable in = new StringReader(userInput);
    StringBuilder log = new StringBuilder();
    model = new MockUserData(log);
    StockController controller = new StockController(model, in, out);
    controller.control();
  }

  private String startMenu() {
    return lineSeparator() + "1: View Portfolios\n" + "2: View Stocks\n"
            + quitPrompt() + selectMenuOptionPrompt();
  }

  private String portfolioMenu() {
    StringBuilder portfolioMenu = new StringBuilder(lineSeparator() + "1: Create Portfolio\n");
    int portfolioIndex = 2;
    for (Portfolio portfolio : model.listPortfolios()) {
      portfolioMenu.append(portfolioIndex++).append(": ").append(portfolio.getName()).append("\n");
    }
    portfolioMenu.append(returnPrompt()).append(quitPrompt()).append(selectMenuOptionPrompt());
    return portfolioMenu.toString();
  }

  private String stockMenu() {
    StringBuilder stockMenu = new StringBuilder(lineSeparator());
    stockMenu.append("1: Last Closing Price\n").append("2: Closing Price\n")
            .append("3: Net Gain\n").append("4: X-Day Moving Average\n")
            .append("5: X-Day Crossovers\n");
    return stockMenu.toString();
  }

  private String specificPortfolioMenu() {
    StringBuilder specificPortfolioMenu = new StringBuilder(lineSeparator());
    specificPortfolioMenu.append(model.)
  }

  private String welcomePrompt() {
    return lineSeparator() + "Welcome to the virtual stocks program!\n"
            + "In menus that are numbered, input your number option.\n"
            + "Otherwise, type string input if prompted.\n";
  }

  private String farewellPrompt() {
    return lineSeparator() + "Thanks for using our virtual stocks program!\n";
  }

  private String returnPrompt() {
    return "(r or return to go back)\n";
  }

  private String quitPrompt() {
    return "(q or quit to quit)\n";
  }

  private String viewStocksPrompt() {
    return "Stock Ticker (to be viewed): ";
  }

  private String selectMenuOptionPrompt() {
    return "Select menu option: ";
  }

  private String invalidInputPrompt() {
    return lineSeparator() + "Invalid input. Please try again.\n";
  }

  private String lineSeparator() {
    return "-------------------------------------------------\n";
  }
}
