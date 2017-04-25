package utilities;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;

public class SoundEffectPlayer {
	
	// Método para execução de áudios
    public static void Play(String filePath){
        try {
            // Efetua a execução do áudio passado por parâmetro
        	ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream in = cl.getResourceAsStream(filePath);
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        }
        
        catch (Exception e){
            // Tratamento de erros
        	System.out.println("Sound error:" + e.getMessage());
        }
    }
}
