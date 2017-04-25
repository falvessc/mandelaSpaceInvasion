package events.commands;

import game.Game;
import utilities.Command;

public class MoveInvadersToNextLineAndChangeDirectionOfMovement implements Command {
    @Override
    public void Apply(Game game) {
    	// Movimenta todos as naves invasoras juntas
    	game.allInvaderShips
        .stream()
        .filter(invaderShip -> !invaderShip.IsGoingToChangeDirection())
        .forEach(invader -> {
            invader.ChangeDirectionOfMovement();
            invader.MoveToNextLine();
        });
    }
}
