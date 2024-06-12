package model.user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;

public class LoadPortfolioCommand implements Command<String> {
  private final String dirPath = "res/portfolios/";
  private final String ext = ".csv";
  private final String filename;
  private final String path;

  public LoadPortfolioCommand(String filename) {
    this.filename = filename;
    this.path = dirPath + filename + ext;

    if (!Files.exists(Path.of(path))) {
      throw new IllegalArgumentException("This file does not exist.");
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

        if (type) {
          pf.buyStock(ticker, amount, date);
        } else {
          pf.sellStock(ticker, amount, date);
        }
      }

      user.addPortfolio(pf);
      msg = "Successfully loaded the portfolio";
    } catch (IOException | NumberFormatException e) {
      System.err.println("Error reading CSV file: " + e.getMessage());
      msg = "Error reading CSV file.";
    } catch (IllegalArgumentException e) {
      System.err.println("Error creating portfolio: " + e.getMessage());
      msg = "Error creating portfolio.";
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
