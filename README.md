# Pacman-like-game
A pacman inspired game where the player is chased by ghosts and the goal is to score as many points as possible by staying alive as long as the player can.

This project was part of Cal Poly SLO's CSC 203 final project.

To play the game, use the arrow keys to direct the player entity (pacman).

1. World event is triggered by mouse click on the virtual world.

2. After mouse is clicked, a 3x3 area around the clicked area turns from grass to flowers. Then a fruit spawns and rushes
towards the tombstone in the middle of the world.

3. The ghost nearest to the clicked point becomes an orange ghost and it also goes slower. Once fruit reaches tombstone,
it becomes a red ghost.

4. The new entity is the fruit. It uses A* to determine the best path it needs to take to reach the tombstone and follows
that path.
