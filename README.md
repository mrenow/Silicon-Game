# My Year 12 Major Project

Basically a clone of the Zachtronics circuit building flash game [Enginer of the People](https://www.zachtronics.com/kohctpyktop-engineer-of-the-people/)

One key feature about Zachtronics' game that I really didnt like is that it ended. So I made a sandbox version, with a few modified mechanics and some extra features.
The differences are primarily:
- Redstone-like connectivity system, where adjacent tiles always interact if they can.
- Ability to rotate and flip selections.
- User placeable power sources and scopes.
- Ability to pause and step logic simulation
- Lots of datastructure optimizations to handle such a large grid space, hoping that I can eventually build a small computer in it.

Heres a circuit which runs the collatz function `(n) => n%2==1 ? 3n+1 : n/2` every clock cycle.

![2022-08-17 02-17-20](https://user-images.githubusercontent.com/18377830/184942688-b492d9ac-e74f-48e7-9bc2-e134a2d385d7.gif)
