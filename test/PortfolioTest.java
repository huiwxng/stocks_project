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
    p1.buyStock("AAPL", 10.0, "2024-06-04");

    p2 = new BasicPortfolio("portfolio 2");
    p2.buyStock("AAPL", 10.0, "2024-06-04");
    p2.buyStock("GOOG", 10.0, "2024-06-04");
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
    List<Double> expectedShares = new ArrayList<>();

    assertEquals(expectedStocks, p.getStocks("2024-06-04"));
    assertEquals(expectedShares, p.getShares("2024-06-04"));

    p.buyStock("GOOG", 10.0, "2024-06-04");
    expectedStocks.add(new BasicStock("GOOG"));
    expectedShares.add(10.0);
    for (int i = 0; i < p.getStocks("2024-06-04").size(); i++) {
      assertEquals(expectedStocks.get(i).getTicker(), p.getStocks("2024-06-04").get(i).getTicker());
      assertEquals(expectedShares.get(i), p.getShares("2024-06-04").get(i));
    }

    p.buyStock("AAPL", 20.0, "2024-06-04");
    expectedStocks.add(new BasicStock("AAPL"));
    expectedShares.add(20.0);
    for (int i = 0; i < p.getStocks("2024-06-04").size(); i++) {
      assertEquals(expectedStocks.get(i).getTicker(), p.getStocks("2024-06-04").get(i).getTicker());
      assertEquals(expectedShares.get(i), p.getShares("2024-06-04").get(i));
    }
  }

  @Test
  public void testGetComposition() {
    // test getComposition with empty portfolio
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getComposition("2024-06-04"));

    // test getComposition with portfolio with one stock
    expected = new ArrayList<>();
    expected.add("AAPL: 10 share(s)");
    assertEquals(expected, p1.getComposition("2024-06-04"));

    // test getComposition with portfolio with two stocks
    expected = new ArrayList<>();
    expected.add("AAPL: 10 share(s)");
    expected.add("GOOG: 10 share(s)");
    assertEquals(expected, p2.getComposition("2024-06-04"));
  }

  @Test
  public void testGetDistribution() {
    // test getComposition with empty portfolio
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getDistribution("2024-06-04"));

    // test getComposition with portfolio with one stock
    expected = new ArrayList<>();
    expected.add("AAPL: $1943.50");
    assertEquals(expected, p1.getDistribution("2024-06-04"));

    // test getComposition with portfolio with two stocks
    expected = new ArrayList<>();
    expected.add("AAPL: $1943.50");
    expected.add("GOOG: $1751.30");
    assertEquals(expected, p2.getDistribution("2024-06-04"));
  }

  @Test
  public void testBuyStock() {
    // tests for adding to empty portfolio
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getComposition("2024-06-04"));
    p.buyStock("AAPL", 10, "2024-06-04");
    expected.add("AAPL: 10 share(s)");
    assertEquals(expected, p.getComposition("2024-06-04"));

    // tests for adding to portfolio with one stock
    expected = new ArrayList<>();
    p1.buyStock("AAPL", 10, "2024-06-04");
    expected.add("AAPL: 20 share(s)");
    assertEquals(expected, p1.getComposition("2024-06-04"));

    p1.buyStock("GOOG", 10, "2024-06-04");
    expected.add("GOOG: 10 share(s)");
    assertEquals(expected, p1.getComposition("2024-06-04"));

    // tests for adding to portfolio with two stocks
    expected = new ArrayList<>();
    p2.buyStock("AAPL", 10, "2024-06-04");
    expected.add("AAPL: 20 share(s)");
    expected.add("GOOG: 10 share(s)");
    assertEquals(expected, p2.getComposition("2024-06-04"));

    p2.buyStock("AMZN", 10, "2024-06-04");
    expected.add("AMZN: 10 share(s)");
    assertEquals(expected, p2.getComposition("2024-06-04"));

    // test for date before the add
    expected = new ArrayList<>();
    assertEquals(expected, p2.getComposition("2024-06-03"));

    // invalid date
    assertThrows(IllegalArgumentException.class, () -> {
      p.buyStock("AAPL", 10, "bleh");
    });

    // future date
    assertThrows(IllegalArgumentException.class, () -> {
      p.buyStock("AAPL", 10, "3000-10-10");
    });

  }

  @Test
  public void testSellStock() {
    List<String> expected = new ArrayList<>();
    assertEquals(expected, p.getComposition("2024-06-04"));

    // tests for error when removing from empty portfolio
    assertThrows(IllegalArgumentException.class, () -> {
      p.sellStock("AAPL", 10, "2024-06-04");
    });

    // tests for removing from portfolio with one stock
    expected = new ArrayList<>();
    p1.sellStock("AAPL", 5, "2024-06-04");
    expected.add("AAPL: 5 share(s)");
    assertEquals(expected, p1.getComposition("2024-06-04"));
    p1.sellStock("AAPL", 5, "2024-06-04");
    expected = new ArrayList<>();
    assertEquals(expected, p1.getComposition("2024-06-04"));

    // tests for removing from portfolio with two stocks
    expected = new ArrayList<>();
    p2.sellStock("AAPL", 5, "2024-06-04");
    expected.add("AAPL: 5 share(s)");
    expected.add("GOOG: 10 share(s)");
    assertEquals(expected, p2.getComposition("2024-06-04"));
    p2.sellStock("AAPL", 10.0, "2024-06-04");
    expected.remove(0);
    assertEquals(expected, p2.getComposition("2024-06-04"));

    // tests for removing from portfolio with not enough shares of a stock
    assertThrows(IllegalArgumentException.class, () -> {
      p1.sellStock("AAPL", 10, "2024-06-04");
    });

    // tests that removing a stock on a later day doesn't affect earlier days
    p1.buyStock("AAPL", 10, "2024-06-04");
    expected = new ArrayList<>();
    expected.add("AAPL: 10 share(s)");
    p1.sellStock("AAPL", 5, "2024-06-05");
    List<String> laterExpected = new ArrayList<>();
    laterExpected.add("AAPL: 5 share(s)");
    assertEquals(expected, p1.getComposition("2024-06-04"));
    assertEquals(laterExpected, p1.getComposition("2024-06-05"));

    // tests that removing a stock on an earlier day affects later days
    p1.sellStock("AAPL", 2, "2024-06-04");
    expected = new ArrayList<>();
    expected.add("AAPL: 3 share(s)");
    assertEquals(expected, p1.getComposition("2024-06-05"));

    // test for selling stock that the portfolio does not holds
    assertThrows(IllegalArgumentException.class, () -> {
      p2.sellStock("AMZN", 10, "2024-06-04");
    });

    // test for selling more shares than the portfolio holds
    assertEquals(expected, p1.getComposition("2024-06-05"));
    p1.sellStock("AAPL", 10, "2024-06-05");
    expected = new ArrayList<>();
    assertEquals(expected, p1.getComposition("2024-06-05"));

    // invalid date
    assertThrows(IllegalArgumentException.class, () -> {
      p.sellStock("AAPL", 10, "bleh");
    });

    // future date
    assertThrows(IllegalArgumentException.class, () -> {
      p1.sellStock("AAPL", 10, "3000-10-10");
    });
  }

  @Test
  public void testIsEmpty() {
    assertTrue(p.isEmpty("2024-06-04"));
    p.buyStock("AAPL", 10.0, "2024-06-04");
    assertFalse(p.isEmpty("2024-06-04"));
  }

  @Test
  public void testSave() {
    // test saving empty portfolio
    String expected = "Portfolio [empty portfolio] successfully saved to " +
            "res/portfolios/empty portfolio.csv";
    assertEquals(expected, p.save());
    expected = "Portfolio [empty portfolio] overwritten successfully and saved to " +
            "res/portfolios/empty portfolio.csv";
    assertEquals(expected, p.save());

    // test save portfolio with one interaction
    expected = "Portfolio [portfolio 1] successfully saved to " +
            "res/portfolios/portfolio 1.csv";
    assertEquals(expected, p1.save());
    expected = "Portfolio [portfolio 1] overwritten successfully and saved to " +
            "res/portfolios/portfolio 1.csv";
    assertEquals(expected, p1.save());

    // test
    expected = "Portfolio [portfolio 2] successfully saved to " +
            "res/portfolios/portfolio 2.csv";
    assertEquals(expected, p2.save());
    expected = "Portfolio [portfolio 2] overwritten successfully and saved to " +
            "res/portfolios/portfolio 2.csv";
    assertEquals(expected, p2.save());
  }
}