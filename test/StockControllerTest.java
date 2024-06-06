import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import controller.StockController;
import model.user.MockUserData;
import model.user.UserData;

import static org.junit.Assert.assertEquals;

/**
 * This class is a tester for the StockController.
 */
public class StockControllerTest {
  private UserData model;
  private Readable in;
  private Appendable out;
  private StockController controller;

  @Before
  public void setup() {
    out = new StringBuilder();
    model = new MockUserData((StringBuilder) out);
  }

  @Test
  public void testStartMenu() throws InterruptedException {
    in = new StringReader("q\n");
    controller = new StockController(model, in, out);
    controller.control();
    String output = out.toString();
    String expected = "-------------------------------------------------\n"
                    + "Welcome to the virtual stocks program!\n"
            + "In menus that are numbered, input your number option.\n"
            + "Otherwise, type string input if prompted.\n"
            + "-------------------------------------------------\n"
            + "1: View Portfolios\n"
            + "2: View Stocks\n"
            + "(q or quit to quit)\n"
            + "Select menu option: ";
    assertEquals(expected, output);
  }
}
