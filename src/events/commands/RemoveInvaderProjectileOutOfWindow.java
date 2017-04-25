package events.commands;

import actors.InvaderProjectile;
import game.Game;
import utilities.Command;

public class RemoveInvaderProjectileOutOfWindow implements Command {
    private final InvaderProjectile invaderProjectileOutOfWindow;

    // Verifica qual projétil que será removido
    public RemoveInvaderProjectileOutOfWindow(InvaderProjectile invaderProjectileOutOfWindow){
        this.invaderProjectileOutOfWindow = invaderProjectileOutOfWindow;
    }

    @Override
    public void Apply(Game game) {
    	// Remove o projétil da nave invasora para fora do painel
        int indexOfDeadProjectile = game.allInvaderProjectiles.indexOf(invaderProjectileOutOfWindow);
        game.allInvaderProjectiles.remove(indexOfDeadProjectile);
    }
}
