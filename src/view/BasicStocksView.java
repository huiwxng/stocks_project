package view;

public class BasicStocksView implements StocksView {
  private final StringBuilder string;

  protected BasicStocksView(String string) {
    this.string = new StringBuilder(string);
  }

  @Override
  public void write(String str) {
    string.append(str);
  }
}
