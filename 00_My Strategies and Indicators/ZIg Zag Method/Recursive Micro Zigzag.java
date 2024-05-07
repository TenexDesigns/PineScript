


// This work is licensed under Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (CC BY-NC-SA 4.0) https://creativecommons.org/licenses/by-nc-sa/4.0/
// © Trendoscope Pty Ltd
//                                       ░▒             
//                                  ▒▒▒   ▒▒      
//                              ▒▒▒▒▒     ▒▒      
//                      ▒▒▒▒▒▒▒░     ▒     ▒▒          
//                  ▒▒▒▒▒▒           ▒     ▒▒          
//             ▓▒▒▒       ▒        ▒▒▒▒▒▒▒▒▒▒▒  
//   ▒▒▒▒▒▒▒▒▒▒▒ ▒        ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒         
//   ▒  ▒       ░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░        
//   ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒▒▒▒▒▒▒▒         
//   ▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ ▒▒                       
//    ▒▒▒▒▒         ▒▒▒▒▒▒▒                            
//                 ▒▒▒▒▒▒▒▒▒                           
//                ▒▒▒▒▒ ▒▒▒▒▒                          
//               ░▒▒▒▒   ▒▒▒▒▓      ████████╗██████╗ ███████╗███╗   ██╗██████╗  ██████╗ ███████╗ ██████╗ ██████╗ ██████╗ ███████╗
//              ▓▒▒▒▒     ▒▒▒▒      ╚══██╔══╝██╔══██╗██╔════╝████╗  ██║██╔══██╗██╔═══██╗██╔════╝██╔════╝██╔═══██╗██╔══██╗██╔════╝
//              ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒        ██║   ██████╔╝█████╗  ██╔██╗ ██║██║  ██║██║   ██║███████╗██║     ██║   ██║██████╔╝█████╗ 
//             ▒▒▒▒▒       ▒▒▒▒▒       ██║   ██╔══██╗██╔══╝  ██║╚██╗██║██║  ██║██║   ██║╚════██║██║     ██║   ██║██╔═══╝ ██╔══╝  
//            ▒▒▒▒▒         ▒▒▒▒▒      ██║   ██║  ██║███████╗██║ ╚████║██████╔╝╚██████╔╝███████║╚██████╗╚██████╔╝██║     ███████╗
//             ▒▒             ▒                        
//@version=5
indicator("Recursive Micro Zigzag", overlay = true, max_lines_count=500)
import HeWhoMustNotBeNamed/Drawing/3 as dr

ltf = input.timeframe('1', 'Lower timeframe')
levelToDisplay = input.int(4, 'Level to Display')
backtestBars = input.int(5000, 'Backtest Bars', minval=500, maxval=20000, step=500)

type Pivot
	dr.Point point
	int dir
	int level = 0

type Zigzag
    array<Pivot> zigzagPivots
    array<Pivot> newPivots
    array<dr.Line> zigzagLines
    bool removeLast = false
    Pivot removedPivot
    Pivot tempBullishPivot
    Pivot tempBearishPivot

method addnewpivot(Zigzag this, Pivot pivot)=>
    dir = math.sign(pivot.dir)
    if(this.zigzagPivots.size() >=1)
        lastPivot = this.zigzagPivots.get(0)
        lastValue = lastPivot.point.price

        if(math.sign(lastPivot.dir) == math.sign(dir))
            runtime.error('Direction mismatch')
        if(this.zigzagPivots.size() >=2)
            llastPivot = this.zigzagPivots.get(1)
            value = pivot.point.price
            llastValue = llastPivot.point.price
            newDir =  dir * value > dir * llastValue ? dir * 2 : dir
            pivot.dir := int(newDir)

    this.zigzagPivots.unshift(pivot)
    this.newPivots.push(pivot)
    this

method resetFlags(Zigzag this)=>
    this.removeLast := false
    this.removedPivot := na
    this.newPivots.clear()

method removeLast(Zigzag this)=>
    this.removedPivot := this.zigzagPivots.shift()
    this.removeLast := true

method addNew(Zigzag this, Pivot pivot)=>
    if(this.zigzagPivots.size() == 0)
        this.zigzagPivots.unshift(pivot)
        false
    else
        lastPivot = this.zigzagPivots.first()
        if(math.sign(lastPivot.dir) == math.sign(pivot.dir) and pivot.point.price*pivot.dir > lastPivot.point.price*pivot.dir)
            this.removeLast()
        if(pivot.point.price*pivot.dir > lastPivot.point.price*pivot.dir)
            this.addnewpivot(pivot)
        true

