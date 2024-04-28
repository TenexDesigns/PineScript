// "Volatility ZigZag" by brettkind; Dec 12, 2023
//@version=5
indicator("Volatility ZigZag", "V-ZZ", true)
// ___________________________________________________________________________________________________

// inputs
SOURCE = input.source(close, "Source")
// dynamic length
len_def = input.bool(true, "default Length [1 Year]", inline="length")
len_cus = input.int(200, " | Custom", 10, inline="length")
// default Year
length = len_def? (timeframe.ismonthly? 12 : timeframe.isweekly? 52 : timeframe.isdaily?  251 : timeframe.isintraday ? 300 : 300) : len_cus
length := math.min(length, bar_index + 1)
// use the two-sided standard deviation
two_stdev = input.bool(false, "two-sided standard deviation | ", inline = "dev")
// min deviation
min_dev_input = input.float(0.00, "min-deviation (%)", minval = 0, maxval = 100, step = 1, inline = "dev")
// ZigZag Line Color
ZigZag = input.bool(true, "ZigZag", group = "ZigZag Styling", inline = "ZigZag Styling")
zz_color = input.color(color.yellow, " | Color", group = "ZigZag Styling", inline = "ZigZag Styling")
zz_width = input.int(1, "Line Width", group = "ZigZag Styling", inline = "ZigZag Styling", minval = 0, maxval = 10)
// percentage instead of absolute Price Change
ZZ_Label = input.bool(true, "Labels | ", group = "ZigZag-Label", inline = "1")
percentage = input.bool(true, "percentage Change", group = "ZigZag-Label", inline = "1")
pch_text = input.bool(true, "Price Change | ", group = "ZigZag-Label", inline = "2")
vol_text = input.bool(true, "Volume", group = "ZigZag-Label", inline = "2")
abs_text = input.bool(true, "absoultes | ", group = "ZigZag-Label", inline = "3")
rel_text = input.bool(true, "relatives", group = "ZigZag-Label", inline = "3")
rev_text = input.bool(true, "reversal price | ", group = "ZigZag-Label", inline = "4")
bars_text = input.bool(true, "bars", group = "ZigZag-Label", inline = "4")

// darkmode styling
darkmode = input.bool(true, "darkmode")
// ___________________________________________________________________________________________________

// functions
// Highest since-function; Nov 17, 2021
// https://www.tradingcode.net/tradingview/highest-since-condition/
GetHighestSince(condition, series=high) =>
    var float highestValueSince = na
    if condition or series > highestValueSince
        highestValueSince := series
    highestValueSince

// Lowest since-function; Nov 20, 2021
// https://www.tradingcode.net/tradingview/lowest-since-condition/
GetLowestSince(condition, series=low) =>
    var float lowestValueSince = na
    if condition or series < lowestValueSince
        lowestValueSince := series
    lowestValueSince

// Nth Root by OztheWoz; Oct 15, 2021
// https://www.tradingview.com/script/zDfDeIBk-Nth-Root/
nroot(nsrc, nlen) =>
    num = nsrc
    sqrt = math.pow(num, 1 / nlen)
    sqrt

// Function to safely retrieve an element from an array based on its index
array_get(array_id, array_index, else_con = true, else_val = 0) =>
    array.size(array_id) > array_index ? array.get(array_id, array_index) : else_con ? else_val : na
// This function ensures safe access to array elements by checking index against array size

// ___________________________________________________________________________________________________

// variables I
// ta.bb() allows series int as length
[SMA, UPPER_SMA, LOWER_SMA] = ta.bb(SOURCE, length, 1)
// standard deviation
vola = two_stdev? UPPER_SMA - LOWER_SMA : UPPER_SMA - SMA
// min deviation
min_dev = close * min_dev_input/100

// tr and atr are needed to avoid double simultaneous high/low peaks
// true range
// tr = math.max(high, na(close[1]) ? high : close[1] ) - math.min(low, na(close[1]) ? low : close[1] )
tr_highest = ta.highest(2)
tr_lowest = ta.lowest(2)
tr = (na(tr_highest) ? high : tr_highest) - (na(tr_lowest) ? low : tr_lowest)
// average true range calculated with ta.bb() (allows series int as length)
[atr, _, _] = ta.bb(tr, length, 1)
// Deviation criterion for new ZigZag
deviation = math.max(vola, tr, atr, min_dev)
// IPO Opening Price
var IPO_OPEN = open
// All Time High and Low
ATH = ta.max(high)
ATL = ta.min(low)
// Colors 
text_clr = darkmode? color.white : color.rgb(0, 0, 0)
// Querying the currency of the chart
chartCurrency = syminfo.currency

