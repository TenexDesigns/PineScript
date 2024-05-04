// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © BalintDavid

// WHAT IT DOES AND HOW TO USE:
// In the Input page you configure the RSI
// 
// The indicator draws swings on the chart based on RSI extremes
// Example: Lines are draws from OVERSOLD to OVERBOUGHT and vice-versa
// If we keep geing in deeper OVERBOUGHT or OVERSOLD, the swinglines follow the price, till another cycle is complete


//@version=4
study("RSI Market Structure", overlay=true, max_bars_back=1000)

// RSI Settings for user
rsiSource = input(title="RSI Source", type=input.source, defval=close)
rsiLength = input(title="RSI Length", type=input.integer, defval=7)
rsiOverbought = input(title="RSI Overbought", type=input.integer, defval=70, minval=50, maxval=100)
rsiOvesold = input(title="RSI Oversold", type=input.integer, defval=30, minval=1, maxval=50)

// RSI value based on inbuilt RSI
rsiValue = rsi(rsiSource, rsiLength)

// Get the current state
isOverbought = rsiValue >= rsiOverbought
isOversold = rsiValue <= rsiOvesold

// State of the last extreme 0 for initialization, 1 = overbought, 2 = oversold
var laststate = 0

// Highest and Lowest prices since the last state change
var hh = low
var ll = high

// Labels
var label labelll = na
var label labelhh = na

// Swing lines
var line line_up = na
var line line_down = na


// We go from overbought straight to oversold  NEW DRAWINGS CREATED HERE
if(laststate == 1 and isOversold)
    ll := low
    labelll := label.new(bar_index, low, style=label.style_label_up, color=color.green, size=size.tiny)
    labelhh_low = label.get_x(labelhh)
    labelhh_pos = label.get_y(labelhh)
    line_down := line.new(bar_index, high, labelhh_low, labelhh_pos, width=2)

// We go from oversold straight to overbought NEW DRAWINGS CREATED HERE
if(laststate == 2 and isOverbought)
    hh := high
    labelhh := label.new(bar_index, high, style=label.style_label_down, color=color.red, size=size.tiny)
    labelll_low = label.get_x(labelll)
    labelll_pos = label.get_y(labelll)
    line_up := line.new(bar_index, high, labelll_low, labelll_pos, width=2)
    

// If we are overbought
if(isOverbought)
    if(high >= hh)
        hh := high
        label.set_x(labelhh, bar_index)
        label.set_y(labelhh, high)
        line.set_x1(line_up, bar_index)
        line.set_y1(line_up, high)
    laststate := 1
    
// If we are oversold
if(isOversold)
    if(low <= ll)
        ll := low
        label.set_x(labelll, bar_index)
        label.set_y(labelll, low)
        line.set_x1(line_down, bar_index)
        line.set_y1(line_down, low)
    laststate := 2
    
    
// If last state was overbought and we are overbought
if(laststate == 1 and isOverbought)
    if(hh <= high)
        hh := high
        label.set_x(labelhh, bar_index)
        label.set_y(labelhh, high)
        line.set_x1(line_up, bar_index)
        line.set_y1(line_up, high)
    
//If we are oversold and the last state was oversold, move the drawings to the lowest price
if(laststate == 2 and isOversold)
    if(low <= ll)
        ll := low
        label.set_x(labelll, bar_index)
        label.set_y(labelll, low)
        line.set_x1(line_down, bar_index)
        line.set_y1(line_down, low)


// If last state was overbought
if(laststate == 1)
    if(hh <= high)
        hh := high
        label.set_x(labelhh, bar_index)
        label.set_y(labelhh, high)
        line.set_x1(line_up, bar_index)
        line.set_y1(line_up, high)
        
// If last stare was oversold
if(laststate == 2)
    if(ll >= low)
        ll := low
        label.set_x(labelll, bar_index)
        label.set_y(labelll, low)
        line.set_x1(line_down, bar_index)
        line.set_y1(line_down, low)
