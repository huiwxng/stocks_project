import controller.GraphicStockController;
import controller.IController;
import model.user.BasicUserData;
import model.user.UserData;
import view.GraphicView;

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
    IController controller = new GraphicStockController(userData, new GraphicView());
    controller.control();
  }
}