// Line coordinates
var int x1 = 0
var float Y1 = IPO_OPEN
var int x2 = 0
var float Y2 = IPO_OPEN
// ___________________________________________________________________________________________________

// Find the first/next ZigZag Pivot - Conditions
// Is ... bigger than deviation
// ... High deviation from IPO ...
ipo_high_diff = (high - IPO_OPEN) > deviation
// ... Low deviation ... ...
ipo_low_diff = (IPO_OPEN - low) > deviation

// no first ZigZag Pivot was found
var bool no_first_zigzag = true
// was the last ZigZag Pivot a high-Pivot
var bool last_zigzag_high = false
// ... a low-Pivot
var bool last_zigzag_low = false
// a new Pivot was found
new_zigzag = ta.change(last_zigzag_high) or ta.change(last_zigzag_low)
// a Pivot was updated
bool y2_changed = ta.change(Y2) != 0
bool x2_changed = ta.change(x2) != 0
update_zigzag = y2_changed or x2_changed
// ___________________________________________________________________________________________________

// find the first Zig Zag Pivot
if (ipo_low_diff or ipo_high_diff) and no_first_zigzag
    no_first_zigzag := false
    x2 := bar_index
    Y2 := ipo_high_diff ? ATH : ipo_low_diff ? ATL : 9999
    if ipo_high_diff
        last_zigzag_high := true
    if ipo_low_diff
        last_zigzag_low := true
// ___________________________________________________________________________________________________

// update or find the next Zig Zag Pivot
// update
// update high Zig Zag Pivot
new_or_update_zigzag = new_zigzag or update_zigzag
GHHS = GetHighestSince(new_or_update_zigzag)
GLLS = GetLowestSince(new_or_update_zigzag)
if last_zigzag_high and GHHS > Y2 and GHHS == high
    x2 := bar_index
    Y2 := high
// update low Zig Zag Pivot
if last_zigzag_low and GLLS < Y2 and GLLS == low
    x2 := bar_index
    Y2 := low
// find the next Zig Zag Pivot
high_zigzag_dev = GHHS - Y2
low_zigzag_dev = Y2 - GLLS
if last_zigzag_high and low_zigzag_dev > deviation and low == GLLS
    x1 := x2[1]
    Y1 := Y2[1]
    x2 := bar_index
    Y2 := low
    last_zigzag_high := false
    last_zigzag_low := true
if last_zigzag_low and high_zigzag_dev > deviation and high == GHHS
    x1 := x2[1]
    Y1 := Y2[1]
    x2 := bar_index
    Y2 := high
    last_zigzag_high := true
    last_zigzag_low := false
// ___________________________________________________________________________________________________

// ZigZag Pivot Lines
// create x/y - Arrays
var int[] x1_array = array.new_int(0)
var float[] Y1_array = array.new_float(0)

// Variables for getting the coordinates-values
var int get_x1 = 0
var float get_Y1 = open
var int get_x2 = 0
var float get_Y2 = open

// Draw the ZigZag Lines
// Index for the first Line-Point
bs_index = ta.barssince(new_zigzag) +1
if ta.change(Y1) != 0
    L1 = line.new(x1[bs_index], Y1[bs_index], x1, Y1, color = ZigZag ? zz_color : color.rgb(0, 0, 0, 100), width = zz_width)
    array.unshift(x1_array, x1) 
    array.unshift(Y1_array, Y1)
    get_x1 := line.get_x1(L1)
    get_Y1 := line.get_y1(L1)
    get_x2 := line.get_x2(L1)
    get_Y2 := line.get_y2(L1)
// Draw or Update the current ZigZag Line
if barstate.islast and ZigZag
    L2 = line.new(x1, Y1, x2, Y2, color = zz_color, width = zz_width, style = line.style_arrow_right)
// ___________________________________________________________________________________________________

// Calculate the sum and average (per bar) of the Volume
sum_vol = 0.0
// included the all bars from x1 to x2
// volume of the peak bars is included in both ZigZag-Labels before and after."
number_of_bars = get_x2 - get_x1 + 1
// Loop over the bars between x1 and x2
for i = bar_index - get_x2 to bar_index - get_x1
    sum_vol += volume[i]
