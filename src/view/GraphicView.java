package view;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
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
  private JButton create, load, buy, sell, save;
  private JTextArea notifications;

  /**
   * Construct a view for a graphical user interface.
   */
  public GraphicView() {
    frame = new JFrame();
    portfolios = new DefaultListModel<>();
    initialize();
  }

  private void initialize() {
    // set frame
    frame.setTitle("Virtual Stocks Program");
    frame.setSize(600, 400);
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
    frame.setVisible(true);
  }

  private JPanel createInitialButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    create = createButton("Create Portfolio", 200 ,100,
            new Font("Verdana", Font.PLAIN, 14));
    create.setActionCommand("create");
    load = createButton("Load (from CSV file)", 200, 100,
            new Font("Verdana", Font.PLAIN, 14));
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

  private JButton createButton(String name, int width, int height, Font font) {
    JButton button = new JButton(name);
    button.setPreferredSize(new Dimension(width, height));
    button.setFont(font);
    return button;
  }

  /**
   * Adds action listeners for initial buttons, so they can do something if they are clicked.
   *
   * @param listener listener that waits for the action
   */
  public void setInitialActionListener(ActionListener listener) {
    create.addActionListener(listener);
    load.addActionListener(listener);;
  }

  public void setSpecificPortfolioActionListener(ActionListener listener) {
    buy.addActionListener(listener);
    sell.addActionListener(listener);
    save.addActionListener(listener);
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
   * @return int representing the index
   */
  public int currentPortfolioIndex() {
    return portfolioList.getSelectedIndex();
  }

  /**
   * Loads the portfolio menu for a specific portfolio.
   * @param portfolio portfolio that menu is to be loaded for
   */
  public void specificPortfolioMenu(Portfolio portfolio) {
    // reset frame
    frame.getContentPane().removeAll();
    frame.setLayout(new GridLayout(1, 2));

    // set button and notification panel on the left
    JPanel leftPanel = createSpecificPortfolioLeftPanel();

    // set back button, portfolio name, and composition panel on the right
    JPanel rightPanel = new JPanel();

    frame.add(leftPanel);
    frame.add(rightPanel);
    frame.revalidate();
    frame.repaint();
  }

  private JPanel createSpecificPortfolioLeftPanel() {
    JPanel leftPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    buy = createButton("Buy Stock", 100, 50,
            new Font("Verdana", Font.PLAIN, 12));
    buy.setActionCommand("buy");
    sell = createButton("Sell Stock", 100, 50,
            new Font("Verdana", Font.PLAIN, 12));
    sell.setActionCommand("sell");
    save = createButton("Save (to a CSV file)", 200, 50,
            new Font("Verdana", Font.PLAIN, 12));
    save.setActionCommand("save");
    notifications = createNotificationArea();
    leftPanel.add(buy, gbc);
    gbc.gridy++;
    leftPanel.add(sell, gbc);
    gbc.gridy++;
    leftPanel.add(save, gbc);
    gbc.gridy++;
    leftPanel.add(new JScrollPane(notifications), gbc);
    return leftPanel;
  }

  private JTextArea createNotificationArea() {
    JTextArea notifications = new JTextArea(3, 20);
    notifications.setFont(new Font("Verdana", Font.PLAIN, 12));
    notifications.setLineWrap(true);
    notifications.setFocusable(false);
    notifications.setEditable(false);
    notifications.setBorder(BorderFactory.createTitledBorder("Notifications: "));
    return notifications;
  }

  private JPanel createSpecificPortfolioRightPanel() {
    JPanel rightPanel = new JPanel(new GridLayout());
    return rightPanel;
  }

  @Override
  public void showMessage(String message) {
    notifications.append(message + "\n");
  }
}