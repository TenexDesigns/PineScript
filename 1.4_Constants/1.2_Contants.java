Pine Script, the programming language used on TradingView, offers a wide range of built-in variables and functions that provide valuable information and make calculations for you, allowing you to focus on the logic of your scripts rather than the underlying calculations. Here's an overview of some of the most commonly used built-in variables and functions in Pine Script, as detailed in the Pine Script v5 User Manual and Language Reference Manual:

### Built-in Variables

- **`close`**: The closing price of the current bar.
- **`open`**: The opening price of the current bar.
- **`high`**: The highest price of the current bar.
- **`low`**: The lowest price of the current bar.
- **`volume`**: The volume of the current bar.
- **`time`**: The time at the open of the current bar.
- **`syminfo.tickerid`**: The ticker ID of the current symbol.
- **`syminfo.mintick`**: The smallest price change for the current symbol.

### Built-in Functions

- **`sma(source, length)`**: Calculates the Simple Moving Average of the `source` over the specified `length`.
- **`ema(source, length)`**: Calculates the Exponential Moving Average of the `source` over the specified `length`.
- **`rsi(source, length)`**: Calculates the Relative Strength Index of the `source` over the specified `length`.
- **`macd(source, fastLength, slowLength, signalSmoothing)`**: Calculates the MACD (Moving Average Convergence Divergence) of the `source` with the specified `fastLength`, `slowLength`, and `signalSmoothing`.
- **`crossover(series1, series2)`**: Returns `true` if `series1` crosses over `series2`.
- **`crossunder(series1, series2)`**: Returns `true` if `series1` crosses under `series2`.

### Namespaces

Pine Script organizes built-in variables and functions into namespaces, which are prefixes to the function or variable name. For example, the `ta` namespace stands for "technical analysis" and includes functions like `ta.sma()` for calculating the Simple Moving Average.

### Time Functions

- **`time(timeframe, session, timezone)`**: Returns the time of the bar's open from the specified `timeframe`, but only if it is within the `session` time in the specified `timezone`.

Understanding these built-ins is crucial for effectively using Pine Script to analyze market data and make informed trading decisions. The Pine Script v5 User Manual and Language Reference Manual are comprehensive resources that provide detailed information on all built-in variables and functions, making them an essential tool for both beginners and experienced Pine Script users [2][3][4].

Citations:
[1] https://www.tradingview.com/pine-script-reference/v5/
[2] https://www.tradingview.com/pine-script-docs/en/v5/language/Built-ins.html
[3] https://www.kaggle.com/datasets/aramacus/pine-script-language-reference-manual
[4] https://medium.com/coinmonks/pine-script-the-basics-6ec4eca1da61
[5] https://www.youtube.com/watch?v=wheZP0gfZB0
[6] https://gist.github.com/kaigouthro/b95a8b4c43e607ea71897e204904b9c0
[7] https://www.tradingcode.net/tradingview/to-string-function/
[8] https://www.youtube.com/watch?v=NWL9ZuYRtd8
[9] https://medium.com/@faizur.sezen/how-to-create-and-use-variables-in-pine-script-841d5b8de50c
[10] https://docs.bulltrading.io/bulltrading-designer/pinescript/pine-script-tm-v5-user-manual/built-ins
