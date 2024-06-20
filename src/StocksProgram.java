import java.io.InputStreamReader;

import controller.GraphicStockController;
import controller.IController;
import controller.TextStockController;
import model.user.BasicUserData;
import model.user.UserData;
import view.GraphicView;
import view.TextView;

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
    if (args.length == 0) {
      IController controller = new GraphicStockController(userData, new GraphicView());
      controller.control();
    } else if (args.length == 1 && args[0].equals("-text")) {
      IController controller = new TextStockController(
              userData, new TextView(new InputStreamReader(System.in), System.out));
      controller.control();
    } else {
      System.err.println("Invalid command-line arguments.");
      System.err.println("java -jar StocksProgram.jar for the graphical user interface");
      System.err.println("java -jar StocksProgram.jar -text for the text-based interface");
      System.exit(1);
    }
  }
}
