// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © loxx

//@version=5
indicator("One-Sided Gaussian Filter w/ Channels [Loxx]", 
     shorttitle="OSGFC [Loxx]", 
     overlay = true, 
     timeframe="", 
     timeframe_gaps = true)

import loxx/loxxexpandedsourcetypes/4

greencolor = #2DD204
redcolor = #D2042D 

//ehlers 2-pole super smoother
_twopoless(float src, int len)=>
    a1 = 0., b1 = 0.
    coef1 = 0., coef2 = 0., coef3 = 0.
    filt = 0., trig = 0.
    a1 := math.exp(-1.414 * math.pi / len)
    b1 := 2 * a1 * math.cos(1.414 * math.pi / len)
    coef2 := b1
    coef3 := -a1 * a1
    coef1 := 1 - coef2 - coef3
    filt := coef1 * src + coef2 * nz(filt[1]) + coef3 * nz(filt[2])
    filt := bar_index < 3 ? src : filt
    filt
    
_gaussian(size, x)=>
    out = (math.exp(-x * x * 9 / ((size + 1) * (size + 1))))
    out

//calc fibonacci numbers 0, 1, 1, 2, 3, 5, 8, 13, 21 ... etc
_fiblevels(len)=>
    arr_levels = array.new_float(len, 0.)
    t1 = 0, t2 = 1
    nxt = t1 + t2
    for i = 0 to len - 1 
        array.set(arr_levels, i, nxt)
        t1 := t2
        t2 := nxt
        nxt := t1 + t2
    arr_levels
    
//calc weights given fibo numbers and how many fibos chosen
_gaussout(levels)=>
    perin = array.size(levels)
    arr_gauss = matrix.new<float>(perin, perin, 0.)
    for k = 0 to array.size(levels) - 1 
        sum = 0.
        for i = 0 to perin - 1 
            if (i >= array.get(levels, k)) 
                break
            matrix.set(arr_gauss, i, k, _gaussian(array.get(levels, k), i))
            sum += matrix.get(arr_gauss, i, k)
        for i = 0 to perin - 1
            if (i >= array.get(levels, k)) 
                break
            temp = matrix.get(arr_gauss, i, k) / sum
            matrix.set(arr_gauss, i, k, temp)
    arr_gauss   

//calc moving average applying fibo numbers
_smthMA(level, src, per)=>
    sum = 0.
    lvltemp = _fiblevels(per)
    gtemp = _gaussout(lvltemp)
    for i = 0 to matrix.rows(gtemp) - 1
        sum += matrix.get(gtemp, i, level) * nz(src[i])
    sum

smthtype = input.string("Kaufman", "Heiken-Ashi Better Smoothing", options = ["AMA", "T3", "Kaufman"], group=  "Source Settings")

srcoption = input.string("Close", "Source", group= "Source Settings", 
     options = 
     ["Close", "Open", "High", "Low", "Median", "Typical", "Weighted", "Average", "Average Median Body", "Trend Biased", "Trend Biased (Extreme)", 
     "HA Close", "HA Open", "HA High", "HA Low", "HA Median", "HA Typical", "HA Weighted", "HA Average", "HA Average Median Body", "HA Trend Biased", "HA Trend Biased (Extreme)",
     "HAB Close", "HAB Open", "HAB High", "HAB Low", "HAB Median", "HAB Typical", "HAB Weighted", "HAB Average", "HAB Average Median Body", "HAB Trend Biased", "HAB Trend Biased (Extreme)"])
     
smthper = input.int(10, "Guassian Levels Depth", maxval = 100, group=  "Basic Settings")
extrasmthper = input.int(10, "Extra Smoothing (2-Pole Ehlers Super Smoother) Period", group=  "Basic Settings")

atrper = input.int(21, "ATR Period", group = "Basic Settings")
mult = input.float(.628, "ATR Multiplier", group = "Basic Settings")

colorbars = input.bool(true, "Color bars?", group = "UI Options")
showsignals = input.bool(true, "Show signals?", group = "UI Options")


kfl=input.float(0.666, title="* Kaufman's Adaptive MA (KAMA) Only - Fast End", group = "Moving Average Inputs")
ksl=input.float(0.0645, title="* Kaufman's Adaptive MA (KAMA) Only - Slow End", group = "Moving Average Inputs")
amafl = input.int(2, title="* Adaptive Moving Average (AMA) Only - Fast", group = "Moving Average Inputs")
amasl = input.int(30, title="* Adaptive Moving Average (AMA) Only - Slow", group = "Moving Average Inputs")

haclose = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, close)
haopen = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, open)
hahigh = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, high)
halow = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, low)
hamedian = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, hl2)
hatypical = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, hlc3)
haweighted = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, hlcc4)
haaverage = request.security(ticker.heikinashi(syminfo.tickerid), timeframe.period, ohlc4)

