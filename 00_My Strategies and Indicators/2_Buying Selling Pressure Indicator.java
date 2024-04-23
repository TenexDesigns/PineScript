//@version=4

study("BS",format=format.volume, precision=0)

// BUYING VOLUME AND SELLING VOLUME //
buyVolume = iff( (high==low), 0, volume*(close-low)/(high-low))
sellVolume = iff( (high==low), 0, volume*(high-close)/(high-low))

plot(volume, style=plot.style_columns, color=color.teal, title="SELL V") // shows total volume (!) 
plot(sellVolume, style=plot.style_columns, color=color.red, title="BUY V") // shows only buy volume




  
