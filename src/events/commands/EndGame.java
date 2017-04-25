package events.commands;

import game.Game;
import resources.SoundEffectTracks;
import utilities.Command;
import utilities.SoundEffectPlayer;

public class EndGame implements Command {
    
	// Variável que indica se o jogador ganhou
	private final boolean playerWon;
    
	// Construtor para verificação do fim do jogo
    public EndGame(boolean playerWon) {
        this.playerWon = playerWon;
    }

    @Override
    public void Apply(Game game) {
    	
    	// Verifica se o jogador ganhou
        if(playerWon){
        	// Emite som da vitória pelo heroi
        	SoundEffectPlayer.Play(SoundEffectTracks.GetTrackPath(SoundEffectTracks.Track.HeroWin));
        }else{
        	// Emite som da vitória pelo invasor
        	SoundEffectPlayer.Play(SoundEffectTracks.GetTrackPath(SoundEffectTracks.Track.InvaderWin));
        }
    	
        // Aplica configurações do fim de jogo
    	game.IsGameOver = true;
        game.EndTime = java.time.LocalDateTime.now();
        game.PlayerWon = playerWon;
    }
}
