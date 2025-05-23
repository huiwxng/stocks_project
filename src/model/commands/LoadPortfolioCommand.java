package model.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.user.UserData;

/**
 * Command to load a properly formatted CSV file as a portfolio that the user
 * can manipulate in the program.
 */
public class LoadPortfolioCommand implements Command<String> {
  private final String dirPath = "portfolios/";
  private final String ext = ".csv";
  private final String filename;
  private final String path;

  /**
   * Constructs a command that loads the CSV file that represents the portfolio.
   * @param filename name of the CSV file without the .csv extension
   */
  public LoadPortfolioCommand(String filename) {
    this.filename = filename;
    this.path = dirPath + filename + ext;

    if (!Files.exists(Path.of(path))) {
      throw new IllegalArgumentException("This file does not exist in the portfolios folder.");
    }
  }

  /**
   * Executes the command onto a {@link UserData} object.
   *
   * @param user {@link UserData} object
   * @return a value given the command
   */
  @Override
  public String execute(UserData user) {
    String msg;
    Portfolio pf = new BasicPortfolio(filename);
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));

      String line = br.readLine();
      String[] headers = line.split(",");
      int dateIndex = findIndex(headers, "date");
      int typeIndex = findIndex(headers, "type");
      int tickerIndex = findIndex(headers, "ticker");
      int amountIndex = findIndex(headers, "amount");
      if (dateIndex == -1 || typeIndex == -1 || tickerIndex == -1 || amountIndex == -1) {
        throw new IllegalArgumentException(
                "Reformat csv file or add a header. Date,Type,Ticker,Amount");
      }

      while ((line = br.readLine()) != null) {
        String[] parts = line.split(",");
        String date = parts[dateIndex].trim();
        boolean type = parts[typeIndex].trim().equalsIgnoreCase("buy");
        String ticker = parts[tickerIndex].trim();
        double amount = Double.parseDouble(parts[amountIndex].trim());
        if (type && amount % 1 != 0) {
          throw new IllegalArgumentException("Cannot buy fractional shares.");
        }

        if (type) {
          pf.buyStock(ticker, amount, date);
        } else {
          pf.sellStock(ticker, amount, date);
        }
      }

      user.addPortfolio(pf);
      user.setCurrentPortfolio(pf);
      msg = "Successfully loaded the portfolio from " + path;
    } catch (IOException | NumberFormatException e) {
      throw new IllegalArgumentException("Error reading CSV file: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Error creating portfolio: " + e.getMessage());
    }
    return msg;
  }

  private int findIndex(String[] strList, String str) {
    for (int i = 0; i < strList.length; i++) {
      if (strList[i].equalsIgnoreCase(str)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the name of the command being executed.
   *
   * @return the String for the name of the command.
   */
  @Override
  public String getName() {
    return "load portfolio";
  }
}
