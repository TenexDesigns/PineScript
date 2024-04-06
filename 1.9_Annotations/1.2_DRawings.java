In Pine Script, annotations are used to add text, shapes, or other graphical elements to the chart for visual analysis or explanation purposes. They allow you to annotate specific points or regions on the chart to highlight important information or insights. Here's a reference manual for some commonly used annotations in Pine Script:

### 1. **Label Annotations**:
   - **label.new()**: Creates a text label at a specified location on the chart. Example:
     ```pinescript
     label.new(x, y, text="Label Text", color=color.white, style=label.style_label_down)
     ```

### 2. **Shape Annotations**:
   - **line.new()**: Draws a straight line on the chart. Example:
     ```pinescript
     line.new(x1, y1, x2, y2, color=color.blue, width=1)
     ```
   - **hline.new()**: Draws a horizontal line across the chart. Example:
     ```pinescript
     hline.new(y, color=color.red, width=1)
     ```
   - **vline.new()**: Draws a vertical line on the chart. Example:
     ```pinescript
     vline.new(x, color=color.green, width=1)
     ```
   - **rectangle.new()**: Draws a rectangle on the chart. Example:
     ```pinescript
     rectangle.new(left, top, right, bottom, color=color.orange, width=1, style=shape.style_solid)
     ```

### 3. **Arrow Annotations**:
   - **arrow.new()**: Draws an arrow on the chart. Example:
     ```pinescript
     arrow.new(x, y, x2, y2, color=color.blue, width=1, style=arrow.style_normal)
     ```

### 4. **Plot Annotations**:
   - **plotshape()**: Plots a shape on the chart based on specified conditions. Example:
     ```pinescript
     plotshape(condition, style=shape.triangleup, location=location.belowbar, color=color.green, size=size.small)
     ```

### Most Used Annotations:
   - **label.new()**: Used for adding text labels to the chart.
   - **line.new()**, **hline.new()**, **vline.new()**: Used for drawing lines on the chart.
   - **rectangle.new()**: Used for highlighting specific regions on the chart.
   - **plotshape()**: Used for plotting shapes based on specific conditions.

Annotations are essential for adding context and clarity to your technical analysis charts in Pine Script. By using annotations effectively, you can communicate insights, identify patterns, and make informed trading decisions more easily on the TradingView platform.

  
