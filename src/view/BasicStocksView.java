package view;

public class BasicStocksView implements StocksView {
  private final StringBuilder string;

  public BasicStocksView() {
    this.string = new StringBuilder();
  }

  @Override
  public void write(String str) {
    string.append(str);
  }
}
