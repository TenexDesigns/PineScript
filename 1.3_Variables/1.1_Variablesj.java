In Pine Script, variables are used to store and manipulate data throughout your script. Understanding the various types of variables and how they work is essential for creating effective scripts. Here's a reference manual for some of the most commonly used variable types in Pine Script:

### 1. **Numeric Variables**:
   - **int**: Represents integer values. Example: `var int myInt = 10;`
   - **float**: Represents floating-point values (decimals). Example: `var float myFloat = 10.5;`
   - **series**: Special type for storing time-series data. Example: `var series<float> mySeries = close;`

### 2. **Boolean Variables**:
   - **bool**: Represents boolean values (true/false). Example: `var bool isAboveMA = close > sma;`

### 3. **String Variables**:
   - **string**: Represents text strings. Example: `var string myString = "Hello, world!";`

### 4. **Arrays**:
   - **Array**: Represents a collection of elements of the same type. Example: `var float[] myArray = array.new_float(10);`

### 5. **Plotting Variables**:
   - **color**: Represents colors. Example: `var color myColor = color.red;`
   - **linewidth**: Represents the width of plotted lines. Example: `var linewidth myLineWidth = linewidth.normal;`

### 6. **Special Variables**:
   - **ta**: Provides access to built-in technical analysis functions.
   - **strategy**: Provides access to strategy-related functions.

### 7. **Input Variables**:
   - **input**: Used for defining script inputs that can be adjusted by users. Example: `length = input.int(20, "Length")`



   
### Most Used Variables:
1. **Close Price (`close`):** The current or historical closing price of the security.
2. **Open Price (`open`):** The current or historical opening price of the security.
3. **High Price (`high`):** The current or historical highest price of the security within a bar.
4. **Low Price (`low`):** The current or historical lowest price of the security within a bar.
5. **Volume (`volume`):** The traded volume for the current or historical bar.
6. **Time (`time`):** The current or historical bar's time in seconds since the epoch (January 1, 1970).
7. **`hour`:** The current or historical hour (0-23).
8. **`minute`:** The current or historical minute (0-59).
9. **`na`:** A special value representing "not available" or missing data.
10.**int**: Used for storing integer values such as lengths, periods, etc.
11.**float**: Commonly used for calculations involving prices or indicators.
12.**bool**: Frequently used for defining conditions or flags.
13.**series**: Essential for storing time-series data such as prices or indicator values.
14.**input**: Critical for allowing users to customize parameters of the script.



   **Additional Considerations:**

- Variable names must start with a letter (A-Z or a-z) and can contain letters, numbers, and underscores (`_`).
- Pine Script is case-sensitive (`entry` is different from `Entry`).
- Arrays (`array.new()`) and dictionaries (`dict.new()`) can store collections of data.



**Tips for Using Variables Effectively:**

- **Descriptive Names:** Choose meaningful names that reflect their purpose (e.g., `entryPriceLong`, `profitTarget`).
- **Scope:** Variables declared within a function are localized to that function. Use global variables sparingly and with caution.
- **Comments:** Add comments to explain complex logic or variable usage.


**Example: Calculating Position Size:**

```pine
//@version=5
strategy("Position Size Example", overlay=true)

// User input for risk percentage
risk_percent = input(1, minval=0.01, maxval=5, title="Risk Percentage")

// Account equity
account_equity = strategy.equity

// Calculate position size in lots
lot_size = (account_equity * risk_percent / 100) / symbolinfo.tickvalue

// ... (rest of your strategy)
```



   

**In Conclusion:**

By understanding Pine Script variables, you can effectively store, manipulate, and use data within your scripts, enabling the creation of powerful technical indicators and trading strategies. 
   Practice and experiment to become proficient in using variables and other Pine Script features for your trading endeavors.





