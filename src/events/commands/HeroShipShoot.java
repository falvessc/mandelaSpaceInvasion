package events.commands;

import actors.HeroShip;
import actors.HeroProjectile;
import game.Game;
import resources.SoundEffectTracks;
import utilities.Command;
import utilities.SoundEffectPlayer;

import java.awt.*;

public class HeroShipShoot implements Command {
    private final Point heroShipLocation;
    
    // Cria um projétil a partir da localização atual da nave heroi
    public HeroShipShoot(Point heroShipLocation) {
        this.heroShipLocation = heroShipLocation;
    }

    @Override
    public void Apply(Game game) {
        
    	// Cria coordenadas para disparo de um projétil da nave heroi
    	Point projectileLocation = new Point(
            (int)(heroShipLocation.getX() + HeroShip.WIDTH / 2 * HeroShip.DRAWING_SCALE),
            (int)(heroShipLocation.getY()));
        game.allHeroProjectiles.add(new HeroProjectile(projectileLocation));
        SoundEffectPlayer.Play(SoundEffectTracks.GetTrackPath(SoundEffectTracks.Track.HeroShoot));
    }
}
