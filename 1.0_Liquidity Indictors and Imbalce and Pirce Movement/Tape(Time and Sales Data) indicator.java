Tape (Time and Sales) liquid-trader
https://www.tradingview.com/script/vIDMwYuU-Tape-Time-and-Sales/

-----------AWERSOME INDICATORS TO CHECK OUT   ---------------  https://in.tradingview.com/scripts/tape/






-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © liquid-trader

// This indicator is a synthesized "Tape" (aka. Time and Sales) from real time market data.
// It's specifically designed to be performant, expediting trading insights and decisions.
// More here: https://www.tradingview.com/script/vIDMwYuU-Tape-Time-and-Sales/

//@version=5
indicator("Tape (Time and Sales)", "Tape", true, max_bars_back=0)

// Base Colors
none = color.new(color.black, 100), clr1 = color.lime, clr2 = color.red, clr3 = color.yellow, clr4 = color.white, clr5 = color.silver, clr6 = color.black, clr7 = color.rgb(0,0,0,0)

// Time & Sales Settings
g1 ="Time and Sales", i1 = "Text Color of Cell", i2 = "Background Color of Cell", act1 = "Non Premium (Basic, Essential, Plus)", act2 = "Premium"
acct = input.string(act1, "Account Type", [act1, act2], "The print frequency and precision relies on chart updates. The most granular data non Premium users have access to is a 1 minute chart, which tends to update about once every second. This update interval can aggregate volume changes into a single print rather than showing each volume change as a unique print.\n\nPremium accounts have access to sub-minute timeframe data, which can update intra-second. This means chart update intervals better align with volume changes, lending itself to more frequent and precise prints.\n\nIf you select \"Premium\" and do not in fact have a Premium account, the script may break when it tries to request data the account type does not have access to.", group=g1, display=display.none)
tlen = input.int(20,"Tape Length", 1, 100, 1,tooltip="The max number of \"Time and Sale\" rows displayed in the table.", group=g1, display=display.none)
prsn = input.int(0, "Size Precision",0, 10, 1, tooltip="How many digits passed the decimal you would like to see, if volume is not a whole number.", group=g1, display=display.none)
min  = input.float(0.000001, "Min Print Size", 0, step=1, tooltip="The smallest volume size you want to see.", group=g1, display=display.none)
lrg  = input.float(1000, "Large Size Min", 1, step=100, tooltip="A single order volume size considered to be large for this security.", group=g1, display=display.none)
hlt  = input.bool(true, "Highlight large prints","Changes the colors of a rows \"Size\" cell if it meets the \"Large Size Min\" threshold.", group=g1, display=display.none)
big  = input.bool(true, "Increase row height of large prints", "Enlarges the vertical height of a row if the \"Size\" cell meets the \"Large Size Min\" threshold.", group=g1, display=display.none)
pUpt = input.color(clr7, "Bull", inline=i1, group=g1, display=display.none)
pDnt = input.color(clr7, "Bear", inline=i1, group=g1, display=display.none)
lSzt = input.color(clr7, "Large", inline=i1, group=g1, display=display.none)
hdrt = input.color(clr4, "Header", inline=i1, group=g1, display=display.none)
deft = input.color(clr5, "Default", "Text Colors", inline=i1, group=g1, display=display.none)
pUpc = input.color(clr1, "Bull ", inline=i2, group=g1, display=display.none)
pDnc = input.color(clr2, "Bear", inline=i2, group=g1, display=display.none)
lSzc = input.color(clr3, "Large", inline=i2, group=g1, display=display.none)
hdrc = input.color(clr6, "Header", inline=i2, group=g1, display=display.none)
defc = input.color(clr6, "Default", "Cell / Row Colors", inline=i2, group=g1, display=display.none)