avg_vol = sum_vol / number_of_bars
// sum volume array
var float[] sum_vol_array = array.new_float(0)
if ta.change(sum_vol) != 0
    array.unshift(sum_vol_array, sum_vol)
// average volume array
var float[] avg_vol_array = array.new_float(0)
if ta.change(avg_vol) != 0
    array.unshift(avg_vol_array, avg_vol)

// Calculate the price change between Y1 and Y2
price_change = percentage? (get_Y2 / get_Y1 - 1) * 100 : (get_Y2 - get_Y1)
var float[] price_change_array = array.new_float(0)
if ta.change(price_change) != 0
    array.unshift(price_change_array, price_change)

// Calculate the average price change between Y1 and Y2
// growth rate per bar
exp_price_change_rel = math.max(get_x2 - get_x1, 1)
avg_price_change_rel = (math.pow( price_change / 100 + 1 , 1 / exp_price_change_rel) - 1) * 100
// Alternative
// false/simple average percentage growth
// avg_price_change_rel = price_change / (get_x2 - get_x1 +1)
avg_price_change_abs = price_change / (get_x2 - get_x1 +1)
avg_price_change = percentage ? avg_price_change_rel : avg_price_change_abs
var float[] avg_price_change_array = array.new_float(0)
if ta.change(avg_price_change) != 0
    array.unshift(avg_price_change_array, avg_price_change)

// ___________________________________________________________________________________________________

// Label Text
// Label price change
pct_text = percentage ? "%" : ""
plus = last_zigzag_low ? "+" : ""
price_change_text = pch_text and abs_text ? plus + str.tostring(math.round( array_get(price_change_array, 0) , 2 ) ) + pct_text + (rel_text ? " | " : "") : ""
avg_price_change_text = pch_text and rel_text ? plus + str.tostring(math.round( array_get(avg_price_change_array, 0) , 2 ) ) + pct_text : ""
pch_nbr = pch_text ? "\n" : ""
// Label volume
sum_vol_array_text = vol_text and abs_text ? str.tostring(array_get(sum_vol_array, 0) , format.volume) + (rel_text ? " | " : "") : ""
avg_vol_array_text = vol_text and rel_text ? str.tostring(array_get(avg_vol_array, 0) , format.volume)  : ""
vol_nbr = vol_text and (bars_text or rev_text)? "\n" : ""
// Label number of bars
number_of_bars_text = bars_text ? str.tostring( number_of_bars ) + " Bars" : ""
// Label Reversal Price
rev_price_text = rev_text ? str.tostring( math.round( array_get(Y1_array, 0), 2 ) ) + " " + chartCurrency + (bars_text ? " | " : "") : ""
// ... Gap filler ...
// vol_abs_rel_txt = vol_text and abs_text and rel_text ? " | " : ""

// Final Label
label_text = price_change_text + avg_price_change_text + pch_nbr + 
             sum_vol_array_text + avg_vol_array_text + vol_nbr + 
             rev_price_text + number_of_bars_text
// ___________________________________________________________________________________________________

// Draw Labels
if ZZ_Label and ta.change(x1) != 0
    myLabel = label.new(x1, Y1, label_text, color = color.rgb(0, 0, 0, 100), size=size.small)
    label.set_textcolor(myLabel, text_clr)
    if last_zigzag_low
        label.set_style(myLabel, label.style_label_down)
    else if last_zigzag_high
        label.set_style(myLabel, label.style_label_up)

// Draw Symbol Points
if ZigZag and ta.change(x1) != 0
    points = label.new(x1, Y1, "⦿", textcolor = zz_color, color = color.rgb(0, 0, 0, 100), size = size.tiny, style = label.style_label_center)

// Deviation H-Line
// if high or low crossover this line a new ZigZag Pivot will emerge
new_zz_h_price = last_zigzag_high ? Y2 - deviation : Y2 + deviation
x_ = math.round( (bar_index - x1) / 3 )
if ZigZag and barstate.islast
    new_zz_h_line = line.new(bar_index - x_, new_zz_h_price, bar_index + x_, new_zz_h_price, color = color.gray)
// ___________________________________________________________________________________________________

// data_window plots
plot(deviation, "deviation", display = display.data_window, editable = false)
plot(last_zigzag_high ? 1 : 0, "last ZigZag was high", display = display.data_window, editable = false)
plot(last_zigzag_low ? 1 : 0, "last ZigZag was low", display = display.data_window, editable = false)
