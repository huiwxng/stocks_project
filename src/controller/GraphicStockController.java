package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.commands.Command;
import model.commands.LoadPortfolioCommand;
import model.portfolio.BasicPortfolio;
import model.portfolio.Portfolio;
import model.user.UserData;
import view.GraphicView;

/**
 * This class represents the controller of an interactive virtual stocks application.
 * This controller offers a graphical user interface in which users can create portfolios,
 * buy/sell stocks given a ticker, amount, and date, query the value and composition of a portfolio
 * on a certain date, and save/retrieve portfolios using CSV files.
 */
public class GraphicStockController implements IController, ActionListener, ListSelectionListener {
  private final UserData userData;
  private final GraphicView view;
  private boolean tradeState;

  /**
   * Create a controller that works with a specified UserData that contains
   * a list of Portfolios.
   *
   * @param userData the user/UserData to work with (model)
   * @param view     the view that the controller tells what to do
   */
  public GraphicStockController(UserData userData, GraphicView view) {
    if ((userData == null) || (view == null)) {
      throw new IllegalArgumentException("UserData or view is null.");
    }
    this.userData = userData;
    this.view = view;
    view.setInitialActionListener(this);
    view.setListListener(this);
    this.tradeState = true;
  }

  @Override
  public void control() throws IllegalStateException {

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "create":
        createPortfolio();
        break;
      case "load":
        loadPortfolio();
        break;
      case "buy":
        buyStock();
        break;
      case "sell":
        sellStock();
        break;
      case "trade":
        confirmTrade();
        break;
      case "save":
        savePortfolio();
        break;
      default:
        break;
    }
  }

  private void createPortfolio() {
    String name = JOptionPane.showInputDialog("New Portfolio Name:");
    if (name == null || name.isEmpty()) {
      return;
    }
    Portfolio newPortfolio = new BasicPortfolio(name);
    userData.addPortfolio(newPortfolio);
    view.updatePortfolioList(userData.listPortfolios());
  }

  private void loadPortfolio() {
    JFileChooser chooser = new JFileChooser(".");
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "CSV Files", "csv");
    chooser.setFileFilter(filter);
    chooser.setAcceptAllFileFilterUsed(false);
    int retValue = chooser.showOpenDialog(view);
    if (retValue == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      String fileName = file.getName();
      fileName = fileName.substring(0, fileName.lastIndexOf(".csv"));
      try {
        Command<String> load = new LoadPortfolioCommand(fileName);
        userData.execute(load);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(view, e.getMessage());
      }
      view.updatePortfolioList(userData.listPortfolios());
    }
  }

  private void buyStock() {
    view.getToggle("buy").setSelected(true);
    view.getToggle("sell").setSelected(false);
    tradeState = true;
  }

  private void sellStock() {
    view.getToggle("sell").setSelected(true);
    view.getToggle("buy").setSelected(false);
    tradeState = false;
  }

  private void confirmTrade() {
    try {
      if (tradeState) {
        userData.getCurrentPortfolio().buyStock(view.getTicker(), view.getCount(), view.getDate());
        view.showMessage(String.format(
                "Bought %d share(s) of %s on %s.",
                view.getCount(), view.getTicker(), view.getDate()));
      } else {
        userData.getCurrentPortfolio().sellStock(view.getTicker(), view.getCount(), view.getDate());
        view.showMessage(String.format(
                "Sold %d share(s) of %s on %s.", view.getCount(), view.getTicker(), view.getDate()));
      }
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(view, e.getMessage());
    }
  }

  private void savePortfolio() {
      view.showMessage(userData.getCurrentPortfolio().save());
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      viewPortfolio();
    }
  }

  private void viewPortfolio() {
    int index = view.currentPortfolioIndex();
    userData.setCurrentPortfolio(userData.getPortfolio(index));
    view.setSpecificPortfolioActionListener(this);
  }
}
