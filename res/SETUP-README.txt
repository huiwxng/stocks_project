SETUP:
To run this program, follow these steps:
1. In the directory that the JAR file is in, you must have:
    - a folder named 'res' and inside that folder you should have:
        - a folder named 'data' (this folder will hold all the data for the stocks in csv format)
        - a folder named 'portfolios' (this folder will hold your portfolios in csv format)
        - a file called apikey.txt (input your API key from AlphaVantage here)
2. Launch your Command Line Interface and navigate to the directory that holds the JAR file
3. Run the command:
	java -jar StocksProgram.jar

BASICS:
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