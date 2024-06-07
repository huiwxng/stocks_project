import org.junit.Before;
import org.junit.Test;

import model.stock.BasicStock;
import model.stock.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Test class to test the implementations of the {@link Stock} interface.
 */
public class StockTest {

  Stock Apple;
  Stock Google;
  Stock Nvidia;
  Stock Amazon;
  Stock Tesla;
  Stock Meta;
  Stock Microsoft;
  Stock JPMorgan;
  Stock HomeDepot;
  Stock TSM;
  Stock Walmart;

  @Before
  public void setUp() throws Exception {
    try {
      Apple = new BasicStock("AAPL");
      Google = new BasicStock("GOOG");
      Nvidia = new BasicStock("NVDA");
      Amazon = new BasicStock("AMZN");
      Tesla = new BasicStock("TSLA");
      Meta = new BasicStock("META");
      Microsoft = new BasicStock("MSFT");
      JPMorgan = new BasicStock("JPM");
      HomeDepot = new BasicStock("HD");
      TSM = new BasicStock("TSM");
      Walmart = new BasicStock("WMT");
    } catch (Exception e) {
      System.err.println("Error creating stock: " + e.getMessage());
    }
  }

  @Test
  public void testThrows() {
    assertThrows(IllegalArgumentException.class, () -> {
      Stock stock = new BasicStock("asdfasfasdfasdf");
    });
  }

  @Test
  public void testGetTicker() {
    assertEquals("AAPL", Apple.getTicker());
    assertEquals("GOOG", Google.getTicker());
    assertEquals("NVDA", Nvidia.getTicker());
    assertEquals("AMZN", Amazon.getTicker());
    assertEquals("TSLA", Tesla.getTicker());
    assertEquals("META", Meta.getTicker());
    assertEquals("MSFT", Microsoft.getTicker());
    assertEquals("JPM", JPMorgan.getTicker());
    assertEquals("HD", HomeDepot.getTicker());
    assertEquals("TSM", TSM.getTicker());
    assertEquals("WMT", Walmart.getTicker());
  }

  @Test
  public void testGetClosingPrice() {
    assertEquals(21.23, Apple.getClosingPrice("2002-01-10"), 0.01);
    assertEquals(179.54, Google.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(947.8, Nvidia.getClosingPrice("2024-05-20"), 0.01);
    assertEquals(183.54, Amazon.getClosingPrice("2024-05-20"), 0.01);
    assertEquals(165.08, Tesla.getClosingPrice("2023-04-21"), 0.01);
    assertEquals(212.89, Meta.getClosingPrice("2023-04-21"), 0.01);
    assertEquals(166.72, Microsoft.getClosingPrice("2020-01-23"), 0.01);
    assertEquals(199.52, JPMorgan.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(336.15, HomeDepot.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(153.67, TSM.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(65.15, Walmart.getClosingPrice("2024-05-21"), 0.01);
  }

  @Test
  public void testInvalidDates() {
    // out of range of the csv data (older than the oldest date)
    assertThrows(IllegalArgumentException.class, () -> {
      Apple.getClosingPrice("1999-01-10");
    });

    // out of range of the csv data (in the future)
    assertThrows(IllegalArgumentException.class, () -> {
      Apple.getClosingPrice("2024-10-10");
    });
  }
}