Currently implemented:
- Get/set sentinel spell pieces
- Get sentinel tier piece (missing/basic/greater)
- Greater sentinel extends psi ambit
- Clarity/Clouding status effects modify psi regen rate
- Spell pieces to R/W compatible hex iotas
- Allow hex to treat vector rulers as R/W iota storage
- Helmet sensor that you can write vectors to via hex, and it will trigger the helmet's spell
- Spell pieces to R/W iota from helmet
- Hex pattern to read iota from helmet
- Hex pattern to read psi meter
- Make spell pieces actually error
- A spell bullet that triggers a hex written to it like a trinket
- Spell pieces to suppress the above hex's cast effects, and the media report the bullet does
- Reading/writing from a CAD via Scribe's interacts with the list of its stored vectors (writing may be disabled in the future)
- Treat CADs themselves as media holders in order to more easily recharge trinket bullets
- Phial CAD battery that trinket bullets can draw from, and allows the Recharge spell to distribute media to all bullets at once.
- Hexes cast from trinket bullets can access the space immediately around the psi spell's attacking/attacked entity, regardless of normal range
- Trinket bullets in armor slots have a cooldown on how often they can trigger their hex

To do:
- Some way to recharge bullets in armor slots without taking them out first (Exosuit controller?)
- actual fucking documentation beyond just spell piece descriptions

Planned for later:
- More sensors for more iota types (and associated spell pieces)
- Non-placeholder textures for most spell pieces
- Dedicated pattern to get CAD vectors when the CAD isn't in your hand
- Hex patterns exclusively for trinket bullets that return data from the psi spell context
- Spell pieces that return media in bullet/CAD
- Spell piece that returns a trinket bullet's cooldown

Maybe:
- Psi material hex trinkets or similar
- CAD battery as insulation against hex bloodcasting
- CAD colorizer that mimics your hex pigment, and hex pigment that mimics your psi colorizer (both will fall back to some default if they find the other)