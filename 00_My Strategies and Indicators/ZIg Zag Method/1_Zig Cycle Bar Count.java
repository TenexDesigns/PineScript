// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © Trader_Morry

//@version=5

////////
// Title 
indicator('ZigCycleBarCount', 'ZigCyclBarCnt', true, format.price, max_labels_count=51, max_lines_count=50)


////////
// Input values 
// [
Depth = input.int(12, 'Depth', minval=1, step=1)
Deviation = input.int(5, 'Deviation', minval=1, step=1)
Backstep = input.int(3, 'Backstep', minval=2, step=1)
line_thick = input.int(2, 'Line Thickness', minval=1, maxval=4)
upcolor = input(color.aqua, 'Bull Color')
dncolor = input(color.red, 'Bear Color')
repaint = input(true, 'Repaint Levels')
showHHLL = input(false, 'Show HH/LL')
price = input(true, 'Price Values')
Label_Style = input.string(title='Label Style', defval='TEXT', options=['BALLOON', 'TEXT'])
// ]

////////
// Hidden input values
// [
// ]

//////// 
// Making
// [
var last_h = 1
last_h += 1
var last_l = 1
last_l += 1
var lw = 1
var hg = 1
lw += 1
hg += 1
p_lw = -ta.lowestbars(Depth)
p_hg = -ta.highestbars(Depth)
lowing = lw == p_lw or low - low[p_lw] > Deviation * syminfo.mintick
highing = hg == p_hg or high[p_hg] - high > Deviation * syminfo.mintick
lh = ta.barssince(not highing[1])
ll = ta.barssince(not lowing[1])
down = ta.barssince(not(lh > ll)) >= Backstep
lower = low[lw] > low[p_lw]
higher = high[hg] < high[p_hg]
if lw != p_lw and (not down[1] or lower)
    lw := p_lw < hg ? p_lw : 0
    lw
if hg != p_hg and (down[1] or higher)
    hg := p_hg < lw ? p_hg : 0
    hg

line zz = na
label point = na
x1 = down ? lw : hg
y1 = down ? low[lw] : high[hg]

var zigBarCount = 0  // once at init
var t_zigBarCount = 0  // once at init

if down == down[1]
    if repaint
        label.delete(point[1])
        line.delete(zz[1])
    down
if down != down[1]
    if down
        last_h := hg
        last_h
    else
        last_l := lw
        last_l
    if not repaint
        nx = down ? last_h : last_l
        zigBarCount := bar_index - nx - (bar_index - (down ? last_l : last_h))
        zz := line.new(bar_index - nx, down ? high[nx] : low[nx], bar_index - (down ? last_l : last_h), down ? low[last_l] : high[last_h], width=line_thick, color=down ? upcolor : dncolor)
        // Making text
        string _text = ""
        if showHHLL
            _text := down ? high[nx] > high[last_h[1]] ? 'HH' : 'LH' : low[nx] < low[last_l[1]] ? 'LL' : 'HL'
            _text := _text + '\n'
        if price
            _text := down ? _text + str.tostring(high[nx]) + '\n' + 'C=' + str.tostring(zigBarCount) : _text + str.tostring(low[nx]) + '\n' + 'C=' + str.tostring(zigBarCount)
        else
            _text := _text + str.tostring(zigBarCount)
            
        // Show label
        if Label_Style == 'BALLOON'
            point := label.new(bar_index - nx, down ? high[nx] : low[nx], _text, style=down ? label.style_label_down : label.style_label_up, size=size.normal, color=down ? upcolor : dncolor, textcolor=color.black, tooltip=down ? high[nx] > high[last_h[1]] ? 'Higher High' : 'Lower High' : low[nx] < low[last_l[1]] ? 'Lower Low' : 'Higher Low')
            point
        else
            point := label.new(bar_index - nx, na, _text, style=label.style_none, yloc=down ? yloc.abovebar : yloc.belowbar, size=size.normal, color=down ? upcolor : dncolor, textcolor=color.blue, tooltip=down ? high[nx] > high[last_h[1]] ? 'Higher High' : 'Lower High' : low[nx] < low[last_l[1]] ? 'Lower Low' : 'Higher Low')
            point
    down
if repaint
    // last_h:last_lのどちらかが0なら縦に線を引くように変更する
//    if (last_h==0) or (last_l==0)
//        zigBarCount := (bar_index-x1) - (bar_index-(down?last_h:last_l))-1
//        if zigBarCount < 0
//            zz := line.new(bar_index-(down?last_h:last_l)-1, down ? high[last_h]-1 : low[last_l]-1, bar_index-x1, y1, width=line_thick, color=down?dncolor:upcolor)
//            t_zigBarCount := 0
//        else
//            zz := line.new(bar_index-(down?last_h:last_l), down ? high[last_h]-1 : low[last_l]-1, bar_index-x1, y1, width=line_thick, color=down?dncolor:upcolor)
//            t_zigBarCount := zigBarCount
//    else
    zigBarCount := bar_index - x1 - (bar_index - (down ? last_h : last_l))
    if zigBarCount < 0
        zz := line.new(bar_index - (down ? last_h : last_l) - 1, down ? high[last_h] : low[last_l], bar_index - x1, y1, width=line_thick, color=down ? dncolor : upcolor)
        t_zigBarCount := 0
        t_zigBarCount
    else
        zz := line.new(bar_index - (down ? last_h : last_l), down ? high[last_h] : low[last_l], bar_index - x1, y1, width=line_thick, color=down ? dncolor : upcolor)
        t_zigBarCount := zigBarCount
        t_zigBarCount

    zigBarCount := t_zigBarCount

    // Making text
    string _text = ""
    if showHHLL
        _text := down ? low[x1] < low[last_l] ? 'LL' : 'HL' : high[x1] > high[last_h] ? 'HH' : 'LH'
        _text := _text + '\n'
    if price
        _text := down ? _text + str.tostring(low[x1]) + '\n' + 'C=' + str.tostring(zigBarCount) : _text + str.tostring(high[x1]) + '\n' + 'C=' + str.tostring(zigBarCount)
    else
        _text := _text + str.tostring(zigBarCount)

    // Show label
    if Label_Style == 'BALLOON'
        point := label.new(bar_index - x1, y1, _text, style=down ? label.style_label_up : label.style_label_down, size=size.normal, color=down ? dncolor : upcolor, textcolor=color.black, tooltip=down ? low[x1] < low[last_l] ? 'Lower Low' : 'Higher Low' : high[x1] > high[last_h] ? 'Higher High' : 'Lower High')
        point
    else
        point := label.new(bar_index - x1, na, _text, style=label.style_none, yloc=down ? yloc.belowbar : yloc.abovebar, size=size.normal, color=down ? dncolor : upcolor, textcolor=color.blue, tooltip=down ? low[x1] < low[last_l] ? 'Lower Low' : 'Higher Low' : high[x1] > high[last_h] ? 'Higher High' : 'Lower High')
        point
// ]


////////
// Bull or Bear notifications
// [
bear = down
//bgcolor(bear?color.red:color.lime, title="Scanning Direction")

alertcondition(bear != bear[1], 'Direction Changed', 'Zigzag on {{ticker}} direction changed at {{time}}')
alertcondition(bear != bear[1] and not bear, 'Bullish Direction', 'Zigzag on {{ticker}} bullish direction at {{time}}')
alertcondition(bear != bear[1] and bear, 'Bearish Direction', 'Zigzag on {{ticker}} bearish direction at {{time}}')
// ]