// Volume Metrics Settings
g2 = "Bar Volume Metrics"
vmPrsn = input.int(2, "Metric Precision", 0, 10, 1, tooltip="How many digits passed the decimal you would like to see, if the metric is not a whole number.", group=g2, display=display.none)
vmUp  = input.bool(false, "↑ Up                     ", "Total bar volume correlated with rising prices, and the value at which to highlight the metric.", "up", g2, display=display.none)
vmDn  = input.bool(false, "↓ Down                ", "Total bar volume correlated with falling prices, and the value at which to highlight the metric.", "down", g2, display=display.none)
vmSok = input.bool(false, "→ Absorbed         ", "Total bar volume correlated with unchanging prices, and the value at which to highlight the metric. The volume absorbed by at a given price.", "absorb", g2, display=display.none)
vmTot = input.bool(false, "+ Total                  ", "Sum of all bar volume, and the value at which to highlight the metric.", "total", g2, display=display.none)
vmRto = input.bool(false, "% Ratio                 ", "The ratio between up and down bar volume, and the value at which to highlight the metric. \"Up\" is the numerator in positive ratios and \"Down\" is the numerator in negative ratios. \"Absorbed\" is added to the denominator of both positive and negative ratios.", "ratio", g2, display=display.none)
vmMov = input.bool(false, "⇅ Move               ", "Bar volume of a continuous directional move, excluding \"absorbed\", and the value at which to highlight the metric.", "move", g2, display=display.none)
vmAcc = input.bool(false, "☳ Accumulation  ", "Bar volume accumulating at the current price, and the value at which to highlight the metric. Similar to \"absorbed\", but includes the volume of the last price change.", "accum", g2, display=display.none)
vmUpSig = input.float(1000, "", inline="up", group=g2, display=display.none)
vmDnSig = input.float(1000, "", inline="down", group=g2, display=display.none)
vmSoSig = input.float(1000, "", inline="absorb", group=g2, display=display.none)
vmToSig = input.float(3000, "", inline="total", group=g2, display=display.none)
vmRtSig = input.float(3, "", inline="ratio", group=g2, display=display.none)
vmMoSig = input.float(100, "", inline="move", group=g2, display=display.none)
vmAcSig = input.float(100, "", inline="accum", group=g2, display=display.none)

// General Table Settings
g3 = "Table Settings", i3 = "Position", i4 = "Frame", i5 = "Border"
tsiz = input.string(size.auto, "Text Size                    ", [size.tiny, size.small, size.normal, size.large, size.huge, size.auto],"The size of a cells text.", group=g3, display=display.none)
yPos = input.string("Middle", "Location on the Chart  ", ["Top", "Middle", "Bottom"], inline=i3, group=g3, display=display.none)
xPos = input.string("Right", "",["Left", "Center", "Right"], inline=i3, group=g3, display=display.none)
frmw = input.int(0, "Frame Width & Color   ", 0, 10, 1, inline=i4, group=g3, display=display.none)
frmc = input.color(none, "", inline=i4, group=g3, display=display.none)
brdw = input.int(1, "Border Width & Color  ", 0, 10, 1, inline=i5, group=g3, display=display.none)
brdc = input.color(none, "", inline=i5, group=g3, display=display.none)
shot = input.bool(true, "Time Column       ", "The format and time zone of the date / time.\n\nThe date / time format can be any ISO 8601 format, and the time zone can be any IANA time zone ID (ie. \"America/New_York\").\n\nThe default format is \"hh:mm:ss a\" to only show time, and the default time zone is \"syminfo.timezone\", which inherits the time zone of the exchange of the chart.", inline="datetime", group=g3, display=display.none)
frmt = input.string("hh:mm:ss a", "", inline="datetime", group=g3, display=display.none)
zone = input.string("syminfo.timezone", "", inline="datetime", group=g3, display=display.none)
invertTape = input.bool(false, "Invert Tape Direction", "When enabled, metrics will move to the bottom of the tape, the price / size / time column headers will appear beneath the print stream, and the newest prints will be at the bottom of the print stream causing the prints to \"scroll\" up instead of down.", group=g3, display=display.none)