method addNextLevelPivot(Zigzag nextLevel, Pivot pivot)=>
    lPivot = Pivot.copy(pivot)
    dir = lPivot.dir
    newDir = math.sign(dir)
    value = lPivot.point.price
    lPivot.level := lPivot.level+1

    if(nextLevel.zigzagPivots.size() > 0)
        lastPivot = nextLevel.zigzagPivots.first()
        lastDir = math.sign(lastPivot.dir)
        lastValue = lastPivot.point.price
        if(math.abs(dir) == 2)
            if(lastDir == newDir)
                if(dir*lastValue < dir*value)
                    nextLevel.removeLast()
                else
                    tempPivot = newDir >0 ? nextLevel.tempBearishPivot : nextLevel.tempBullishPivot
                    if(not na(tempPivot))
                        nextLevel.addnewpivot(tempPivot)
            else
                tempFirstPivot = newDir >0 ? nextLevel.tempBullishPivot : nextLevel.tempBearishPivot
                tempSecondPivot = newDir >0 ? nextLevel.tempBearishPivot : nextLevel.tempBullishPivot
                if(not na(tempFirstPivot) and not na(tempSecondPivot))
                    tempVal = tempFirstPivot.point.price
                    val = lPivot.point.price
                    if(newDir*tempVal > newDir*val)
                        nextLevel.addnewpivot(tempFirstPivot)
                        nextLevel.addnewpivot(tempSecondPivot)
            nextLevel.addnewpivot(lPivot)
            nextLevel.tempBullishPivot := na
            nextLevel.tempBearishPivot := na
            true
        else
            tempPivot = newDir > 0? nextLevel.tempBullishPivot : nextLevel.tempBearishPivot
            if(not na(tempPivot))
                tempDir = tempPivot.dir
                tempVal = tempPivot.point.price
                val = lPivot.point.price
                if(val*dir > tempVal*dir)
                    if(newDir > 0)
                        nextLevel.tempBullishPivot := lPivot
                    else
                        nextLevel.tempBearishPivot := lPivot
                false
            else
                if(newDir > 0)
                    nextLevel.tempBullishPivot := lPivot
                else
                    nextLevel.tempBearishPivot := lPivot
                true
    else if(math.abs(dir) == 2)
        nextLevel.addnewpivot(lPivot)
        true

method getColor(Pivot this)=>
    this.dir == 2? color.green :
         this.dir == 1? color.orange :
         this.dir == -1? color.lime : 
         this.dir == -2 ? color.red : color.blue

method draw(Zigzag this, bool highlight = false)=>
    if(this.removeLast and this.zigzagLines.size() > 0)
        this.zigzagLines.shift().delete()
    for [index, newPivot] in this.newPivots
        if(this.zigzagPivots.size() > this.newPivots.size()-index)
            dr.LineProperties properties = dr.LineProperties.new(color = newPivot.getColor(), style = highlight? line.style_solid : line.style_dotted, width = highlight? 2 : 0)
            this.zigzagLines.unshift(this.zigzagPivots.get(this.newPivots.size()-index).point.createLine(newPivot.point, properties).draw())

[lh, ll, lv] = request.security_lower_tf(syminfo.tickerid, ltf, [high, low, volume], true)
if(bar_index >= (last_bar_index - backtestBars))
    hIndices = array.sort_indices(lh, order.descending)
    highestIndex = array.size(hIndices) >= 5? array.get(hIndices, 0) : 0
    lIndices = array.sort_indices(ll, order.ascending)
    lowestIndex = array.size(lIndices) >= 5? array.get(lIndices, 0) : 0

    var array<Zigzag> zigzagMatrix = array.from(Zigzag.new(array.new<Pivot>(), array.new<Pivot>(), array.new<dr.Line>()))

    if not na(highestIndex) and not na(lowestIndex)
        Pivot pHigh = Pivot.new(dr.Point.new(high, bar_index, time), 1)
        Pivot pLow = Pivot.new(dr.Point.new(low, bar_index, time), -1)
        
        currentZigzag = zigzagMatrix.first()
        currentZigzag.addNew(highestIndex<lowestIndex?pHigh:pLow)
        currentZigzag.addNew(highestIndex<lowestIndex?pLow:pHigh)
        level = 0
        while(currentZigzag.zigzagPivots.size() > 0 and currentZigzag.newPivots.size() > 0 and level <= levelToDisplay)
            level := level+1
            if(zigzagMatrix.size()<=level)
                zigzagMatrix.push(Zigzag.new(array.new<Pivot>(), array.new<Pivot>(), array.new<dr.Line>()))
            nextZigzag = zigzagMatrix.get(level)

            if(currentZigzag.removeLast and nextZigzag.zigzagPivots.size() > 0?
                     (currentZigzag.removedPivot.point.price == nextZigzag.zigzagPivots.first().point.price and
                     currentZigzag.removedPivot.point.bar == nextZigzag.zigzagPivots.first().point.bar) : false )
                nextZigzag.removeLast()
            for [index, newPivot] in currentZigzag.newPivots
                nextZigzag.addNextLevelPivot(newPivot)
            if(levelToDisplay == level-1)
                currentZigzag.draw(levelToDisplay == level-1)
            currentZigzag.resetFlags()
            currentZigzag := nextZigzag
