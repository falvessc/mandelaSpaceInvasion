package ui;

import game.Game;
import utilities.GraphicalShape;
import java.awt.*;
import java.awt.geom.Area;

public class GameOverScreenOverlay implements GraphicalShape{
    
	// Variável final do game
	private final Game game;
    
    // Metodo construtor
    public GameOverScreenOverlay(Game game) {
        this.game = game;
    }

    // Método para desenhar algo em tela
    @Override
    public void Paint(Graphics2D graphics) {
        
    	// Desenha a tela para exibir o resultado do final do jogo ao jogador
    	graphics.setColor(new Color(0, 0, 0, 175));
        Area background = new Area(new Rectangle(0, StatusRibbon.HEIGHT, Game.CANVAS_WIDTH, Game.CANVAS_HEIGHT - StatusRibbon.HEIGHT));
        graphics.fill(background);

        // Configura a mensagem se o jogador ganhou na tela
        graphics.setColor(Color.white);
        graphics.setFont(new Font("Arial Black", Font.PLAIN, 25));
        String message = game.PlayerWon ? "GANHOU MANOOOO" : "PERDEU PLAYBOY";
        graphics.drawString(message, Game.CANVAS_WIDTH/2 - 135, (int)(Game.CANVAS_HEIGHT*0.4));   
    }
}
