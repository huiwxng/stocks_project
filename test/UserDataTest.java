import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Port;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.stock.BasicStock;
import model.stock.Stock;
import model.user.BasicUserData;
import model.user.UserData;

import static org.junit.Assert.*;

/**
 * Test class for our model.
 */
public class UserDataTest {

  UserData user;

  @Before
  public void setUp() throws Exception {
    user = new BasicUserData();
  }

  @Test
  public void testPortfolioManagement() {
    // add portfolio
    List<Portfolio> expected = new ArrayList<>();
    assertEquals(expected, user.listPortfolios());

    Portfolio p = new BasicPortfolio("empty");
    user.addPortfolio(p);
    expected.add(p);
    assertEquals(expected, user.listPortfolios());
    assertEquals(p, user.getPortfolio(0));

    // remove portfolio
    user.removePortfolio(p);
    expected.remove(p);
    assertEquals(expected, user.listPortfolios());
  }

  @Test
  public void testCurrentPortfolio() {
    Portfolio p = new BasicPortfolio("empty");
    // setting a current portfolio when it does not exist in our portfolios list
    assertThrows(IllegalArgumentException.class, () -> {
      user.setCurrentPortfolio(p);
    });

    user.addPortfolio(p);
    user.setCurrentPortfolio(p);
    assertEquals(p, user.getCurrentPortfolio());
  }

  @Test
  public void testViewStock() {
    assertThrows(IllegalArgumentException.class, () -> {
      user.getCurrentStock();
    });

    assertThrows(IllegalArgumentException.class, () -> {
      user.setCurrentStock("asdfasdfasdf");
    });

    Stock expected = new BasicStock("GOOG");
    user.setCurrentStock("GOOG");
    assertEquals(expected.getTicker(), user.getCurrentStock().getTicker());
  }
}