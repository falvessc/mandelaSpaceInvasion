package actors;

import collision.CollisionDetection;
import events.EventResolution;
import events.commands.HeroShipShoot;
import utilities.*;
import game.Game;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HeroShip implements GraphicalShape, CollisionalShape, DynamicElement {
	
	// Configurações estáticas para desenho da nave
    public static int WIDTH;
    public static int HEIGHT;
    public static double DRAWING_SCALE;
    private static int DELTA_X;
    private static int SHOOT_COOLDOWN_UPDATE_TIME;

    // Variáveis globais da classe
    private final EventResolution eventResolution;
    private final Area currentShape;
    private long timeUntilShootingAvailable = 0;

    public HeroShip(EventResolution eventResolution) {
    	
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
			
			// Busca a lista de configuração da nave
			NodeList nodeList = doc.getElementsByTagName("heroShip");
			
			// Passa por todos os nós de configuração da nave
			for (int temp = 0; temp < nodeList.getLength(); temp++) {
				
				// Pega o nó de configuração em questão
				Node node = nodeList.item(temp);

				// Verifica se é um elemento de nó do XML
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					// Faz a conversão para coletar as informações
					Element eElement = (Element) node;
					
					// Atribui as configurações mapeadas no arquivo
					WIDTH = Integer.parseInt(eElement.getElementsByTagName("width").item(0).getTextContent());
					HEIGHT = Integer.parseInt(eElement.getElementsByTagName("height").item(0).getTextContent());
					DRAWING_SCALE = Double.parseDouble(eElement.getElementsByTagName("drawingScale").item(0).getTextContent());
					DELTA_X = Integer.parseInt(eElement.getElementsByTagName("deltaX").item(0).getTextContent());
					SHOOT_COOLDOWN_UPDATE_TIME = Integer.parseInt(eElement.getElementsByTagName("shootCooldownUpdateTime").item(0).getTextContent());
					
					// TODO: REMOVER
					System.out.println("Nave do Heroi");
					System.out.println("---------------------------------");
					System.out.println("Width : " + eElement.getElementsByTagName("width").item(0).getTextContent());
					System.out.println("Height : " + eElement.getElementsByTagName("height").item(0).getTextContent());
					System.out.println("drawingScale : " + eElement.getElementsByTagName("drawingScale").item(0).getTextContent());
					System.out.println("deltaY : " + eElement.getElementsByTagName("deltaX").item(0).getTextContent());
					System.out.println("shootCooldownUpdateTime : " + eElement.getElementsByTagName("shootCooldownUpdateTime").item(0).getTextContent());
					System.out.println("");
				}
			}
			
    	} catch (Exception e) {
    		// Tratamento de erros
    		System.out.println("Erro na criação da nave: " + e);
		}
    	
    	// Posiciona e desenha a nave do jogo
        Point location = new Point((int)(Game.CANVAS_WIDTH/2 - (WIDTH/2 * DRAWING_SCALE)), Game.CANVAS_HEIGHT - HEIGHT);
        this.eventResolution = eventResolution;
        this.currentShape = generateShape(location);
    }
    
    // Movimenta as coordenadas da nave cada vez que o usuário utiliza a seta para a esquerda
    public void MoveRight(){
        AffineTransform transform = new AffineTransform();
        if(CollisionDetection.IsShapeAtEdge_Right(this))
            transform.translate(Game.CANVAS_WIDTH - this.currentShape.getBounds2D().getMaxX() - 1, 0);
        else
            transform.translate(DELTA_X, 0);
        currentShape.transform(transform);
    }
    
    public void MoveLeft(){
    	// Movimenta as coordenadas da nave cada vez que o usuário utiliza a seta para a esquerda
        AffineTransform transform = new AffineTransform();
        if(CollisionDetection.IsShapeAtEdge_Left(this))
            transform.translate(1 - this.currentShape.getBounds2D().getMinX(), 0);
        else
            transform.translate(- DELTA_X, 0);
        currentShape.transform(transform);
    }

    @Override
    public void Update(){
    	// Atualiza o tempo de disparo de um novo projétil
        timeUntilShootingAvailable = timeUntilShootingAvailable - 1;
    }
    
    public void Shoot(){
    	// Verifica se já acabou o intervalo para que seja possível disparar novamente um tiro da nave
        if(timeUntilShootingAvailable <= 0){
        	// Dispara o projétil a partir da localidade específica da nave
            Rectangle2D shipBounds = this.currentShape.getBounds2D();
            Point projectileLocation = new Point((int)(shipBounds.getMinX()), (int)shipBounds.getMinY());
            eventResolution.Push(new HeroShipShoot(projectileLocation));
            
            // Seta novamente o intervalo para que seja realizado um novo disparo
            timeUntilShootingAvailable = SHOOT_COOLDOWN_UPDATE_TIME;
        }
    }

    @Override
    public void Paint(Graphics2D graphics){
        // Desenha a nave com a cor branca ao centro do panel
    	graphics.setColor(Color.white);
        Area heroShipDrawingShape = GetCollisionArea();
        graphics.fill(heroShipDrawingShape);
    }

    @Override
    public Area GetCollisionArea() {
        // Retorna informações de onde a nave está localizada
    	return currentShape;
    }

    private static Area generateShape(Point location){
        // Cria um novo retangulo que se tornará a nave posicionada de acordo com o parâmetro
    	Area area = new Area(new Rectangle(
                location.x, location.y,
                (int)(WIDTH*DRAWING_SCALE),(int)(HEIGHT*DRAWING_SCALE)));

        // Desenha a nave na posição inicial
        Arrays.stream(new Area[]{
                getSingleShapePeace(location, 40, 0, 20, 10),

                getSingleShapePeace(location, 0, 0, 20, 20),
                getSingleShapePeace(location, (WIDTH - 20), 0, 20, 20),

                getSingleShapePeace(location, 0, 6, 25, 2),
                getSingleShapePeace(location, (WIDTH - 25), 6, 25, 2),
                getSingleShapePeace(location, 0, 10, 25, 2),
                getSingleShapePeace(location, (WIDTH - 25), 10, 25, 2),
                getSingleShapePeace(location, 0, 14, 25, 2),
                getSingleShapePeace(location, (WIDTH - 25), 14, 25, 2),

                getSingleShapePeace(location, 0, 20, 30, 20),
                getSingleShapePeace(location, (WIDTH - 30), 20, 30, 20),
                getSingleShapePeace(location, 0, 40, 20, 10),
                getSingleShapePeace(location, (WIDTH - 20), 40, 20, 10),
                getSingleShapePeace(location, 0, 50, 10, 10),
                getSingleShapePeace(location, (WIDTH - 10), 50, 10, 10),

                getSingleShapePeace(location, 0, 76, 16, 5),
                getSingleShapePeace(location, (WIDTH - 16), 76, 16, 5),
                getSingleShapePeace(location, 40, 76, 20, 8),
        }).forEach(area::subtract);

        // Retorna a área desenhada
        return area;
    }
    private static Area getSingleShapePeace(Point location, int xPosition, int yPosition, int width, int height){
        // Desenha a nave de acordo com informações passadas por parâmetro
    	return new Area(new Rectangle(
                (int)(xPosition*DRAWING_SCALE) + location.x, (int)(yPosition*DRAWING_SCALE) + location.y,
                (int)(width*DRAWING_SCALE), (int)(height*DRAWING_SCALE)));
    }
}
