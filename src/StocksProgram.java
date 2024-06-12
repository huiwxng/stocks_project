import java.io.InputStreamReader;

import controller.StockController;
import model.user.BasicUserData;
import model.user.UserData;

/**
 * The driver of this application.
 */
public class StocksProgram {
  /**
   * Main method of the program.
   *
   * @param args any command lind arguments
   */
  public static void main(String[] args) throws InterruptedException {
    UserData userData = new BasicUserData();
    Readable in = new InputStreamReader(System.in);
    Appendable out = System.out;
    StockController controller = new StockController(userData, in, out);
    controller.control();
  }
}
