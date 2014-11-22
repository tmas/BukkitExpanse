BukkitExpanse
=============
BukkitExpanse is a plugin I'm writing for the upcoming 1.8 release. When fully implemented, it will allow players to use one or more currency types to expand the size of the world.

## Pricing
Pricing for expansion is calculated per block, with an option to include time as a factor as well. Vanilla 1.8 has a command that takes both the expansion of the radius and the time to expand as arguments, and I thought this could be an interesting thing to include in the pricing. The general idea for the formula is as follows:
((new diameter)^2 - (old diameter)^2) * (k / time)

where k is a constant which has not yet been decided. Time will probably be measured in minutes, but I suppose that could be configurable.
