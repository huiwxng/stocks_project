package controller;

/**
 * An interaction with the user consists of some input to send the program and
 * some output to expect. We represent it as an object that takes in two
 * StringBuilders and produces the intended effects on them.
 */
public interface Interaction {
  /**
   * This method applies the intended effects on the input and output StringBuilders given.
   *
   * @param in StringBuilder for the input.
   * @param out StringBuilder for the output.
   */
  void apply(StringBuilder in, StringBuilder out);

  /**
   * Constructs an Interaction object that appends the input to the
   * in StringBuilder.
   *
   * @param in input to be appended.
   * @return an Interaction object for the input.
   */
  static Interaction inputs(String in) {
    return (input, output) -> {
      input.append(in).append("\n");
    };
  }

  /**
   * Constructs an Interaction object that appends the output to the
   * out StringBuilder.
   *
   * @param lines lines to be appended.
   * @return an Interaction object for the output.
   */
  static Interaction prints(String... lines) {
    return (input, output) -> {
      for (String line : lines) {
        output.append(line);
      }
    };
  }
}
