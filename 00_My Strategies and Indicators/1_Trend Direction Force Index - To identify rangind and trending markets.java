//@version=3
study("Trend Direction Force Index", shorttitle="TDFI")

trendPeriod = input(title="Trend Period", defval=20)
maType = input(title="MA Type", defval="JMA",
			 options=["DEMA",
			 "EMA",
			 "HMA",
			 "JMA",
			 "LSMA",
			 "PWMA",
			 "SMMA",
			 "SMA",
			 "SSF2",
			 "SWMA",
			 "TEMA",
			 "TMA",
			 "VWMA",
			 "WMA",
			 "ZLEMA"])
maPeriod = input(title="MA Period", type=integer, defval=8)
triggerUp = input(title="TriggerUp", defval=0.05)
triggerDown = input(title="TriggerDown", defval=-0.05)
showZeroCrosses = input(title="Show zero cross signals", defval=true)
showLineCrosses = input(title="Show level cross signals", defval=false)

PI = 2 * asin(1)
mma = ema(close, trendPeriod)
smma = ema(mma, trendPeriod)

impetmma = change(mma)
impetsmma = change(smma)
divma = abs(mma - smma) / syminfo.mintick
averimpet = avg(impetmma, impetsmma) / (2 * syminfo.mintick)

tdfRaw = divma * pow(averimpet, 3)
tdfAbsRaw = abs(tdfRaw)

for i = 1 to 3 * trendPeriod - 1
    cand = abs(nz(tdfRaw[i]))
    tdfAbsRaw := cand > tdfAbsRaw ? cand : tdfAbsRaw

ratio = tdfRaw / tdfAbsRaw
smooth = na

//------------------------------------------------------------------------------
// Super Smoother

_ssf2(src, length) =>
	arg = sqrt(2) * PI / length
	a1 = exp(-arg)
	b1 = 2 * a1 * cos(arg)
	c2 = b1
	c3 = -pow(a1, 2)
	c1 = 1 - c2 - c3
	ssf = 0.0
	ssf := c1 * src + c2 * nz(ssf[1], nz(src[1], src)) + c3 * nz(ssf[2], nz(src[2], nz(src[1], src)))

smooth := maType == "SSF2" ? _ssf2(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// DEMA

_dema(src, length) =>
	ema1 = ema(src, length)
	ema2 = ema(ema1, length)
	2 * ema1 - ema2

smooth := maType == "DEMA" ? _dema(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// EMA

smooth := maType == "EMA" ? ema(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// HMA, Hull

_hma(src, length) =>
	wma(2 * wma(src, length / 2) - wma(src, length), round(sqrt(length)))

smooth := maType == "HMA" ? _hma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// LSMA, Least Squares

smooth := maType == "LSMA" ? linreg(ratio, maPeriod, 0) : smooth

//------------------------------------------------------------------------------
// PWMA, Parabolic Weighted

_pwma(src, length) =>
	sum = 0.0
	weightSum = 0.0
	for i = 0 to length - 1
		weight = pow(length - i, 2)
		sum := sum + nz(src[i]) * weight
		weightSum := weightSum + weight
	sum / weightSum

smooth := maType == "PWMA" ? _pwma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// SMMA

smooth := maType == "SMMA" ? rma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// SMA

smooth := maType == "SMA" ? sma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// SWMA

_swma(src, length) =>
	sum = 0.0
	weightSum = 0.0
	for i = 0 to length - 1
		weight = sin(i * PI / (length + 1))
		sum := sum + nz(src[i]) * weight
		weightSum := weightSum + weight
	sum / weightSum

smooth := maType == "SWMA" ? _swma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// TEMA

_tema(src, length) =>
	ema1 = ema(src, length)
	ema2 = ema(ema1, length)
	ema3 = ema(ema2, length)
	3 * (ema1 - ema2) + ema3

smooth := maType == "TEMA" ? _tema(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// TMA

_tma(src, length) =>
	sma(sma(src, ceil(length / 2)), floor(length / 2) + 1)

smooth := maType == "TMA" ? _tma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// VWMA

smooth := maType == "VWMA" ? vwma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// WMA

smooth := maType == "WMA" ? wma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// ZLEMA

_zlema(src, length) =>
	lag = length <= 2 ? 1 : floor((length - 1) / 2)
	ema(src + (src - nz(src[lag])), length)

smooth := maType == "ZLEMA" ? _zlema(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------
// JMA

_jma(src, length) =>
	alpha = 0.45 * (length - 1) / (0.45 * (length - 1) + 2)
	out = 0.0
	e0 = 0.0
	e0 := (1 - alpha) * src + alpha * nz(e0[1])
	e1 = 0.0
	e1 := (1 - alpha) * (src - e0) + alpha * nz(e1[1])
	e2 = 0.0
	e2 := pow(1 - alpha, 2) * (e0 + e1 - nz(out[1])) + pow(alpha, 2) * nz(e2[1])
	out := e2 + nz(out[1])

smooth := maType == "JMA" ? _jma(ratio, maPeriod) : smooth

//------------------------------------------------------------------------------

tdf = tdfAbsRaw > 0 ? max(min(na(smooth) ? ratio : smooth, 1), -1) : 0.0

tdfColor = tdf > triggerUp ? #0ebb23 : tdf < triggerDown ? red : gray
plot(tdf, linewidth=2, color=tdfColor, transp=0)
hline(triggerUp, title="Trigger Up", linestyle=dotted)
hline(triggerDown, title="Trigger Down", linestyle=dotted)

plotshape(crossover(tdf, 0) and showZeroCrosses ? 0 : na, title="Zero Crossover", location=location.absolute, style=shape.circle, size=size.tiny, color=green, transp=0)
plotshape(crossunder(tdf, 0) and showZeroCrosses ? 0 : na, title="Zero Crossunder", location=location.absolute, style=shape.circle, size=size.tiny, color=red, transp=0)

plotshape(crossover(tdf, triggerUp) and showLineCrosses ? triggerUp : na, title="Upper Crossover", location=location.absolute, style=shape.circle, size=size.tiny, color=green, transp=0)
plotshape(crossunder(tdf, triggerUp) and showLineCrosses ? triggerUp : na, title="Upper Crossunder", location=location.absolute, style=shape.circle, size=size.tiny, color=red, transp=0)
plotshape(crossover(tdf, triggerDown) and showLineCrosses ? triggerDown : na, title="Lower Crossover", location=location.absolute, style=shape.circle, size=size.tiny, color=green, transp=0)
plotshape(crossunder(tdf, triggerDown) and showLineCrosses ? triggerDown : na, title="Lower Crossunder", location=location.absolute, style=shape.circle, size=size.tiny, color=red, transp=0)

alertcondition(crossover(tdf, 0), title="Cross zero line up", message="TDF crossed zero line up!")
alertcondition(crossunder(tdf, 0), title="Cross zero line down", message="TDF crossed zero line down!")
alertcondition(crossover(tdf, triggerUp), title="Cross upper line up", message="TDF crossed upper line up!")
alertcondition(crossunder(tdf, triggerUp), title="Cross upper line down", message="TDF crossed upper line down!")
alertcondition(crossover(tdf, triggerDown), title="Cross lower line up", message="TDF crossed lower line up!")
alertcondition(crossunder(tdf, triggerDown), title="Cross lower line down", message="TDF crossed lower line down!")
