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
   - **int**: Used for storing integer values such as lengths, periods, etc.
   - **float**: Commonly used for calculations involving prices or indicators.
   - **bool**: Frequently used for defining conditions or flags.
   - **series**: Essential for storing time-series data such as prices or indicator values.
   - **input**: Critical for allowing users to customize parameters of the script.

These variables are the building blocks of Pine Script, allowing you to manipulate data, define conditions, customize inputs, and visualize results. Understanding their usage and syntax is crucial for developing effective and versatile trading scripts on the TradingView platform.




  ...
