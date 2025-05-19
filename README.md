# Stocks Project

## FEATURES:
TEXT UI FEATURES:
- Allows to the user to view a valid stock (check the valid stocks list):
	- Check the most recent closing price
	- Check the closing price on a given valid date
	- Check the net gain between a specified period of time
	- Check the x-day moving average on a valid date
	- Check the x-day crossovers given a specified period of time and x-day
- Allows the user to hold portfolios:
	- Create portfolios
	- View the stocks within each portfolio
	- Get the value of a portfolio on a given valid date
	- Get the value distribution of a portfolio on a given date
	- Buy stocks to add to a portfolio
	- Sell stocks to remove from a portfolio
	- Visualize portfolio performance with a bar graph
	- Delete portfolios
	- Save and load portfolios to and from CSV files

GUI FEATURES:
- Allows the user to hold portfolios:
    - Create portfolios
    - Buy/sell stocks to add to a portfolio on a given date
    - Query the value and composition of the portfolio on a given date
    - Save and load portfolios to and from CSV files

VALID STOCKS LIST (without access to internet):
- AAPL (1999-11-01 to 2024-06-04)
- AMZN (1999-11-01 to 2024-06-04)
- GOOG (2014-03-27 to 2024-06-04)
- HD   (1999-11-01 to 2024-06-04)
- JPM  (1999-11-01 to 2024-06-04)
- META (2012-05-18 to 2024-06-04)
- MSFT (1999-11-01 to 2024-06-04)
- NVDA (1999-11-01 to 2024-06-04)
- TSLA (2010-06-29 to 2024-06-04)
- TSM  (1999-11-01 to 2024-06-04)

## SETUP:
To run this program, follow these steps:
1. In the directory that the JAR file is in, you must have:
	- a folder named 'data' (this folder will hold all the data for the stocks in csv format)
        - a folder named 'portfolios' (this folder will hold your portfolios in csv format)
        - a file called apikey.txt (input your API key from AlphaVantage here)
2. Navigate to the directory that holds the JAR file
3. Run the command:
	java -jar StocksProgram.jar for the graphical user interface
	java -jar StocksProgram.jar -text for the text based interface
	OR
	Double-click the jar file for the graphical user interface.

TEXT UI BASICS:
To create a portfolio, purchase stocks of at least 3 different companies in that portfolio at different dates and then query the value and cost basis of that portfolio on two specific dates:

To create a portfolio and purchase stocks of 3 different companies and 3 different dates:
1. Run the program (using the instructions in the SETUP section)
2. From the Start Menu:
	- Input '1' and press ENTER to get into the Portfolios Menu
3. From the Portfolios Menu:
	- Input '1' and press ENTER
	- Input a name for the Portfolio and press ENTER to get into the Individual Portfolio Menu
4. From the Individual Portfolio Menu:
    - Input '3' and press ENTER to buy a stock
    - Input a valid stock ticker (ex: AAPL) and press ENTER
        - If you have access to the internet, you may use any valid stock ticker
        - If you do not have access to the internet, check the list of stocks the program supports
    - Input the number of stocks (a reasonable integer) you would like to purchase/add to the portfolio and press ENTER
    - Input a valid year, month, and day
        - To check if the date is valid, check the list of stocks the program supports
5. Repeat Step 4 two more times with different valid stock tickers and different valid dates

To query the value of the portfolio on two different dates:
6. To query the value of the portfolio on one day:
   	- Input '2' and press ENTER
   	- Input a valid year, month, and day
        - To check if the date is valid, check the list of stocks the program supports
7. Repeat Step 6 with a different date
8. Input 'q' and press ENTER at any menu to quit

GUI BASICS:
To create a portfolio, purchase stocks of at least 3 different companies in that portfolio at different dates and then query the value and cost basis of that portfolio on two specific dates:
1. Run the program (using the instructions in the SETUP section)
2. From the Start Menu:
    - Click the 'Create' button
    - Input your portfolio name when the prompt shows up
    - Double-click the new portfolio in the list on the right
3. From the Individual Portfolio Menu:
    - Input a valid stock ticker
        - If you have access to the internet, you may use any valid stock ticker
        - If you do not have access to the internet, check the list of stocks the program supports
    - Input a valid stock share count (a reasonable integer) for the number of stock shares you would like to purchase to the portfolio
    - Input a valid date
4. Repeat Step 3 two more times with different valid stock tickers and different valid dates

To query the value of the portfolio on two different dates:
6. To query the value of the portfolio on one day:
    - Input a valid date
    - Click the 'Query' button
7. Repeat Step 6 with a different date

## DESIGN

DESIGN CHANGES:
- We created a view interface and made different controllers take in different views depending
  on the user interface. For example, for a text based interface, a text controller would take
  in a text view. For a graphic used interface, a graphic controller would take in a graphic view.
- For the text based interface, we have a TextView that has a Readable, Appendable, and scanner.
  This interface implements the writeMessage(String message) method for all views and has its own
  getUserInput() method to get the user's input from the scanner.
- For the graphic user interface, we have a GraphicView that works with various swing components.
- Instead of having one StockController, we made a TextStockController and a GraphicStockController.
  They handle the logic between the UserData model and their respective views.

OLD DESIGN
- Model/View/Controller using Command Design Pattern
	- (MODEL) A UserData interface that represents the user and acts as our main Model
		- BasicUserData implements the methods in the UserData interface
			- Executes on our commands
	- (CONTROLLER/VIEW) IController interface that represents the controller
		- StockController implements the methods in the IController interface
			- Takes in a model, input, and output
			- The implementation also acts as our view by appending to our Appendable output

	- Commands:
		- We had a command for each of the functions that we needed
			- PortfolioGetValueCommand loops through the stocks in the current portfolio and returns the value
			- StockNetGainCommand gets the difference between the closing prices of two days
			- StockMovingAverageCommand gets the average of the last x-days
			- StockCrossoverCommand gets the list of days where an x-days crossover exists
	- Objects:
		- A Stock interface that represents an individual stock
			- BasicStock implements the methods in Stock interface
				- Gets data from local files if possible, if not, then it grabs the data from the API
		- A Portfolio interface that represents a single portfolio
			- BasicPortfolio implements the methods in Portfolio interface

DESIGN CHOICES:
- We have 11 preset valid stocks in our given data folder. If the user does not have access to the internet, they will only have those 11 stocks at their disposal. If the user does have access to the internet, the program will try to pull data from the Alpha Vantage API (with an API key in the apikey.txt file). If the stock ticker does not exist on the API, then the user will be prompted to try another stock ticker.
- For getting data on dates where there is no data on the stock for prices (weekends, holidays, too far back, future):
	- For dates that are within the range of the present date - oldest date of the data, we decided to roll the date backwards until there was data on the date.
	- For dates that are beyond the scope of the data (too far back or the future), we decided to throw an error.
	- For commands that require a specified range (x-day crossovers), if there are dates in that range that are out of the scope of our data as well as dates that are within the scope of our data, we will either advance the start date or roll back the end date until the range is fully in the scope and run the command on the new valid range.
