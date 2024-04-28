# UGarden 2024

**Participants:** Omar Harchi, Yann Bricage

## Introduction

UGarden est un jeu où un jardinier doit retrouver son ami perdu HedgeHog. Pour cela, le jardinier devra s'aventurer dans les lieux les plus sombres et dangereux de la map, lutter contre des frelons et trouver des clés pour accéder à de nouvelles zones où son petit ami pourrait se trouver.

## Instructions

- Pour lancer le jeu, il suffit de sélectionner "Open file" et de choisir la map que tu veux jouer. Le projet a déjà deux maps, et une par défaut qui est juste là pour tester quelques fonctionnalités.
- Pour se déplacer, utilise les touches fléchées {Key Arrows}.
- Les frelons apparaissent toutes les 7 secondes (10 de base, mais sinon ce n'est pas amusant) dans leurs nids. En même temps qu'un frelon apparaît, un insecticide va aussi apparaître.
- L'insecticide sert à se protéger des frelons. Si tu touches un frelon, ce frelon va te retirer 30 d'énergie, à moins que tu aies un insecticide avec toi. Si un frelon te touche, il meurt instantanément.
- Les pommes te permettent de récupérer de l'énergie.
- Les pommes mangées vont te faire perdre de l'énergie en fonction de l'endroit où tu marches (voir `gardener.java` `DoMove` ligne 152).
- Pour ouvrir une porte, il te faudra au moins une clé.
- Les clés sont disséminées un peu partout dans la map.
- Tu peux toujours retourner dans la map précédente.

## Données additionnelles pour notre enseignante de TD

- Il y a deux niveaux disponibles : un compressé et un autre clair. Le compressé est la map originale, vous pouvez l'utiliser si vous préférez pour tester toutes les fonctionnalités.
- L'autre version, claire, est une autre map qui contient 2 nouveaux niveaux que j'ai rajoutés (dont un EASTER EGG).
- Quand vous entrez dans une porte, tous les frelons vont disparaître, pareil pour l'insecticide, pour rester dans l'originalité des jeux où les ennemis disparaissent lorsqu'on rentre dans un autre niveau.
- Les frelons ne peuvent pas aller l'un sur l'autre.
- J'ai aussi modifié la bibliothèque TIMER, car elle ne fonctionnait pas bien pour réinitialiser le compteur. J'ai modifié la méthode `start()` du timer pour inclure aussi `now`, car pour réinitialiser le compteur, il faut aussi `startTime`.
- De plus, j'ai aussi rajouté `setGameEngine` sur `Game`, pour l'appeler ensuite depuis `gameLauncherView`, comme ça je peux appeler des méthodes de `GameEngine` depuis `Hornet`, telle que `isPositionOccupiedByHornet`, pour que le frelon puisse savoir s'il y avait déjà un frelon sur cette position.
- Normalement, tout fonctionne. Si quelque chose ne marche pas, je n'ai pas dû le remarquer.