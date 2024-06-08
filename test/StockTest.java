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

  Stock apple;
  Stock google;
  Stock nvidia;
  Stock amazon;
  Stock tesla;
  Stock meta;
  Stock microsoft;
  Stock jpmorgan;
  Stock homeDepot;
  Stock tsm;
  Stock walmart;

  @Before
  public void setUp() throws Exception {
    try {
      apple = new BasicStock("AAPL");
      google = new BasicStock("GOOG");
      nvidia = new BasicStock("NVDA");
      amazon = new BasicStock("AMZN");
      tesla = new BasicStock("TSLA");
      meta = new BasicStock("meta");
      microsoft = new BasicStock("MSFT");
      jpmorgan = new BasicStock("JPM");
      homeDepot = new BasicStock("HD");
      tsm = new BasicStock("tsm");
      walmart = new BasicStock("WMT");
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
    assertEquals("AAPL", apple.getTicker());
    assertEquals("GOOG", google.getTicker());
    assertEquals("NVDA", nvidia.getTicker());
    assertEquals("AMZN", amazon.getTicker());
    assertEquals("TSLA", tesla.getTicker());
    assertEquals("meta", meta.getTicker());
    assertEquals("MSFT", microsoft.getTicker());
    assertEquals("JPM", jpmorgan.getTicker());
    assertEquals("HD", homeDepot.getTicker());
    assertEquals("tsm", tsm.getTicker());
    assertEquals("WMT", walmart.getTicker());
  }

  @Test
  public void testGetClosingPrice() {
    assertEquals(21.23, apple.getClosingPrice("2002-01-10"), 0.01);
    assertEquals(179.54, google.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(947.8, nvidia.getClosingPrice("2024-05-20"), 0.01);
    assertEquals(183.54, amazon.getClosingPrice("2024-05-20"), 0.01);
    assertEquals(165.08, tesla.getClosingPrice("2023-04-21"), 0.01);
    assertEquals(212.89, meta.getClosingPrice("2023-04-21"), 0.01);
    assertEquals(166.72, microsoft.getClosingPrice("2020-01-23"), 0.01);
    assertEquals(199.52, jpmorgan.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(336.15, homeDepot.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(153.67, tsm.getClosingPrice("2024-05-21"), 0.01);
    assertEquals(65.15, walmart.getClosingPrice("2024-05-21"), 0.01);
  }

  @Test
  public void testInvalidDates() {
    // out of range of the csv data (older than the oldest date)
    assertThrows(IllegalArgumentException.class, () -> {
      apple.getClosingPrice("1999-01-10");
    });

    // out of range of the csv data (in the future)
    assertThrows(IllegalArgumentException.class, () -> {
      apple.getClosingPrice("2024-10-10");
    });
  }
}