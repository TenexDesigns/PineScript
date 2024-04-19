Time & Sales (Tape) [By MUQWISHI]
https://www.tradingview.com/script/IZnarK90-Time-Sales-Tape-By-MUQWISHI/#:~:text=The%20%E2%80%9CTime%20and%20Sales%E2%80%9D%20(,size%20for%20a%20specific%20security.





// This Pine Script™ code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © MUQWISHI
//@version=5
indicator("Time & Sales", overlay = true)

// |++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
// |                                   INPUT                                    |
// |++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
// +++++++++++++++ Table Settings
var G1 = "Table Settings" 

// Timezone
timzon = input.string("GMT-4", "Timezone",   group = G1)

// Location 
tablePos = input.string("Top Right", "Table Location", 
             ["Top Right" , "Middle Right"  , "Bottom Right" , 
              "Top Center", "Middle Center" , "Bottom Center", 
              "Top Left"  , "Middle Left"   , "Bottom Left" ], group = G1)

// Size
tableSiz = input.string("Small", "Table Size", 
             ["Auto", "Huge", "Large", "Normal", "Small", "Tiny"], group = G1)

// Volume Scale Bar
barChk = input.bool(true, "Show Dynamic Volume Scale Bar", group = G1)

// Title Colors
tiClC = input.color(#555580, "Title   ",  group = G1, inline = "2")
tiClX = input.color(#ffffff, "",          group = G1, inline = "2",
         tooltip = "Cell Color | Text Color")

// Cell Colors
upNCC = input.color(#BBD9FB, "Up       ", group = G1, inline = "4")
upXCC = input.color(#0C3299, "",          group = G1, inline = "4")
upClX = input.color(#089981, "",          group = G1, inline = "4",
         tooltip = "Min Color | Max Color | Text Color")

dnNCC = input.color(#FFE0B2, "Down ",     group = G1, inline = "5")
dnXCC = input.color(#E65100, "",          group = G1, inline = "5")
dnClX = input.color(#880E4F, "",          group = G1, inline = "5",
         tooltip = "Min Color | Max Color | Text Color")


// +++++++++++++++ Technical Settings
var G2 = "Technical Settings"
mode = input.string("Lower Timeframe", "Implement By", ["Lower Timeframe", "Live Tick"], group = G2, 
         tooltip = "➤ Lower Timeframe:\nFetch data from selected lower timeframe.\n\n" +
                   "➤ Live Tick:\nFetch data in real-time on a tick-by-tick basis, capturing data as soon as it's observed by the system.")

tFrm = input.timeframe("1S", " ➤ Lower Timeframe",  group = G2,
         tooltip = "Note:\nIf the selected timeframe is invalid (higher than chart's timeframe), the indicator will automatically switch to 1 second.")

nRow = input.int(60, "Length (№ of Rows)", 1, 100, group = G2) + 2

iVol = input.string("Volume", "Size Type", ["Volume", "Price Volume"], group = G2)

// |++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
// |                                 CALCULATION                                |
// |++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
// +++++++++++++++ Get Number of Strings Function
strVol (x) => str.tostring(x, format.volume)
strPvol(x) => str.tostring(x, "$##.00")
strTik (x) => str.tostring(x, format.mintick)

// Right Digits 
rDigits() =>
    var lft = 0
    var out = "0" 
    x = str.tostring(volume)
    if str.contains(x, ".")
        out := "0." 
        nLft = str.length(str.split(x, ".").get(1))
        if nLft > lft
            lft := nLft
            for i = 1 to lft
                out += "0"
    out

// Convert to String
strNum(x, a) =>
    var form = rDigits()
    b =  a == 1 ? strTik(x) : a == 2 ? strPvol(x) : 
         x > 999999 ? strVol(x) : str.tostring(x, form)
    
    a == 1 ? str.length(b) < 4 ? 4 : str.length(b) : 
             str.length(b) < 6 ? 6 : str.length(b)


// +++++++++++++++ Get RBG Color Function
rbgCol(x, xMin, xMax, chg) =>
    cMin = chg > 0 ? upNCC : dnNCC
    cMax = chg > 0 ? upXCC : dnXCC

    c = color.from_gradient(x, xMin, xMax, cMin, cMax)
    [color.r(c), color.b(c), color.g(c), color.t(c)]


// +++++++++++++++ Declare Matrices 
var   tMtx = matrix.new<float>(0,  9, na)  // Applicable For {Implement By= Lower Timeframe}
varip iMtx = matrix.new<float>(1, 10,  0)  // Applicable For {Implement By= Live Tick}


// +++++++++++++++ Call Security For Lower Timeframe Data
fun(c) =>
    cVol = iVol == "Volume" ? volume : hlc3 * volume
    
    if c == "Lower Timeframe" 
        var chg = float(na), 
        if close[1] != close
            chg := (close - close[1])/close[1] > 0 ? 1 : -1
        
        var mx = 0.0, mx := ta.highest(cVol, nRow - 2)
        var mn = 0.0, mn := ta.lowest( cVol, nRow - 2)

        [r, b, g, t] = rbgCol(cVol, mn, mx, chg)
        vCl = color.rgb(int(r), int(g), int(b), int(t))

        [close, time, cVol, vCl, chg]
    else
        [close, timenow, cVol, color(na), float(na)]

var timfram = mode == "Live Tick" ? "" : timeframe.in_seconds(tFrm) > timeframe.in_seconds() ? "1S" : tFrm
[cls, tim, vol, vCl, chg] = request.security_lower_tf(syminfo.tickerid, timfram, fun(mode), true, ignore_invalid_timeframe = true)


// +++++++++++++++ Mode = Lower Timeframe
if mode == "Lower Timeframe"
    if cls.size() > 0 and cls.size() == tim.size() and cls.size() == vol.size()

        // Slice Variables
        sIdx = cls.size() - nRow < 0 ? 0 : cls.size() - nRow
        eIdx = cls.size()

        tim0 = tim.slice(sIdx, eIdx) // Time
        vol0 = vol.slice(sIdx, eIdx) // Volume
        cls0 = cls.slice(sIdx, eIdx) // Last
        vCl0 = vCl.slice(sIdx, eIdx) // Cell Color
        chg0 = chg.slice(sIdx, eIdx) // Change Up or Down

        // Loop
        for i = 0 to cls0.size() - 1
            cTim = tim0.get(i)
            cVol = vol0.get(i)
            cCls = cls0.get(i)
            cCol = vCl0.get(i)
            cChg = chg0.get(i)

            // Cell Color, Up/Down Volume
            r0 = color.r(cCol), g0 = color.g(cCol), b0 = color.b(cCol), t0 = color.t(cCol)

            // Total Strings
            stng = strNum(cCls, 1) + strNum(cVol, iVol == "Volume" ? 0 : 2) +  12

            // Add new rows with values to Matrix
            tMtx.add_row(0, array.from(cTim, cChg, cCls, cVol, stng,  r0, g0, b0, t0))
            
            // Remove over rows from a Matrix
            while tMtx.rows() > nRow
                tMtx.remove_row(tMtx.rows()-1)


// +++++++++++++++ Mode = Live Tick
else
    if barstate.isrealtime
        var mxVol = .0 // Max Volume
        var mnVol = .0 // Min Volume

        tim0 = tim.get(0) // Time
        vol0 = vol.get(0) // Volume
        cls0 = cls.get(0) // Last

        varip cChg = 0    // Change Up or Down
        varip cVol = vol0 // Volume

        // Tick Volume
        if barstate.isnew
            cVol := vol0
        else 
            cVol := vol0 - iMtx.get(0, 9)

        // Change Up or Down
        float pCls = iMtx.get(0, 2)
        cChg := pCls < cls0 ? 1 : pCls > cls0 ? -1 : cChg
        
        // Color for Up/Down Volume
        mxVol := iMtx.col(3).max()
        mnVol := iMtx.col(3).min()
        [r, b, g, t] = rbgCol(cVol, mnVol, mxVol, cChg)
        r0 = r, g0 = g, b0 = b , t0 = 0

        // Total Strings
        stng = strNum(close, 1) + strNum(cVol, (iVol == "Volume" ? 0 : 2)) + 12

        // Add new rows with values to Matrix
        iMtx.add_row(0, array.from(tim0, cChg, cls0, cVol, stng, r0, g0, b0, t0, vol0))

    // Remove over rows from a Matrix
    if iMtx.rows() > nRow
        iMtx.remove_row(iMtx.rows()-1)

// |++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
// |                                   TABLE                                    |
// |++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
// Get Tbale Location & Size
locNsze(x) => 
    y   = str.split(str.lower(x), " ")
    out = ""
    for i = 0 to y.size() - 1
        out := out + y.get(i)
        if i != y.size() - 1
            out := out + "_"
    out

// Volume Format
volForm(x) =>
    var form = rDigits()
    iVol == "Volume" ? x > 999999 ? strVol(x) : str.tostring(x, form) : strPvol(x)      

// Create Table
rows = mode == "Lower Timeframe" ? tMtx.rows() : 
       mode == "Live Tick"       ? iMtx.rows() : 0
var table tbl = na
if rows > 2
    tbl := table.new(locNsze(tablePos), 4, rows + 3, color(na), tiClC, 2, color(na), 0)

// Get Cell Function
cell(col, row, txt, hln, color, txtCol) =>
    halign = hln == -1 ? text.align_left : hln == 1 ? text.align_right : text.align_center
    tbl.cell(col, row, txt, 0, 0, txtCol, halign, bgcolor = color, text_size = locNsze(tableSiz))

// Scale Bar
scaleBar(mtx) =>
    // Sum Volume 
    upVol = .0
    dnVol = .0
    for i = 0 to mtx.rows() - 3
        if mtx.get(i, 1) > 0
            upVol += mtx.get(i, 3) 
        else
            dnVol += mtx.get(i, 3) 
    smVol = upVol + dnVol

    // Estimate Number of Block for Scale Bar 
    stng  = math.ceil(mtx.col(4).max())
    upTxt = "", upStng = math.round(upVol/smVol * stng)
    dnTxt = "", dnStng = stng - upStng
    for i = 0 to math.max(upStng, dnStng)
        if i < upStng 
            upTxt += "█" 
        if i < dnStng
            dnTxt += "█"
    
    // Draw Scale Bar
    var z = tableSiz == "Huge" or tableSiz == "Large" ? "" : " "
    tbl.merge_cells(0, 0, 3, 0)
    tbl.cell(0, 0, upTxt + z + dnTxt, 0, 0.0001, tiClC, text.align_center, text.align_center, 
     locNsze(tableSiz), tiClC, text_font_family = font.family_monospace), 

    tbl.merge_cells(0, 1, 3, 1)
    tbl.cell(0, 1, upTxt, 0, 1, upXCC, text.align_left, text.align_center, 
     locNsze(tableSiz), color.new(upNCC, 25), text_font_family = font.family_monospace), 

    tbl.merge_cells(0, 2, 3, 2)
    tbl.cell(0, 2, dnTxt, 0, 1, dnXCC, text.align_right, text.align_center, 
     locNsze(tableSiz), color.new(dnNCC, 25), text_font_family = font.family_monospace),

    tbl.merge_cells(0, 3, 3, 3)
    tCol = math.max(upVol, dnVol) == upVol ? upNCC : dnNCC
    cell(0, 3, "B  " + volForm(upVol) +"  ✖  " + volForm(dnVol) + "  S", 0, tiClC, tCol)
    
// Fill Cells
fillCell(mtx) =>
    //  Dynamic Volume Scale Bar
    if barChk
        scaleBar(mtx)

    // Cells
    r = 5
    for i = 0 to mtx.rows() - 3
        vVol = mtx.get(i, 3) // Volume
        side = mtx.get(i, 1) // Arrow Direction 

        cCol = color.rgb(int(mtx.get(i, 5)), int(mtx.get(i, 6)), 
                         int(mtx.get(i, 7)), int(mtx.get(i, 8))) // Cell Color
        xCol = side > 0 ? upClX : dnClX // Text Color

        cell(0, r, str.format_time(int(mtx.get(i, 0)), "HH:mm:ss", timzon), 0, cCol, xCol)
        cell(1, r, side > 0 ? "▲" : "▼",  0, cCol, xCol)
        cell(2, r, strTik(mtx.get(i, 2)), 0, cCol, xCol)
        cell(3, r, volForm(vVol), 0, cCol, xCol)
        r += 1

// Draw Table
if barstate.islast
    if not na(tbl)
        table.clear(tbl, 0, 0, 3, rows + 2)

        // Header
        cell(0, 4,   "Time", 0, tiClC, tiClX)
        cell(1, 4,   "Side", 0, tiClC, tiClX)
        cell(2, 4,   "Last", 0, tiClC, tiClX)
        cell(3, 4, "Volume"+ (iVol == "Volume" ? "":"\n("+syminfo.currency+")"), 0, tiClC, tiClX)

        // Table
        if mode == "Lower Timeframe"
            fillCell(tMtx)
        else
            fillCell(iMtx)
