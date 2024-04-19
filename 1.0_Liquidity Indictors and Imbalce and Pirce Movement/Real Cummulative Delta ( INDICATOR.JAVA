Real Cummulative Delta (New TV Function)
GioLucaas


https://in.tradingview.com/script/SEU88K1z/









//@version=5
indicator("Cummulative Delta", "Delta", format=format.volume)

lowerTimeframeTooltip = "The indicator uses the new Up/Down Volume native indicator on TradingView to calculate the Cummulative Delta based on actual buyer/seller aggression.\n\nHigher timeframes provide more historical data, but the data will be less precise."
barsBackTooltip = "Some higher values only work in lower timeframes"

reset = input.bool(true, "Reset the cummulative Delta at start of day/month/year?", group="Reseting")
barsBack = input.int(300, "Start calculating how many bars back?", tooltip = barsBackTooltip, group="Reseting")

useCustomTimeframeInput = input.bool(false, "Use custom timeframe", tooltip = lowerTimeframeTooltip, group="Custom TF")
lowerTimeframeInput = input.timeframe("1", "Timeframe", group="Custom TF")

upAndDownVolume() =>
    posVol = 0.0
    negVol = 0.0
    
    switch
        close >  open     => posVol += volume
        close <  open     => negVol -= volume
        close >= close[1] => posVol += volume
        close <  close[1] => negVol -= volume

    [posVol, negVol]

lowerTimeframe = switch
    useCustomTimeframeInput => lowerTimeframeInput
    timeframe.isintraday    => "1"
    timeframe.isdaily       => "5"
    => "60"

[upVolumeArray, downVolumeArray] = request.security_lower_tf(syminfo.tickerid, lowerTimeframe, upAndDownVolume())

upVolume = array.sum(upVolumeArray)
downVolume = array.sum(downVolumeArray)
delta = upVolume + downVolume

dayStart = (dayofmonth[1] != dayofmonth)
monthStart = (month[1] != month)
yearStart = (year[1] != year)

// cumulative delta reset at globex and rth openings
cumdelta = 0.0
cumdelta := (reset == false and (bar_index == last_bar_index - barsBack)) or (reset == true and (((timeframe.isweekly or timeframe.ismonthly) and yearStart) or (timeframe.isdaily and monthStart) or (timeframe.isintraday and dayStart))) ? delta : cumdelta[1]+delta

// getting ohlc values to plot the cumulative delta candles.
float o = na
float h = na
float l = na
float c = na
o := (reset == false and (bar_index == last_bar_index - barsBack)) or (reset == true and (((timeframe.isweekly or timeframe.ismonthly) and yearStart) or (timeframe.isdaily and monthStart) or (timeframe.isintraday and dayStart))) ? 0 : cumdelta[1]
h := ta.highest(cumdelta, 1)
l := ta.lowest(cumdelta, 1)
c := cumdelta

// plotting cumulative delta as candles. Two 'plotcandle' calls to set wickcolor/bordercolor 'dinamically' setting 'o' to 'na' when needed
plotcandle(o<=c?o:na, h, l, c, title='Cumulative Delta Green Candle', color = color.green,wickcolor=color.green,bordercolor=color.green)
plotcandle(o>=c?o:na, h, l, c, title='Cumulative Delta Red Candle', color = color.red,wickcolor=color.red,bordercolor=color.red)

var cumVol = 0.
cumVol += nz(volume)
if barstate.islast and cumVol == 0
    runtime.error("The data vendor doesn't provide volume data for this symbol.")
