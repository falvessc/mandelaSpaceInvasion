package game;

import actors.HeroShip;
import actors.InvaderProjectile;
import actors.InvaderShip;
import actors.HeroProjectile;
import collision.CollisionDetection;
import events.EventResolution;
import events.commands.PlayIntroSound;
import events.commands.InvaderShipShoot;
import ui.GameOverScreenOverlay;
import ui.StatusRibbon;
import utilities.DynamicElement;
import utilities.GameTimer;
import utilities.GraphicalShape;
import utilities.InputHandler;
import vfx.Explosion;
import vfx.VfxManager;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class Game extends Canvas implements Runnable, GameTimer {
    public static final int CANVAS_WIDTH = 500;
    public static final int CANVAS_HEIGHT = 700;
    public static final int INVADER_COLUMN_WIDTH = 50;
    public static final int INVADER_ROW_HEIGHT = 50;
    public static final int INVADER_WINDOW_MARGIN_TOP = 50;
    public static final int INVADER_WINDOW_MARGIN_LEFT = 50;
    public static final int INVADER_NEXT_LINE_HEIGHT = 50;
    private static final int INITIAL_SHOOTING_DELAY = 250;
    private static String LEVEL;

    private JFrame frame;
    private boolean running = false;
    private int invaderShootingCooldownPeriod = 40;
    private long invaderShootingLastTime = 0;
    private final Color COLOR_BACKGROUND = new Color(35, 31, 32);
    private int rowLevel;
    private int columnLevel;

    public LocalDateTime EndTime;
    public boolean IsGameOver = false;
    public boolean PlayerWon = false;

    public final HeroShip heroShip;
    public final StatusRibbon statusRibbon;
    public final GameOverScreenOverlay gameOverScreenOverlay;
    public final List<HeroProjectile> allHeroProjectiles;
    public final List<InvaderShip> allInvaderShips;
    public final List<InvaderProjectile> allInvaderProjectiles;
    public final List<Explosion> allExplosionVFX;

    private final InputHandler input;
    private final CollisionDetection collisionDetection;
    private final EventResolution eventResolution;
    private final VfxManager vfxManager;

    public Game(){
    	try {
			
    		// Busca o arquivo de configuração para o Mandela Space Invasion
    		File xmlFile = new File("src/resources/init-config.xml");
    		
    		// Cria um recurso para leitura do arquivo de configuração em XML
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder documentBuilder;
			documentBuilder = dbFactory.newDocumentBuilder();
			
			// Efetua a leitura dos elementos do arquivo de configuração
			Document doc = documentBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			// Busca a lista de configuração do projétil do game
			NodeList nodeList = doc.getElementsByTagName("game");
			
			// Passa por todos os nós de configuração do projétil do game
			for (int temp = 0; temp < nodeList.getLength(); temp++) {
				
				// Pega o nó de configuração em questão
				Node node = nodeList.item(temp);

				// Verifica se é um elemento de nó do XML
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					// Faz a conversão para coletar as informações
					Element eElement = (Element) node;
					
					// Atribui as configurações mapeadas no arquivo
					LEVEL = eElement.getElementsByTagName("level").item(0).getTextContent();
					
					// TODO: REMOVER
					System.out.println("Game");
					System.out.println("---------------------------------");
					System.out.println("level : " + eElement.getElementsByTagName("level").item(0).getTextContent());
					System.out.println("");
				}
			}
			
    	} catch (Exception e) {
    		// Tratamento de erros
    		System.out.println("Erro na leitura do arquivo de configuração do game: " + e);
			
		}
    	
    	// Seta a largura e altura do frame do jogo
    	setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

    	// Efetua configurações do frame para execução do jogo
        frame = new JFrame("Mandela Space Invaders :: Loading");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        frame.setVisible(true);
        frame.add(this, BorderLayout.CENTER);

        // Cria as informações para criação do jogo
        this.input = new InputHandler(this);
        this.statusRibbon = new StatusRibbon(this);
        this.gameOverScreenOverlay = new GameOverScreenOverlay(this);
        this.eventResolution = new EventResolution(this);
        this.heroShip = new HeroShip(this.eventResolution);
        this.allHeroProjectiles = new ArrayList<HeroProjectile>();
        this.allInvaderShips = new ArrayList<InvaderShip>();
        
        // Verifica se o nível escolhido foi o fácil
        if(LEVEL.equals("easy")){
        	rowLevel = 4;
        	columnLevel = 6;
        	
        // Verifica se o nível escolhido foi o difícil	
        }else if (LEVEL.equals("hard")){
        	rowLevel = 6;
        	columnLevel = 8;
        	
        }else{
        	// Neste caso é o level normal
        	rowLevel = 5;
        	columnLevel = 7;
        }
        
        // Cria a quantidade de naves invasoras de acordo com o nível escolhido
        for (int row = 0; row < rowLevel; row++)
            for(int column = 0; column < columnLevel; column++)
                this.allInvaderShips.add(new InvaderShip(row, column));
        
        // Finaliza mais algumas configurações
        this.allInvaderProjectiles = new ArrayList<InvaderProjectile>();
        this.allExplosionVFX = new ArrayList<Explosion>();
        this.collisionDetection = new CollisionDetection(this, this.eventResolution);
        this.vfxManager = new VfxManager(this);
    }

    // Inicia uma thread para executar o jogo
    public synchronized void start(){
        this.running = true;
        new Thread(this).start();
    }

    public long GetCurrentUpdateCount(){
        return totalUpdateCount;
    }

    private long totalUpdateCount = 0;
    private static final int MILLIS_IN_SECOND = 1000;

    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = Math.pow(10D, 9) / 60;
        double delta = 0;

        while(running){
            long now = System.nanoTime();

            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;
            boolean shouldRender = false;

            while(delta >= 1){
                if(!IsGameOver){
                    ProcessInput();
                    Update();
                    this.totalUpdateCount++;
                }
                delta--;
                shouldRender = true;
            }

            if(shouldRender){
                Render();
            }
            
            frame.setTitle("Mandela Space Invaders");
        }
    }
    private void ProcessInput(){
        if(input.right.isKeyDown())
            heroShip.MoveRight();

        if(input.left.isKeyDown())
            heroShip.MoveLeft();

        if(input.space.isKeyDown())
            heroShip.Shoot();
    }

    private void Update(){
        UpdateDynamicElements();

        if(this.GetCurrentUpdateCount() == 30)
            this.eventResolution.Push(new PlayIntroSound());

        boolean isPastInitialDelay = (this.GetCurrentUpdateCount() - INITIAL_SHOOTING_DELAY) > 0;
        boolean isPastCooldownTime = (this.GetCurrentUpdateCount() - invaderShootingLastTime) > invaderShootingCooldownPeriod;
        if(isPastInitialDelay && isPastCooldownTime){
            eventResolution.Push(new InvaderShipShoot());
            invaderShootingLastTime = this.GetCurrentUpdateCount();
        }

        collisionDetection.Detect();
        eventResolution.Resolve();
        vfxManager.Update();
    }
    private void UpdateDynamicElements(){
        Stream
            .of(
                Arrays.asList(heroShip),
                allInvaderShips,
                allInvaderProjectiles,
                allHeroProjectiles,
                allExplosionVFX)
            .<DynamicElement>flatMap(dynamicElements -> dynamicElements.stream())
            .forEach(DynamicElement::Update);
    }

    private void Render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        Graphics graphics = bs.getDrawGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;

        graphics.setColor(COLOR_BACKGROUND);
        graphics.fillRect(0, 0, getWidth(), getHeight());

        Stream
            .of(
                allInvaderShips,
                allInvaderProjectiles,
                allHeroProjectiles,
                allExplosionVFX,
                Arrays.asList(heroShip, statusRibbon))
            .<GraphicalShape>flatMap(dynamicElements -> dynamicElements.stream())
            .forEach(shape -> shape.Paint(graphics2D));

        if(this.IsGameOver)
            gameOverScreenOverlay.Paint(graphics2D);

        bs.show();
        graphics2D.dispose();
    }

    public static void main(String[] args){
        new Game().start();
    }
}