// Ancillary Params
tmzn = zone == "syminfo.timezone" ? syminfo.timezone : zone
vmVis = array.from(vmUp, vmDn, vmSok, vmTot, vmRto, vmMov, vmAcc), vmArr = vmVis.size()
vmSig = array.from(vmUpSig, vmDnSig, vmSoSig, vmToSig, vmRtSig, vmMoSig, vmAcSig)

// Create new arrays for tracking price, volume, and timestamps.
varip price = array.new_float(), varip vol  = array.new_float(), varip stamp = array.new_int()

// Initialize core variables.
varip vdiff = array.new_float(2, 0), varip vsize = 0.0, varip newVolume = false, varip prevDir = 0., varip bull = 0., varip bear = 0., varip abso = 0., varip totl = 0., varip move = 0., varip volAtPrice = 0., varip accu = 0., varip rtio = 0., metricOffset = 0

// Get price, volume, and time from a 1 second chart.
tf = acct == act1 ? "1" : "1S"
[ltf_p, ltf_v, ltf_t] = request.security_lower_tf(syminfo.tickerid, tf, [close, volume, timenow])

// Putting the core logic within a single iteration loop reduces performance penalties accumulated over time.
// This is because historical values of variables initialized inside a loop are not maintained.
for Fewer_Performance_Penalties_Over_Time = 0 to 0

    // Reset some of the core variables on each new bar.
    if barstate.isnew
        vdiff.fill(0), bull := 0, bear := 0, abso := 0, totl := 0

    // Calc the new volume size.
    barVol = ltf_v.sum()
    if barVol != vdiff.last()
        vdiff.push(barVol), vdiff.shift()
        vsize := vdiff.range()
        newVolume := true

    // Update the price, volume, and timestamp arrays when there is new volume and the minimum print size is reached.
    if barstate.isrealtime and newVolume and vsize >= min
        price.unshift(ltf_p.last()), vol.unshift(vsize), stamp.unshift(ltf_t.last())

    // Create array for column headers, which also serves to define the number of columns.
    hdrStr = shot ? array.from(" PRICE ", " SIZE ", " TIME ") : array.from(" PRICE ", " SIZE ")

    // Track column and row lengths.
    headrArr = hdrStr.size(), priceArr = price.size()

    // Create table with params and metric variables / header array.
    vmTitles = array.from(" ↑ Up ", " ↓ Down ", " → Abs … ", " + Total ", " % Ratio ", " ⇅ Move ", " ☳ Acc … ")
    tape = table.new(str.lower(yPos) + "_" + str.lower(xPos), headrArr, tlen + vmArr + 3, none, frmc, frmw, brdc, brdw)

    // If there are at least 2 prices to compare…
    if priceArr > 1
        // If any metrics are enabled…
        if vmVis.includes(true)
            // Update metrics if there is new volume.
            if newVolume
                // Get the difference between the current and previous price.
                pdiff = price.get(0) - price.get(1)

                // Evaluate the price direction, then update the price difference.
                dir = pdiff == 0 ? 0 : math.sign(pdiff)
                changeDir = (dir > 0 and prevDir < 0) or (dir < 0 and prevDir > 0)
                prevDir := dir != 0 ? dir : prevDir

                // Set the bullish, bearish, absorbed, and total volume metrics.
                switch dir
                    1  => bull += vsize
                    0  => abso += vsize
                    -1 => bear += vsize
                totl += vsize

                // Set the bull-bear ratio, adding absorbed to the denominator.
                rtio := bull > bear ? nz(bull / (bear + abso)) : bear > bull ? nz(-bear / (bull + abso)) : 0

                // Set the moving and accumulating volumes, based on price action.
                move := dir == 0 ? move : changeDir ? vsize * dir : move + vsize * dir
                volAtPrice := dir == 0 ? volAtPrice + vsize : vsize
                accu := dir != 0 ? 0 : volAtPrice
            
            // Put the volume metric values into an array.
            vmValues = array.from(bull, bear, abso, totl, rtio, move, accu)
            
            // Show the volume metrics header.
            metHedRow = invertTape ? tlen + 2 : 0
            tape.cell(0, metHedRow, " VOLUME ", bgcolor=hdrc, text_color=hdrt, text_size=tsiz, text_halign=text.align_center), tape.merge_cells(0, metHedRow, headrArr - 1, metHedRow)

            // For each volume metric the trader has enabled…
            m = 0
            for [i, showMetric] in vmVis
                if showMetric
                    // Increment the row.
                    m += 1
                    row = invertTape ? tlen + 2 + m : m

                    // Get title and value of metric.
                    vmTitle = vmTitles.get(i)
                    vmValue = math.round(vmValues.get(i), vmPrsn)
                    
                    // Check if a key signal level has been touched.
                    keyLvl = vmSig.get(i)
                    signal = vmValue >= keyLvl or vmValue <= -keyLvl
                    
                    // Show enabled volume metric.
                    for col = 0 to 1
                        rowLabel = col == 0
                        tape.cell(col, row, rowLabel ? vmTitle : str.tostring(vmValue), 0, 0, signal ? lSzt : deft, rowLabel ? text.align_left : text.align_center, text.align_center, tsiz, signal ? lSzc : defc)
                    
                    // Merge the metrics value cell with any remaining table columns in that row.
                    tape.merge_cells(1, row, headrArr - 1, row)
            
            // Visually separate volume metrics from the time and sales.
            tape.cell(0, invertTape ? tlen + 1 : m + 1, "", height=1, text_color=none, bgcolor=none)

            // Set the metric offset, plus the header and visual separation.
            metricOffset := m + 2
        else
            metricOffset := 0
        
        // Show column headers of tape.
        headerRow = invertTape ? tlen : metricOffset
        for [col, colHeader] in hdrStr
            tape.cell(col, headerRow, colHeader, bgcolor=hdrc, text_color=hdrt, text_size=tsiz, text_halign= col != 1 ? text.align_left : text.align_center)

        // Set the direction the tape should scroll.
        extraData = priceArr > tlen ? priceArr - tlen + 1 : 2

        // Iterate through price, volume, and timestamp arrays until the trader defined number of rows is reached.
        for i = 0 to priceArr - extraData
            // Check if the price at index "i" is higher or lower than previous price in array.
            p1 = price.get(i)
            p2 = price.get(i + 1)
            up = p1 > p2
            dn = p1 < p2

            // Get volume size at index "i" and check if it meets the traders large print criteria.
            v = vol.get(i)
            large = v >= lrg
            space = large and big ? " \n " : " "

            // Set which row to update.
            row = invertTape ? tlen - 1 - i : metricOffset + 1 + i

            // Set the rows price, volume, and time data.
            rowStr = array.from(space + str.tostring(p1) + space, space + str.tostring(math.round(v, prsn)) + space, space + str.format_time(stamp.get(i), frmt, tmzn) + space)

            // Print and color rows that have data.
            for col = 0 to headrArr - 1
                largeSize = col == 1 and large and hlt
                bgClr  = largeSize ? lSzc : up ? pUpc : dn ? pDnc : defc
                txtClr = largeSize ? lSzt : up ? pUpt : dn ? pDnt : deft
                tape.cell(col, row, rowStr.get(col), 0, 0, txtClr, col == 1 ? text.align_center : text.align_left, text.align_center, tsiz, bgClr)
        
        // Print empty rows too.
        if priceArr < tlen + 1
            for i = 0 to tlen - nz(priceArr)
                row = invertTape ? i : metricOffset + priceArr + i
                for col = 0 to headrArr - 1
                    tape.cell(col, row, "", bgcolor=defc)

    if priceArr > tlen + 1
        // Remove oldest item in the arrays, if they exceed the trader specified row count.
        price.pop(), vol.pop(), stamp.pop()
    else if priceArr < 2
        // Inform the trader there is no realtime data, if there are fewer than 2 prices to compare.
        tape.cell(0, 0, "\n\t\t\t Waiting for real-time market data ... \t\t\t\n\n", bgcolor=defc, text_color=deft, text_size=tsiz)

    newVolume := false






-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                          












                          
