package utilities;

import game.Game;

// Aplica comandos no game
public interface Command {
    void Apply(Game game);
}
