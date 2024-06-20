package view;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class is an implementation of IView that supports a text-based user interface.
 */
public class TextView implements IView {
  private final Readable in;
  private final Appendable out;
  private final Scanner scanner;

  /**
   * Constructs a text-based view for the controller to work with.
   * @param in the Readable object for inputs
   * @param out the Appendable object for outputs
   */
  public TextView(Readable in, Appendable out) {
    this.in = in;
    this.out = out;
    this.scanner = new Scanner(in);
  }

  @Override
  public void showMessage(String message) throws IllegalArgumentException {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  /**
   * Gets the user's input.
   * @return returns a String representing the user's input.
   */
  public String getUserInput() {
    return scanner.nextLine().trim();
  }
}
