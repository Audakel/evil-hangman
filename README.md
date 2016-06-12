# evil-hangman
Evil Hangman is a program that actively cheats at Hangman. Instead of
choosing a single word that the player tries to guess, the program maintains a set of words that
it continuously pares down. It does the latter in such a way as to minimize the player’s chance
of winning.
For this program we assume a word is a sequence of letters a­z. We also assume that letter
comparisons are case­insensitive. Thus, ‘a’ matches either ‘a’ or ‘A’, and ‘A’ matches either ‘a’
or ‘A’.
