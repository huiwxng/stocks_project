package view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import model.portfolio.Portfolio;

/**
 * This class represents an implementation of the view using JFrame for a
 * graphical user interface.
 */
public class GraphicView extends JFrame implements IView {
  private final JFrame frame;
  private final DefaultListModel<Portfolio> portfolios;
  private JList<String> portfolioList;
  private JButton create, load, trade, save, backButton, query;
  private JToggleButton buy, sell;
  private JTextArea notifications, info;
  private JTextField tickerField;
  private JSpinner countSpinner, tradeDateSpinner, queryDateSpinner;

  /**
   * Construct a view for a graphical user interface.
   */
  public GraphicView() {
    frame = new JFrame();
    portfolios = new DefaultListModel<>();
    mainMenu();
  }

  public void mainMenu() {
    // set frame
    frame.getContentPane().removeAll();
    frame.setTitle("Virtual Stocks Program");
    frame.setSize(640, 480);
    frame.setResizable(false);
    frame.setLayout(new GridLayout(1, 2));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);

    // set button panel on left
    JPanel buttonPanel = createInitialButtonPanel();

    // set portfolios panel on right
    JPanel portfolioListPanel = createPortfolioListPanel();

    // add panels and make frame visible
    frame.add(buttonPanel);
    frame.add(portfolioListPanel);
    frame.revalidate();
    frame.repaint();
    frame.setVisible(true);
  }

  private JPanel createInitialButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    create = createButton("Create Portfolio", 200, 100, 14);
    create.setActionCommand("create");
    load = createButton("Load (from CSV file)", 200, 100, 14);
    load.setActionCommand("load");
    buttonPanel.add(create, gbc);
    gbc.gridy++;
    buttonPanel.add(load, gbc);
    return buttonPanel;
  }

  private JPanel createPortfolioListPanel() {
    JPanel portfolioListPanel = new JPanel();
    portfolioListPanel.setBorder(BorderFactory.createTitledBorder("Select your portfolio: "));
    portfolioListPanel.setLayout(new BorderLayout());
    DefaultListModel<String> portfolioNames = new DefaultListModel<>();
    for (int i = 0; i < portfolios.size(); i++) {
      portfolioNames.add(i, portfolios.get(i).getName());
    }
    portfolioList = new JList<>(portfolioNames);
    portfolioList.setFont(new Font("Verdana", Font.PLAIN, 14));
    portfolioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(portfolioList);
    portfolioListPanel.add(scrollPane, BorderLayout.CENTER);
    return portfolioListPanel;
  }

  private JButton createButton(String name, int width, int height, int fontsize) {
    JButton button = new JButton(name);
    button.setPreferredSize(new Dimension(width, height));
    button.setFont(new Font("Verdana", Font.PLAIN, fontsize));
    return button;
  }

  private JToggleButton createToggle(String name, int width, int height, int fontsize) {
    JToggleButton toggle = new JToggleButton(name);
    toggle.setPreferredSize(new Dimension(width, height));
    toggle.setFont(new Font("Verdana", Font.PLAIN, fontsize));
    return toggle;
  }

  /**
   * Adds action listeners for initial buttons, so they can do something if they are clicked.
   *
   * @param listener listener that waits for the action
   */
  public void setInitialActionListener(ActionListener listener) {
    create.addActionListener(listener);
    load.addActionListener(listener);
  }

  public void setSpecificPortfolioActionListener(ActionListener listener) {
    buy.addActionListener(listener);
    sell.addActionListener(listener);
    trade.addActionListener(listener);
    save.addActionListener(listener);
    backButton.addActionListener(listener);
    query.addActionListener(listener);
  }

  /**
   * Adds list election listener for every list, so if an option is selected, something can
   * happen.
   *
   * @param listener listener that waits for the selection
   */
  public void setListListener(ListSelectionListener listener) {
    portfolioList.addListSelectionListener(listener);
  }

  /**
   * Updates the currently displayed list of portfolios by adding the new
   * list of portfolios.
   *
   * @param newPortfolios new list of portfolios to be added
   */
  public void updatePortfolioList(List<Portfolio> newPortfolios) {
    portfolios.clear();
    for (Portfolio portfolio : newPortfolios) {
      portfolios.addElement(portfolio);
    }
    DefaultListModel<String> portfolioNames = new DefaultListModel<>();
    for (int i = 0; i < portfolios.size(); i++) {
      portfolioNames.add(i, portfolios.get(i).getName());
    }
    portfolioList.setModel(portfolioNames);
  }

  /**
   * Gets the index of the current portfolio / the portfolio the user selected from the list.
   *
   * @return int representing the index
   */
  public int currentPortfolioIndex() {
    return portfolioList.getSelectedIndex();
  }

  /**
   * Loads the portfolio menu for a specific portfolio.
   */
  public void specificPortfolioMenu() {
    // reset frame
    frame.getContentPane().removeAll();
    frame.setLayout(new GridLayout(1, 2));

    // set button and notification panel on the left
    JPanel leftPanel = createSpecificPortfolioLeftPanel();

    // set back button, portfolio name, and composition panel on the right
    JPanel rightPanel = createSpecificPortfolioRightPanel();

    frame.add(leftPanel);
    frame.add(rightPanel);
    frame.revalidate();
    frame.repaint();
  }

  private JPanel createSpecificPortfolioLeftPanel() {
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));

    // create buttons
    JPanel togglesPanel = new JPanel(new GridLayout());
    buy = createToggle("Buy Stock", 125, 50, 12);
    buy.setActionCommand("buy");
    buy.setSelected(true);
    sell = createToggle("Sell Stock", 125, 50, 12);
    sell.setActionCommand("sell");
    togglesPanel.add(buy);
    togglesPanel.add(sell);
    leftPanel.add(togglesPanel);

    // create input fields
    JPanel inputsPanel = createInputsPanel();
    leftPanel.add(inputsPanel);

    // create trade/save buttons
    trade = createButton("Confirm Trade", 200, 50, 12);
    trade.setActionCommand("trade");
    leftPanel.add(trade);
    save = createButton("Save (to a CSV file)", 200, 50, 12);
    save.setActionCommand("save");
    leftPanel.add(save);

    // create notification area
    notifications = createNotificationArea();
    leftPanel.add(new JScrollPane(notifications));

    return leftPanel;
  }

  /**
   * Gets the button that is toggled for the trade (buy/sell).
   *
   * @param name name of the button (buy/sell)
   * @return button that isi toggled
   */
  public JToggleButton getToggle(String name) {
    if (name.equals("buy")) {
      return buy;
    } else if (name.equals("sell")) {
      return sell;
    }
    throw new IllegalArgumentException("Toggle name does not exist.");
  }

  private JPanel createInputsPanel() {
    JPanel inputsPanel = new JPanel(new GridLayout(3, 3));

    // ticker input
    JPanel tickerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel tickerLabel = new JLabel("Ticker:");
    tickerField = new JTextField(5);
    tickerPanel.add(tickerLabel);
    tickerPanel.add(tickerField);

    // count input
    JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel countLabel = new JLabel("Amount:");
    countSpinner = new JSpinner(new SpinnerNumberModel(
            1, 1, Integer.MAX_VALUE, 1));
    JSpinner.NumberEditor countEditor = new JSpinner.NumberEditor(
            countSpinner, "#");
    countEditor.getTextField().setColumns(5);
    countSpinner.setEditor(countEditor);
    countPanel.add(countLabel);
    countPanel.add(countSpinner);

    // date input
    JPanel datePanel = new JPanel(new FlowLayout());
    JLabel dateLabel = new JLabel("Date:");
    tradeDateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(
            tradeDateSpinner, "yyyy-MM-dd");
    tradeDateSpinner.setEditor(dateEditor);
    datePanel.add(dateLabel);
    datePanel.add(tradeDateSpinner);

    inputsPanel.add(tickerPanel);
    inputsPanel.add(countPanel);
    inputsPanel.add(datePanel);

    return inputsPanel;
  }

  public String getTicker() {
    return tickerField.getText().toUpperCase();
  }

  public int getCount() {
    return (int) countSpinner.getValue();
  }

  public String getTradeDate() {
    return new SimpleDateFormat("yyyy-MM-dd").format((Date) tradeDateSpinner.getValue());
  }

  public String getQueryDate() {
    return new SimpleDateFormat("yyyy-MM-dd").format((Date) queryDateSpinner.getValue());
  }

  private JTextArea createNotificationArea() {
    JTextArea notifications = new JTextArea(8, 23);
    notifications.setFont(new Font("Verdana", Font.PLAIN, 12));
    notifications.setLineWrap(true);
    notifications.setFocusable(false);
    notifications.setEditable(false);
    notifications.setBorder(BorderFactory.createTitledBorder("Notifications: "));
    return notifications;
  }

  private JPanel createSpecificPortfolioRightPanel() {
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    TitledBorder border = BorderFactory.createTitledBorder(portfolios.get(
            currentPortfolioIndex()).getName() + " ");
    border.setTitleFont(new Font("Verdana", Font.ITALIC, 16));
    rightPanel.setBorder(border);

    // set date
    JPanel back = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, -5));
    backButton = createButton("Main Menu", 100, 30, 12);
    backButton.setActionCommand("back");

    JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    JPanel datePanel = new JPanel(new FlowLayout());
    JLabel dateLabel = new JLabel("Date:");
    queryDateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(
            queryDateSpinner, "yyyy-MM-dd");
    queryDateSpinner.setEditor(dateEditor);
    datePanel.add(dateLabel);
    datePanel.add(queryDateSpinner);

    query = createButton("Query", 75, 30, 12);
    query.setActionCommand("query");

    info = new JTextArea(21, 23);
    info.setFont(new Font("Verdana", Font.PLAIN, 12));
    info.setLineWrap(true);
    info.setFocusable(false);
    info.setEditable(false);
    info.setBorder(BorderFactory.createTitledBorder("Portfolio Information: "));

    back.add(backButton);
    title.add(datePanel);
    title.add(query);

    rightPanel.add(back);
    rightPanel.add(title);
    rightPanel.add(info);

    return rightPanel;
  }

  @Override
  public void showMessage(String message) {
    notifications.append(message + "\n");
  }

  public void showQuery(String message) {
    info.setText(message);
  }
}