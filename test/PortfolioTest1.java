import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.stock.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


/**
 * Test class to test the implementations of the {@link Portfolio} interface.
 */
public class PortfolioTest1 {

  // tests to write:
  // run getters at every step
  // test for an empty portfolio:
      // running removeStock should throw error

  // test for adding a stock to portfolio:
      // test for adding to existing stock
      // test for adding to non-existing stock

  // test for removing a stock:
      // test for removing existing stock:
          // with enough shares
          // with not enough shares:
              // amount more than the existing shares
              // amount the same as the existing shares
    // test for removing non-existing stock should throw error

  Portfolio p;
  Portfolio p1;
  Portfolio p2;

  @Before
  public void setUp() throws Exception {
    p = new BasicPortfolio("empty portfolio");

    p1 = new BasicPortfolio("portfolio 1");
    p1.addStock("AAPL", 10);

    p2 = new BasicPortfolio("portfolio 2");
    p2.addStock("AAPL", 10);
    p2.addStock("GOOG", 10);
  }

  @Test
  public void getName() {
    assertEquals("empty portfolio", p.getName());
    assertEquals("portfolio 1", p1.getName());
    assertEquals("portfolio 2", p2.getName());
  }

  @Test
  public void getStocks() {
    List<Stock> expected = new ArrayList<>();
    assertEquals(expected, p.getStocks());
  }

  @Test
  public void getStocksWithAmt() {
    // test getStocksWithAmt with empty portfolio
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getStocksWithAmt());

    // test getStocksWithAmt with portfolio with one stock
    expected = new ArrayList<>();
    expected.add("AAPL: 10");
    assertEquals(expected, p1.getStocksWithAmt());

    // test getStocksWithAmt with portfolio with two stocks
    expected = new ArrayList<>();
    expected.add("AAPL: 10");
    expected.add("GOOG: 10");
    assertEquals(expected, p2.getStocksWithAmt());
  }

  @Test
  public void addStock() {
    // tests for adding to empty portfolio
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getStocksWithAmt());
    p.addStock("AAPL", 10);
    expected.add("AAPL: 10");
    assertEquals(expected, p.getStocksWithAmt());

    // tests for adding to portfolio with one stock
    expected = new ArrayList<>();
    p1.addStock("AAPL", 10);
    expected.add("AAPL: 20");
    assertEquals(expected, p1.getStocksWithAmt());

    p1.addStock("GOOG", 10);
    expected.add("GOOG: 10");
    assertEquals(expected, p1.getStocksWithAmt());

    // tests for adding to portfolio with two stocks
    expected = new ArrayList<>();
    p2.addStock("AAPL", 10);
    expected.add("AAPL: 20");
    expected.add("GOOG: 10");
    assertEquals(expected, p2.getStocksWithAmt());

    p2.addStock("AMZN", 10);
    expected.add("AMZN: 10");
    assertEquals(expected, p2.getStocksWithAmt());
  }

  @Test
  public void removeStock() {
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getStocksWithAmt());

    // tests for error when removing from empty portfolio
    assertThrows(IllegalArgumentException.class, () -> {
      p.removeStock("AAPL", 10);
    });

    // tests for removing from portfolio with one stock
    expected = new ArrayList<>();
    p1.removeStock("AAPL", 5);
    expected.add("AAPL: 5");
    assertEquals(expected, p1.getStocksWithAmt());
    p1.removeStock("AAPL", 5);
    expected = new ArrayList<>();
    assertEquals(expected, p1.getStocksWithAmt());

    // tests for removing from portfolio with two stocks
    expected = new ArrayList<>();
    p2.removeStock("AAPL", 5);
    expected.add("AAPL: 5");
    expected.add("GOOG: 10");
    assertEquals(expected, p2.getStocksWithAmt());
    p2.removeStock("AAPL", 10);
    expected.remove(0);
    assertEquals(expected, p2.getStocksWithAmt());
  }

  @Test
  public void isEmpty() {
    assertTrue(p.isEmpty());
    p.addStock("AAPL", 10);
    assertFalse(p.isEmpty());
  }
}