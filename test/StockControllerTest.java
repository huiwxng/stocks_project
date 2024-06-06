import org.junit.Before;

import controller.StockController;
import model.user.BasicUserData;
import model.user.UserData;

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
  }


}
