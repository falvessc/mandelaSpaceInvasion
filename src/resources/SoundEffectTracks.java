package resources;

import java.util.HashMap;
import java.util.Map;

public class SoundEffectTracks {
    
	// Enumera as possibilidades de sons disponíveis
	public enum Track {
        IntroSound,
        HeroShoot,
        InvaderShoot,
        InvaderExplosion,
        HeroWin,
        InvaderWin
    }

	// Mapeia cada tipo de som que tocada quando acontecer eventos específicos
    private static Map<Track, String> trackToPathMap =
        new HashMap<Track, String>(){{
        	put(Track.IntroSound, "resources/partiu-mandela-v2.wav");
            put(Track.HeroShoot, "resources/hero-shoot.wav");
            put(Track.InvaderShoot, "resources/laser.wav");
            put(Track.InvaderExplosion, "resources/invader-explosion.wav");
            put(Track.HeroWin, "resources/rap_das_armas.wav");
            put(Track.InvaderWin, "resources/tente-outra-vez.wav");
        }};
    
    // Retorna informações sobre os audios configurados no jogo    
    public static String GetTrackPath(Track track){
        return trackToPathMap.get(track);
    }
}
