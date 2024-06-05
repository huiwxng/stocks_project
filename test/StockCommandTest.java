import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import model.stock.BasicStock;
import model.stock.CrossoverCommand;
import model.stock.MovingAverageCommand;
import model.stock.NetGainCommand;
import model.stock.Stock;
import model.stock.StockCommand;

import static org.junit.Assert.*;

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

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when the date is in invalid format
      StockCommand<Double> testThrow = new NetGainCommand("204-06-04", "2020-05-20");
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
      // tests for exception when the date is in invalid format
      StockCommand<Double> testThrow = new MovingAverageCommand("204-06-04", 10);
      testThrow.execute(stock);
    });

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

    expected = List.of("2024-05-20", "2024-05-21", "2024-05-22", "2024-05-29", "2024-05-30",
            "2024-05-31", "2024-06-03", "2024-06-04");
    crossover = new CrossoverCommand("2024-05-20", "2024-06-04", 5);
    assertEquals(expected, crossover.execute(stock));

    expected = List.of();
    crossover = new CrossoverCommand("2022-05-23", "2022-06-02", 30);
    assertEquals(expected, crossover.execute(stock));

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when the start date is later than the end date
      StockCommand<List<String>> testThrow = new CrossoverCommand("2024-06-04", "2020-05-20", 30);
      testThrow.execute(stock);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when the date is in invalid format
      StockCommand<List<String>> testThrow = new CrossoverCommand("204-06-04", "2020-05-20", 30);
      testThrow.execute(stock);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      // tests for exception when x-days value is negative
      StockCommand<List<String>> testThrow = new CrossoverCommand("2022-05-23", "2022-06-02", -1);
      testThrow.execute(stock);
    });
  }
}