package utilities;

import game.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    
	// Cria um evento para ficar "escutando" as teclas que o usuário utiliza no teclado
	public InputHandler(Game game){
        game.addKeyListener(this);
    }

    public class Key{
    	// Verifica se o usuário pressionou alguma tecla no teclado
    	private boolean isKeyDown = false;
        public void toggle(boolean isKeyDown){
            this.isKeyDown = isKeyDown;
        }
        public boolean isKeyDown(){
            return isKeyDown;
        }
    }
    
    // Configura as teclas do jogo (esquerda, direta e espaço)
    public Key left = new Key();
    public Key right = new Key();
    public Key space = new Key();

    public void keyTyped(KeyEvent e) {
    }

    // Evento para capturar tecla
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }

    public void toggleKey(int keyCode, boolean isPressed){
    	// Verifica qual tecla específica o usuário está pressionando
    	if(keyCode == KeyEvent.VK_D)
            right.toggle(isPressed);
        if(keyCode == KeyEvent.VK_A)
            left.toggle(isPressed);
        if(keyCode == KeyEvent.VK_SPACE)
            space.toggle(isPressed);
    }
}