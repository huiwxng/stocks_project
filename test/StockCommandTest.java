import org.junit.Before;
import org.junit.Test;

import java.util.List;

import model.stock.BasicStock;
import model.stock.CrossoverCommand;
import model.stock.MovingAverageCommand;
import model.stock.NetGainCommand;
import model.stock.Stock;
import model.stock.StockCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


/**
 * Test class to test the implementations of the {@link StockCommand} interface.
 */
public class StockCommandTest {

  Stock stock;

  @Before
  public void setUp() {
    stock = new BasicStock("AAPL");
  }

  @Test
  public void testNetGainCommand() {

    StockCommand<Double> netGain;

    netGain = new NetGainCommand("2024-05-20", "2024-06-04");
    assertEquals(3.31, netGain.execute(stock), 0.01);

    netGain = new NetGainCommand("2020-05-20", "2024-06-04");
    assertEquals(-124.88, netGain.execute(stock), 0.01);

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when the start date is later than the end date
      StockCommand<Double> testThrow = new NetGainCommand("2024-06-04", "2020-05-20");
      testThrow.execute(stock);
    });
  }

  @Test
  public void testMovingAverageCommand() {

    StockCommand<Double> movingAvg;

    movingAvg = new MovingAverageCommand("2024-06-04", 5);
    assertEquals(192.44, movingAvg.execute(stock), 0.01);

    movingAvg = new MovingAverageCommand("2024-06-04", 30);
    assertEquals(183.52, movingAvg.execute(stock), 0.01);

    movingAvg = new MovingAverageCommand("2020-06-04", 30);
    assertEquals(306.45, movingAvg.execute(stock), 0.01);

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when x-days value is negative
      StockCommand<Double> testThrow = new MovingAverageCommand("2024-06-04", -1);
      testThrow.execute(stock);
    });
  }

  @Test
  public void testCrossoverCommand() {
    StockCommand<List<String>> crossover;
    List<String> expected;
    List<String> emptyExpected = List.of("No x-day crossovers.");

    // tests for normal behavior

    // tests for periods where there are 5-day crossovers
    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-29", "2024-05-30",
            "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new CrossoverCommand("2024-05-20", "2024-06-04", 5);
    assertEquals(expected, crossover.execute(stock));

    // tests for periods where there are 7-day crossovers
    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-29", "2024-05-30",
            "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new CrossoverCommand("2024-05-20", "2024-06-04", 7);
    assertEquals(expected, crossover.execute(stock));

    // tests for periods where there are 30-day crossovers
    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-23", "2024-05-24",
            "2024-05-28", "2024-05-29", "2024-05-30", "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new CrossoverCommand("2024-05-20", "2024-06-04", 30);
    assertEquals(expected, crossover.execute(stock));

    // tests for periods where there are no x-day crossovers
    crossover = new CrossoverCommand("2022-05-23", "2022-06-02", 30);
    assertEquals(emptyExpected, crossover.execute(stock));

    // test for same day at the oldest data of the csv
    crossover = new CrossoverCommand("1999-11-01", "1999-11-01", 30);
    assertEquals(emptyExpected, crossover.execute(stock));

    // test for range at the oldest data of the csv
    expected = List.of("1999-11-02");
    crossover = new CrossoverCommand("1999-11-01", "1999-11-02", 30);
    assertEquals(expected, crossover.execute(stock));

    // test for range that starts before the oldest date of the csv
    expected = List.of("1999-11-02");
    crossover = new CrossoverCommand("1999-10-31", "1999-11-02", 30);
    assertEquals(expected, crossover.execute(stock));

    // test for range that ends after the most recent date of the csv
    expected = List.of("2024-06-03", "2024-06-04");
    crossover = new CrossoverCommand("2024-06-03", "2024-11-10", 30);
    assertEquals(expected, crossover.execute(stock));

    // errors

    // test for exception when the start date is later than the end date
    assertThrows(IllegalArgumentException.class, () -> {
      StockCommand<List<String>> testThrow = new CrossoverCommand("2024-06-04", "2020-05-20", 30);
      testThrow.execute(stock);
    });

    // test for exception when x-days value is negative
    assertThrows(IllegalArgumentException.class, () -> {
      StockCommand<List<String>> testThrow = new CrossoverCommand("2022-05-23", "2022-06-02", -1);
      testThrow.execute(stock);
    });

    // test for exception when the end date is before the oldest date in the csv
    assertThrows(IllegalArgumentException.class, () -> {
      StockCommand<List<String>> testThrow = new CrossoverCommand("1999-02-01", "1999-03-25", 30);
      testThrow.execute(stock);
    });

    // test for exception when the start date is after the most recent date in the csv
    assertThrows(IllegalArgumentException.class, () -> {
      StockCommand<List<String>> testThrow = new CrossoverCommand("2024-07-01", "2024-07-02", 30);
      testThrow.execute(stock);
    });
  }
}