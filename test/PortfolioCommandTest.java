import org.junit.Before;
import org.junit.Test;

import model.portfolio.BasicPortfolio;
import model.portfolio.GetValueCommand;
import model.portfolio.Portfolio;
import model.portfolio.PortfolioCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Test class to test the implementations of the {@link PortfolioCommand} interface.
 */
public class PortfolioCommandTest {
  Portfolio p;
  Portfolio p1;
  Portfolio p2;

  @Before
  public void setUp() {
    p = new BasicPortfolio("empty portfolio");

    p1 = new BasicPortfolio("portfolio 1");
    p1.addStock("AAPL", 10);

    p2 = new BasicPortfolio("portfolio 2");
    p2.addStock("AAPL", 10);
    p2.addStock("GOOG", 10);
  }

  @Test
  public void testGetValueCommand() {

    PortfolioCommand<Double> getValue;

    getValue = new GetValueCommand("2024-06-04");
    assertEquals(1943.50, getValue.execute(p1), 0.01);

    getValue = new GetValueCommand("2024-06-04");
    assertEquals(3694.80, getValue.execute(p2), 0.01);

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when the portfolio is empty
      PortfolioCommand<Double> testThrow = new GetValueCommand("2024-06-04");
      testThrow.execute(p);
    });
  }
}