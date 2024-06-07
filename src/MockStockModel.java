import java.util.List;

import model.portfolio.Portfolio;
import model.portfolio.PortfolioCommand;
import model.stock.Stock;
import model.stock.StockCommand;
import model.user.UserData;

public class MockStockModel implements Portfolio, PortfolioCommand, Stock, StockCommand, UserData {
  private StringBuilder log;

  @Override
  public <T> void executeReturn(PortfolioCommand<T> cmd) {

  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public List<Stock> getStocks() {
    return List.of();
  }

  @Override
  public List<Integer> getShares() {
    return List.of();
  }

  @Override
  public List<String> getStocksWithAmt() {
    return List.of();
  }

  @Override
  public void addStock(String ticker, int shareAmt) {

  }

  @Override
  public int removeStock(String ticker, int shareAmt) {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Object execute(Portfolio portfolio) {
    return null;
  }

  @Override
  public String getTicker() {
    return "";
  }

  @Override
  public List<String> getAllClosingPricesWithDates() {
    return List.of();
  }

  @Override
  public List<Double> getAllClosingPrices() {
    return List.of();
  }

  @Override
  public List<String> getAllDates() {
    return List.of();
  }

  @Override
  public double getClosingPrice(String date) {
    return 0;
  }

  @Override
  public int getIndex(String date) {
    return 0;
  }

  @Override
  public <T> void executeReturn(StockCommand<T> cmd) {

  }

  @Override
  public Object execute(Stock stock) {
    return null;
  }

  @Override
  public void addPortfolio(Portfolio portfolio) {

  }

  @Override
  public void removePortfolio(Portfolio portfolio) {

  }

  @Override
  public List<Portfolio> listPortfolios() {
    return List.of();
  }

  @Override
  public Portfolio getCurrentPortfolio() {
    return null;
  }

  @Override
  public void setCurrentPortfolio(String name) {

  }

  @Override
  public Stock viewStock(String ticker) {
    return null;
  }
}
