
   strategy(title="Range Filter Buy and Sell 3hr", shorttitle="Range Filter", overlay=true)

// Color variables
upColor   = color.white
midColor = #90bff9
downColor = color.blue

// Source
src = input(defval=close, title="Source")

// Sampling Period
per = input.int(defval=100, minval=1, title="Sampling Period")

// Range Multiplier
mult = input.float(defval=3.0, minval=0.1, title="Range Multiplier")

// Smooth Average Range
smoothrng(x, t, m) =>
    wper = t * 2 - 1
    avrng = ta.ema(math.abs(x - x[1]), t)
    smoothrng = ta.ema(avrng, wper) * m

// Calculate smoothrng for the current 3-hour timeframe
// Assuming smoothrng function exists and is correctly defined
smrng = smoothrng(src, timeframe.multiplier * 3, mult)

// Range Filter
rngfilt(x, r) =>
    rngfilt = x
    rngfilt := x > nz(rngfilt[1]) ? x - r < nz(rngfilt[1]) ? nz(rngfilt[1]) : x - r : 
       x + r > nz(rngfilt[1]) ? nz(rngfilt[1]) : x + r
    rngfilt
filt = rngfilt(src, smrng)

// Filter Direction
upward = 0.0
upward := filt > filt[1] ? nz(upward[1]) + 1 : filt < filt[1] ? 0 : nz(upward[1])
downward = 0.0
downward := filt < filt[1] ? nz(downward[1]) + 1 : filt > filt[1] ? 0 : nz(downward[1])

// Target Bands
hband = filt + smrng
lband = filt - smrng

// Colors
filtcolor = upward > 0 ? upColor : downward > 0 ? downColor : midColor
barcolor = src > filt and src > src[1] and upward > 0 ? upColor :
   src > filt and src < src[1] and upward > 0 ? upColor : 
   src < filt and src < src[1] and downward > 0 ? downColor : 
   src < filt and src > src[1] and downward > 0 ? downColor : midColor

filtplot = plot(filt, color=filtcolor, linewidth=2, title="Range Filter")

// Target
hbandplot = plot(hband, color=color.new(upColor, 70), title="High Target")
lbandplot = plot(lband, color=color.new(downColor, 70), title="Low Target")

// Fills
fill(hbandplot, filtplot, color=color.new(upColor, 90), title="High Target Range")
fill(lbandplot, filtplot, color=color.new(downColor, 90), title="Low Target Range")

// Bar Color
barcolor(barcolor)

// Break Outs
longCond = bool(na)
shortCond = bool(na)
longCond := src > filt and src > src[1] and upward > 0 or 
   src > filt and src < src[1] and upward > 0
shortCond := src < filt and src < src[1] and downward > 0 or 
   src < filt and src > src[1] and downward > 0

CondIni = 0
CondIni := longCond ? 1 : shortCond ? -1 : CondIni[1]
longCondition = longCond and CondIni[1] == -1
shortCondition = shortCond and CondIni[1] == 1

//Alerts
plotshape(longCondition, title="Buy Signal", text="Buy", textcolor=color.white, style=shape.labelup, size=size.small, location=location.belowbar, color=color.new(#aaaaaa, 20))
plotshape(shortCondition, title="Sell Signal", text="Sell", textcolor=color.white, style=shape.labeldown, size=size.small, location=location.abovebar, color=color.new(downColor, 20))

alertcondition(longCondition, title="Buy alert on Range Filter", message="Buy alert on Range Filter")
alertcondition(shortCondition, title="Sell alert on Range Filter", message="Sell alert on Range Filter")
alertcondition(longCondition or shortCondition, title="Buy and Sell alert on Range Filter", message="Buy and Sell alert on Range Filter")

// For use as a Strategy
grp_STRAT   = "Strategy settings"
timeInput   = input.time(timestamp("1 Nov 2022 00:00 +0000"), title="Start date", group=grp_STRAT)

timePeriod = time >= timeInput
notInTrade = strategy.position_size == 0

if(longCondition and timePeriod and notInTrade)
    strategy.entry("Long", strategy.long)

if(shortCondition and timePeriod and notInTrade)
    strategy.entry("Short", strategy.short)

if(longCondition[1] and strategy.position_size < 0)
    strategy.close("Short","Exit short")
    strategy.entry("Long", strategy.long)
if(shortCondition[1] and strategy.position_size > 0)
    strategy.close("Long","Exit long")
    strategy.entry("Short", strategy.short)

// Trailing Stop Loss
trailingStopLossPips = 40
var float lastStopLossPrice = na

if (strategy.position_size > 0)
    if (na(lastStopLossPrice) or close > lastStopLossPrice + trailingStopLossPips * syminfo.mintick)
        lastStopLossPrice := close - trailingStopLossPips * syminfo.mintick
    if (low < lastStopLossPrice)
        strategy.close("Long", "Trailing Stop Loss")

if (strategy.position_size < 0)
    if (na(lastStopLossPrice) or close < lastStopLossPrice - trailingStopLossPips * syminfo.mintick)
        lastStopLossPrice := close + trailingStopLossPips * syminfo.mintick
    if (high > lastStopLossPrice)
        strategy.close("Short", "Trailing Stop Loss")



-------------------------------------------------------

          Yes, the code achieves your intentions. Here's how:

1. **3-Hour Time Frame Signals**: The script is designed to generate buy and sell signals based on the 3-hour time frame. This is accomplished by using `timeframe.multiplier * 3` when calculating the `smoothrng` for the range filter.

2. **Closing Trades Based on Opposing Signals**: When a new signal opposite to the current position appears on the 3-hour time frame, the script closes the current position and opens a new one in the opposite direction. This is achieved through the `strategy.close` and `strategy.entry` functions inside the conditions `if(longCondition[1] and strategy.position_size < 0)` and `if(shortCondition[1] and strategy.position_size > 0)`.

3. **Trailing Stop Loss of 40 Pips**: The script implements a trailing stop loss of 40 pips. For long positions, if the price drops below the trailing stop loss level, the position is closed. For short positions, if the price rises above the trailing stop loss level, the position is closed. This is achieved using the `trailingStopLossPips` variable and the logic within the `if (strategy.position_size > 0)` and `if (strategy.position_size < 0)` blocks.

Overall, the code appears to align with your intentions. If you have any specific questions or need further clarification on any part of the code, feel free to ask!










