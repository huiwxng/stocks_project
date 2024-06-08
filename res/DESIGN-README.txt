DESIGN
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