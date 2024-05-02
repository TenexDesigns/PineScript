// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © RozaniGhani-RG

//@version=5
indicator('Zigzag Chart Points', 'ZCP', true, max_bars_back = 500)

// 0. Inputs
// 1. Variables and arrays
// 2. Map
// 3. Switches
// 4. Methods
// 5. Executions

//#region ———————————————————— 0. Inputs
G0         = 'Main Settings'
T0         = 'Zigzag values\nDefault : 14\nMin : 2\nMax : 50'
length     = input.int(            14,      'Length', group = G0, minval = 2, maxval = 50, tooltip = T0)
colorUp    = input.color(color.lime,       'Trend', group = G0, inline = '0')
colorDn    = input.color( color.red,            '', group = G0, inline = '0')

G1         = 'Line Settings'
T1         = 'Default\nStyle : Solid\nWidth : 4'
showLine   = input.bool(         true, 'Show / hide', group = G1)
lineType   = input.string(    'solid',       'Style', group = G1, options = ['dash', 'dot', 'solid', 'arrow right', 'arrow left'])
extendType = input.string(    'none',       'Extend', group = G1, options = ['left', 'right', 'both', 'none'])
width      = input.int(             4,       'Width', group = G1, minval = 1, maxval = 4, tooltip = T1)

G2         = 'Label Settings'
T2         = 'Small font size recommended for mobile app or multiple layout'
showLabel  = input.bool(         true, 'Show / hide', group = G2)
fontSize   = input.string(   'normal',        'Size', group = G2, options = ['tiny',  'small', 'normal', 'large', 'huge'], tooltip = T2)
//#endregion

//#region ———————————————————— 1. Variables and arrays
float ph      = na,  ph := ta.highestbars(high, length) == 0 ? high : na
float pl      = na,  pl := ta.lowestbars(  low, length) == 0 ?  low : na  
var  dir      =  0, dir := not na(ph) and na(pl) ? 1 : not na(pl) and na(ph) ? -1 : dir
var zigzag    = array.new<chart.point>(0)
oldzigzag     = zigzag.copy()
dirchanged    = dir[0] != dir[1]
var arraySize = 10
//#endregion

//#region ———————————————————— 2. Map
// Create map for label's tooltip
tip = map.new<string, string>()
tip.put('HH', 'HIGHER HIGH')
tip.put('LH', 'LOWER HIGH')
tip.put('LL', 'LOWER LOW')
tip.put('HL', 'HIGHER LOW')
//#endregion

//#region ———————————————————— 3. Switches
switchLine        = switch lineType
    'dash'        => line.style_dashed
    'dot'         => line.style_dotted
    'solid'       => line.style_solid
    'arrow right' => line.style_arrow_right
    'arrow left'  => line.style_arrow_left
switchExtend      = switch extendType
    'left'        => extend.left
    'right'       => extend.right
    'both'        => extend.both
    'none'        => extend.none
//#endregion

//#region ———————————————————— 4. Methods
// @function     addPoint
// @param           price   float value
// @param           index   int value
// @param       arraySize   array size for chart.point[]
// @returns chart.point[] 
method addPoint(chart.point[] id, float price = na, int index = na, int arraySize = na) =>
    id.unshift(chart.point.new(time, index, price))
    if id.size() > arraySize
        id.pop()

// @function updatePoints
// @param           price   float value
// @param           index   int value
// @param       arraySize   array size for chart.point[]
// @param             dir   int value
// @returns chart.point[] 
method updatePoints(chart.point[] id, float price = na, int index = na, int arraySize = na, int dir = na) =>
    if id.size() == 0
        id.addPoint(price, index)
    else
        if dir == 1 and price > id.get(0).price or dir == -1 and price < id.get(0).price 
            id.set(0, chart.point.new(time, index, price))
        chart.point.new(na, na, na)
//#endregion

//#region ———————————————————— 5. Executions
if na(ph) or na(pl)
    if dirchanged
        zigzag.addPoint(dir == 1 ? ph : pl, bar_index, arraySize)
    else
        zigzag.updatePoints(dir == 1 ? ph : pl, bar_index, arraySize, dir)

if zigzag.size() >= 3 and oldzigzag.size() >= 3
    var line  linezigzag  = na
    var label labelzigzag = na
    if     zigzag.get(0).index != oldzigzag.get(0).index or  zigzag.get(0).price != oldzigzag.get(0).price
        if zigzag.get(1).index == oldzigzag.get(1).index and zigzag.get(1).price == oldzigzag.get(1).price
            linezigzag.delete()
            labelzigzag.delete()
        if showLine
            linezigzag  := line.new(
                                     first_point  = zigzag.get(0),
                                     second_point = zigzag.get(1),
                                     xloc         = xloc.bar_index,
                                     extend       = switchExtend,
                                     color        = dir == 1 ? colorUp : colorDn,
                                     style        = switchLine,
                                     width        = width)
        if showLabel
            textzigzag   = dir == 1 ?    zigzag.get(0).price > zigzag.get(2).price ? 'HH'    : 'LH' :
                                         zigzag.get(0).price < zigzag.get(2).price ? 'LL'    : 'HL'
            colorzigzag  = dir == 1 ?    zigzag.get(0).price > zigzag.get(2).price ? colorDn : colorUp :
                                         zigzag.get(0).price < zigzag.get(2).price ? colorUp : colorDn
            currentRes   = timeframe.isintraday ? 'dd-MMM-yyyy HH:mm' : 'dd-MMM-yyyy'
            labelzigzag := label.new(
                                     point     = zigzag.get(0),
                                     text      =  textzigzag,
                                     xloc      = xloc.bar_index,
                                     style     = dir == 1 ? label.style_label_down : label.style_label_up,
                                     color     = color.new(color.blue, 100),
                                     textcolor = colorzigzag,
                                     size      = fontSize,
                                     tooltip   = tip.get(textzigzag) + '\n' +
                                                 'Time  : ' + str.format_time(zigzag.get(0).time, currentRes, syminfo.timezone)+ '\n' +
                                                 'Price : ' + str.tostring(zigzag.get(0).price))
//#endregion
