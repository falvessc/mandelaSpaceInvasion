package ui;

import game.Game;
import utilities.GraphicalShape;

import java.awt.*;
import java.awt.geom.Area;

public class StatusRibbon implements GraphicalShape{
    private static final Color COLOR_GRAY = Color.decode("#282828");
    private static final Color COLOR_GREEN = Color.decode("#A6E22E");
    private static final Color COLOR_YELLOW = Color.decode("#E6DB74");
    public static final int HEIGHT = 30;
    public static final int WIDTH = Game.CANVAS_WIDTH;
    
    public StatusRibbon(Game game) {
    }

    @Override
    public void Paint(Graphics2D graphics) {
        graphics.setColor(COLOR_GRAY);
        Area background = new Area(new Rectangle(0, 0, Game.CANVAS_WIDTH, HEIGHT));
        graphics.fill(background);
        graphics.setFont(new Font("Consolas", Font.PLAIN, 12));
        graphics.setColor(COLOR_YELLOW);
        graphics.setColor(COLOR_GREEN);
        graphics.drawString("Mandela Space Invasion", WIDTH/2 - 85, 20);
    }
}
