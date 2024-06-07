import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.stock.BasicStock;
import model.stock.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


/**
 * Test class to test the implementations of the {@link Portfolio} interface.
 */
public class PortfolioTest {
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
  public void testGetName() {
    assertEquals("empty portfolio", p.getName());
    assertEquals("portfolio 1", p1.getName());
    assertEquals("portfolio 2", p2.getName());
  }

  @Test
  public void testGetStocksAndShares() {
    List<Stock> expectedStocks = new ArrayList<>();
    List<Integer> expectedShares = new ArrayList<>();

    assertEquals(expectedStocks, p.getStocks());
    assertEquals(expectedShares, p.getShares());

    p.addStock("GOOG", 10);
    expectedStocks.add(new BasicStock("GOOG"));
    expectedShares.add(10);
    for (int i = 0; i < p.getStocks().size(); i++) {
      assertEquals(expectedStocks.get(i).getTicker(), p.getStocks().get(i).getTicker());
      assertEquals(expectedShares.get(i), p.getShares().get(i));
    }

    p.addStock("AAPL", 20);
    expectedStocks.add(new BasicStock("AAPL"));
    expectedShares.add(20);
    for (int i = 0; i < p.getStocks().size(); i++) {
      assertEquals(expectedStocks.get(i).getTicker(), p.getStocks().get(i).getTicker());
      assertEquals(expectedShares.get(i), p.getShares().get(i));
    }
  }

  @Test
  public void testGetStocksWithAmt() {
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
  public void testAddStock() {
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
  public void testRemoveStock() {
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
  public void testIsEmpty() {
    assertTrue(p.isEmpty());
    p.addStock("AAPL", 10);
    assertFalse(p.isEmpty());
  }
}