float src = switch srcoption
	"Close" => loxxexpandedsourcetypes.rclose()
	"Open" => loxxexpandedsourcetypes.ropen()
	"High" => loxxexpandedsourcetypes.rhigh()
	"Low" => loxxexpandedsourcetypes.rlow()
	"Median" => loxxexpandedsourcetypes.rmedian()
	"Typical" => loxxexpandedsourcetypes.rtypical()
	"Weighted" => loxxexpandedsourcetypes.rweighted()
	"Average" => loxxexpandedsourcetypes.raverage()
    "Average Median Body" => loxxexpandedsourcetypes.ravemedbody()
	"Trend Biased" => loxxexpandedsourcetypes.rtrendb()
	"Trend Biased (Extreme)" => loxxexpandedsourcetypes.rtrendbext()
	"HA Close" => loxxexpandedsourcetypes.haclose(haclose)
	"HA Open" => loxxexpandedsourcetypes.haopen(haopen)
	"HA High" => loxxexpandedsourcetypes.hahigh(hahigh)
	"HA Low" => loxxexpandedsourcetypes.halow(halow)
	"HA Median" => loxxexpandedsourcetypes.hamedian(hamedian)
	"HA Typical" => loxxexpandedsourcetypes.hatypical(hatypical)
	"HA Weighted" => loxxexpandedsourcetypes.haweighted(haweighted)
	"HA Average" => loxxexpandedsourcetypes.haaverage(haaverage)
    "HA Average Median Body" => loxxexpandedsourcetypes.haavemedbody(haclose, haopen)
	"HA Trend Biased" => loxxexpandedsourcetypes.hatrendb(haclose, haopen, hahigh, halow)
	"HA Trend Biased (Extreme)" => loxxexpandedsourcetypes.hatrendbext(haclose, haopen, hahigh, halow)
	"HAB Close" => loxxexpandedsourcetypes.habclose(smthtype, amafl, amasl, kfl, ksl)
	"HAB Open" => loxxexpandedsourcetypes.habopen(smthtype, amafl, amasl, kfl, ksl)
	"HAB High" => loxxexpandedsourcetypes.habhigh(smthtype, amafl, amasl, kfl, ksl)
	"HAB Low" => loxxexpandedsourcetypes.hablow(smthtype, amafl, amasl, kfl, ksl)
	"HAB Median" => loxxexpandedsourcetypes.habmedian(smthtype, amafl, amasl, kfl, ksl)
	"HAB Typical" => loxxexpandedsourcetypes.habtypical(smthtype, amafl, amasl, kfl, ksl)
	"HAB Weighted" => loxxexpandedsourcetypes.habweighted(smthtype, amafl, amasl, kfl, ksl)
	"HAB Average" => loxxexpandedsourcetypes.habaverage(smthtype, amafl, amasl, kfl, ksl)
    "HAB Average Median Body" => loxxexpandedsourcetypes.habavemedbody(smthtype, amafl, amasl, kfl, ksl)
	"HAB Trend Biased" => loxxexpandedsourcetypes.habtrendb(smthtype, amafl, amasl, kfl, ksl)
	"HAB Trend Biased (Extreme)" => loxxexpandedsourcetypes.habtrendbext(smthtype, amafl, amasl, kfl, ksl)
	=> haclose

lmax = smthper + 1
out1 = _smthMA(smthper, src, lmax)
out = _twopoless(out1, extrasmthper)
sig = out[1]
colorout = out > sig ? greencolor : redcolor

atr = ta.atr(atrper)
smax = out + atr * mult
smin = out - atr * mult

plot(smax, color = bar_index % 2 ? color.silver : na, linewidth = 1)
plot(smin, color = bar_index % 2 ? color.silver : na, linewidth = 1)

plot(out, "GMA", color = colorout, linewidth = 4)
barcolor(colorbars ? colorout : na)

goLong = ta.crossover(out, sig)
goShort = ta.crossunder(out, sig)

plotshape(goLong and showsignals, title = "Long", color = color.yellow, textcolor = color.yellow, text = "L", style = shape.triangleup, location = location.belowbar, size = size.tiny)
plotshape(goShort and showsignals, title = "Short", color = color.fuchsia, textcolor = color.fuchsia, text = "S", style = shape.triangledown, location = location.abovebar, size = size.tiny)

alertcondition(goLong, title="Long", message="One-Sided Gaussian Filter w/ Channels [Loxx]: Long\nSymbol: {{ticker}}\nPrice: {{close}}")
alertcondition(goShort, title="Short", message="One-Sided Gaussian Filter w/ Channels [Loxx]: Short\nSymbol: {{ticker}}\nPrice: {{close}}")



