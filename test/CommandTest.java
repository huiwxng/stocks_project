import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import model.commands.PortfolioPerformanceCommand;
import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.user.BasicUserData;
import model.commands.LoadPortfolioCommand;
import model.commands.PortfolioGetValueCommand;
import model.commands.PortfolioRebalanceCommand;
import model.commands.StockCrossoverCommand;
import model.commands.StockMovingAverageCommand;
import model.commands.StockNetGainCommand;
import model.user.UserData;
import model.commands.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Test class for all commands.
 */
public class CommandTest {
  UserData user;
  Portfolio p;
  Portfolio p1;
  Portfolio p2;

  @Before
  public void setUp() throws Exception {
    user = new BasicUserData();
    user.setCurrentStock("AAPL");

    p = new BasicPortfolio("empty portfolio");

    p1 = new BasicPortfolio("portfolio 1");
    p1.buyStock("AAPL", 10.0, "2024-06-04");

    p2 = new BasicPortfolio("portfolio 2");
    p2.buyStock("AAPL", 10.0, "2024-06-04");
    p2.buyStock("GOOG", 10.0, "2024-06-04");

    user.addPortfolio(p);
    user.addPortfolio(p1);
    user.addPortfolio(p2);
  }

  @Test
  public void testStockNetGainCommand() {
    Command<Double> netGain;

    netGain = new StockNetGainCommand("2024-05-20", "2024-06-04");
    assertEquals(3.31, user.execute(netGain), 0.01);

    netGain = new StockNetGainCommand("2020-05-20", "2024-06-04");
    assertEquals(-124.88, user.execute(netGain), 0.01);

    // same day net gain
    netGain = new StockNetGainCommand("2024-06-04", "2024-06-04");
    assertEquals(0.0, user.execute(netGain), 0.01);

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when the start date is later than the end date
      Command<Double> testThrow = new StockNetGainCommand("2024-06-04", "2020-05-20");
      user.execute(testThrow);
    });
  }

  @Test
  public void testStockMovingAverageCommand() {
    Command<Double> movingAvg;

    movingAvg = new StockMovingAverageCommand("2024-06-04", 5);
    assertEquals(192.44, user.execute(movingAvg), 0.01);

    movingAvg = new StockMovingAverageCommand("2024-06-04", 30);
    assertEquals(183.52, user.execute(movingAvg), 0.01);

    movingAvg = new StockMovingAverageCommand("2020-06-04", 30);
    assertEquals(306.45, user.execute(movingAvg), 0.01);

    movingAvg = new StockMovingAverageCommand("2020-06-04", 100);
    assertEquals(293.31, user.execute(movingAvg), 0.01);

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when x-days value is negative
      Command<Double> testThrow = new StockMovingAverageCommand("2024-06-04", -1);
      user.execute(testThrow);
    });
  }

  @Test
  public void testStockCrossoverCommand() {
    Command<List<String>> crossover;
    List<String> expected;
    List<String> emptyExpected = List.of("No x-day crossovers.");

    // tests for normal behavior

    // tests for periods where there are 5-day crossovers
    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-29", "2024-05-30",
            "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new StockCrossoverCommand("2024-05-20", "2024-06-04", 5);
    assertEquals(expected, user.execute(crossover));

    // tests for periods where there are 7-day crossovers
    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-29", "2024-05-30",
            "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new StockCrossoverCommand("2024-05-20", "2024-06-04", 7);
    assertEquals(expected, user.execute(crossover));

    // tests for periods where there are 30-day crossovers
    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-23", "2024-05-24",
            "2024-05-28", "2024-05-29", "2024-05-30", "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new StockCrossoverCommand("2024-05-20", "2024-06-04", 30);
    assertEquals(expected, user.execute(crossover));

    // tests for periods where there are no x-day crossovers
    crossover = new StockCrossoverCommand("2022-05-23", "2022-06-02", 30);
    assertEquals(emptyExpected, user.execute(crossover));

    // test for same day at the oldest data of the csv
    crossover = new StockCrossoverCommand("1999-11-01", "1999-11-01", 30);
    assertEquals(emptyExpected, user.execute(crossover));

    // test for range at the oldest data of the csv
    expected = List.of("1999-11-02");
    crossover = new StockCrossoverCommand("1999-11-01", "1999-11-02", 30);
    assertEquals(expected, user.execute(crossover));

    // test for range that starts before the oldest date of the csv
    expected = List.of("1999-11-02");
    crossover = new StockCrossoverCommand("1999-10-31", "1999-11-02", 30);
    assertEquals(expected, user.execute(crossover));

    // test for range that ends after the most recent date of the csv
    expected = List.of("2024-06-03", "2024-06-04");
    crossover = new StockCrossoverCommand("2024-06-03", "2024-11-10", 30);
    assertEquals(expected, user.execute(crossover));

    // errors

    // test for exception when the start date is later than the end date
    assertThrows(IllegalArgumentException.class, () -> {
      Command<List<String>> testThrow = new StockCrossoverCommand("2024-06-04", "2020-05-20", 30);
      user.execute(testThrow);
    });

    // test for exception when x-days value is negative
    assertThrows(IllegalArgumentException.class, () -> {
      Command<List<String>> testThrow = new StockCrossoverCommand("2022-05-23", "2022-06-02", -1);
      user.execute(testThrow);
    });

    // test for exception when the end date is before the oldest date in the csv
    assertThrows(IllegalArgumentException.class, () -> {
      Command<List<String>> testThrow = new StockCrossoverCommand("1999-02-01", "1999-03-25", 30);
      user.execute(testThrow);
    });

    // test for exception when the start date is after the most recent date in the csv
    assertThrows(IllegalArgumentException.class, () -> {
      Command<List<String>> testThrow = new StockCrossoverCommand("2024-07-01", "2024-07-02", 30);
      user.execute(testThrow);
    });
  }

  @Test
  public void testPortfolioGetValueCommand() {
    Command<Double> getValue;

    user.setCurrentPortfolio(p1);
    // value is 0 if the date is before any adds
    getValue = new PortfolioGetValueCommand("2024-06-03");
    assertEquals(0, user.execute(getValue), 0.01);

    getValue = new PortfolioGetValueCommand("2024-06-04");
    assertEquals(1943.50, user.execute(getValue), 0.01);

    user.setCurrentPortfolio(p2);
    // value is 0 if the date is before any adds
    getValue = new PortfolioGetValueCommand("2024-06-03");
    assertEquals(0, user.execute(getValue), 0.01);

    getValue = new PortfolioGetValueCommand("2024-06-04");
    assertEquals(3694.80, user.execute(getValue), 0.01);

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when an invalid date is provided
      user.setCurrentPortfolio(p);
      Command<Double> testThrow = new PortfolioGetValueCommand("2024-10-14");
      user.execute(testThrow);
    });
  }

  @Test
  public void testPortfolioRebalanceCommand() {
    Command<Double> getValue;
    Command<String> rebalance;
    List<String> expected;

    user.setCurrentPortfolio(p);
    assertThrows(IllegalArgumentException.class, () -> {
      Command<String> test = new PortfolioRebalanceCommand("2024-06-04");
      user.execute(test);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      Command<String> test = new PortfolioRebalanceCommand("2024-06-04", 100);
      user.execute(test);
    });

    expected = new ArrayList<>();
    user.setCurrentPortfolio(p1);
    rebalance = new PortfolioRebalanceCommand("2024-06-04", 100);
    user.execute(rebalance);
    getValue = new PortfolioGetValueCommand("2024-06-04");
    assertEquals(1943.50, user.execute(getValue), 0.01);
    expected.add("AAPL: 10 share(s)");
    assertEquals(expected, user.getCurrentPortfolio().getComposition("2024-06-04"));

    expected = new ArrayList<>();
    user.setCurrentPortfolio(p2);
    rebalance = new PortfolioRebalanceCommand("2024-06-04", 80, 20);
    user.execute(rebalance);
    getValue = new PortfolioGetValueCommand("2024-06-04");
    assertEquals(3694.80, user.execute(getValue), 0.01);
    expected.add("AAPL: 15.208850012863392 share(s)");
    expected.add("GOOG: 4.219494090104494 share(s)");
    assertEquals(expected, user.getCurrentPortfolio().getComposition("2024-06-04"));
  }

  @Test
  public void testLoadPortfolioCommand() {
    Command<String> loadPF;
    List<String> expected = new ArrayList<>();

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for missing file
      Command<String> test = new LoadPortfolioCommand("hui");
    });

    // tests that a CSV file that is formatted properly is loaded
    loadPF = new LoadPortfolioCommand("test");
    user.execute(loadPF);
    assertEquals(4, user.getNumPortfolios());
    expected.add("AMZN: 100 share(s)");
    assertEquals(expected, user.getCurrentPortfolio().getComposition("2024-06-04"));
    expected = new ArrayList<>();
    expected.add("AMZN: 50 share(s)");
    assertEquals(expected, user.getCurrentPortfolio().getComposition("2024-06-05"));

    // tests that the user cannot buy fractional shares through the csv
    assertThrows(IllegalArgumentException.class, () -> {
      Command<String> test = new LoadPortfolioCommand("buyFractional");
      user.execute(test);
    });

    // tests that a CSV file that is not formatted properly isn't loaded
    Command<String> loadPF2 = new LoadPortfolioCommand("badFormat");
    assertThrows(IllegalArgumentException.class, () -> {
      user.execute(loadPF2);
    });

    // tests that a file that isn't a CSV file can't be loaded
    assertThrows(IllegalArgumentException.class, () -> {
      Command<String> loadPF3 = new LoadPortfolioCommand("notCSV");
    });
  }

  @Test
  public void testPortfolioPerformanceCommand() {
    user.setCurrentPortfolio(p2);
    String expected = "Performance of portfolio portfolio 2 from 2023-06-04 to 2024-06-04\n" +
            "\n" +
            "JUN 04 2023: \n" +
            "JUN 30 2023: \n" +
            "JUL 30 2023: \n" +
            "AUG 30 2023: \n" +
            "SEP 30 2023: \n" +
            "OCT 30 2023: \n" +
            "NOV 30 2023: \n" +
            "DEC 30 2023: \n" +
            "JAN 30 2024: \n" +
            "FEB 29 2024: \n" +
            "MAR 29 2024: \n" +
            "APR 29 2024: \n" +
            "MAY 29 2024: \n" +
            "JUN 04 2024: **************************************************\n" +
            "\n" +
            "Scale: * = 73.90";

    Command<String> visualize;
    visualize = new PortfolioPerformanceCommand("2023-06-04", "2024-06-04");

    assertEquals(expected, user.execute(visualize));

    user.getCurrentPortfolio().buyStock("aapl", 10, "2023-06-04");
    expected = "Performance of portfolio portfolio 2 from 2023-06-04 to 2024-06-04\n" +
            "\n" +
            "JUN 04 2023: ****************\n" +
            "JUN 30 2023: *****************\n" +
            "JUL 30 2023: *****************\n" +
            "AUG 30 2023: ****************\n" +
            "SEP 30 2023: ***************\n" +
            "OCT 30 2023: ***************\n" +
            "NOV 30 2023: ****************\n" +
            "DEC 30 2023: *****************\n" +
            "JAN 30 2024: ****************\n" +
            "FEB 29 2024: ****************\n" +
            "MAR 29 2024: ***************\n" +
            "APR 29 2024: ***************\n" +
            "MAY 29 2024: ****************\n" +
            "JUN 04 2024: **************************************************\n" +
            "\n" +
            "Scale: * = 112.77";

    visualize = new PortfolioPerformanceCommand("2023-06-04", "2024-06-04");

    assertEquals(expected, user.execute(visualize));

    Command<Double> getValue = new PortfolioGetValueCommand("2023-06-04");
    assertEquals(1809.5, user.execute(getValue), 0.01);

    getValue = new PortfolioGetValueCommand("2024-06-04");
    assertEquals(5638.3, user.execute(getValue), 0.01);
  }
}