package actors;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.File;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import game.*;
import utilities.CollisionalShape;
import utilities.DynamicElement;
import utilities.GraphicalShape;

public class InvaderShip implements GraphicalShape, CollisionalShape, DynamicElement {
    
	// Configurações estáticas para desenho da nave invasora
	public static int WIDTH;
    public static int HEIGHT;
    public static double DRAWING_SCALE;
    private static int MOVEMENT_COOLDOWN_UPDATE_TIME;
    private static Color COLOR;
    private static double CONSTANT_SPEED_UP;

    // Variáveis globais
    private double delta_X = 0.5;
    private Area currentShape;
    private boolean willChangeDirectionAfterCooldown = false;
    private long timeUntilNextMoveAvailable = 0;
    
    public InvaderShip(int row, int column){
    	
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
			
			// Busca a lista de configuração da nave invasora
			NodeList nodeList = doc.getElementsByTagName("invaderShip");
			
			// Passa por todos os nós de configuração da nave invasora
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
					MOVEMENT_COOLDOWN_UPDATE_TIME = Integer.parseInt(eElement.getElementsByTagName("movementCooldownUpdateTime").item(0).getTextContent());
					COLOR = Color.decode(eElement.getElementsByTagName("color").item(0).getTextContent());
					CONSTANT_SPEED_UP = Double.parseDouble(eElement.getElementsByTagName("constantSpeedUp").item(0).getTextContent());
					
					// TODO: REMOVER
					System.out.println("Nave do Invasora");
					System.out.println("---------------------------------");
					System.out.println("Width : " + eElement.getElementsByTagName("width").item(0).getTextContent());
					System.out.println("Height : " + eElement.getElementsByTagName("height").item(0).getTextContent());
					System.out.println("drawingScale : " + eElement.getElementsByTagName("drawingScale").item(0).getTextContent());
					System.out.println("movementCooldownUpdateTime : " + eElement.getElementsByTagName("movementCooldownUpdateTime").item(0).getTextContent());
					System.out.println("color : " + eElement.getElementsByTagName("color").item(0).getTextContent());
					System.out.println("constantSpeedUp : " + eElement.getElementsByTagName("constantSpeedUp").item(0).getTextContent());
					System.out.println("");
				}
			}
			
    	} catch (Exception e) {
    		// Tratamento de erros
    		System.out.println("Erro na criação da nave invasora: " + e);
		}
    	
        Point location = new Point(
            column * Game.INVADER_COLUMN_WIDTH +  Game.INVADER_WINDOW_MARGIN_LEFT,
            row * Game.INVADER_ROW_HEIGHT + Game.INVADER_WINDOW_MARGIN_TOP);
        this.currentShape = GenerateShape(location);
    }

    public void Update(){
        this.delta_X = this.delta_X + Math.signum(this.delta_X)*CONSTANT_SPEED_UP;
        if(timeUntilNextMoveAvailable <= 0){
            if(this.willChangeDirectionAfterCooldown){
                this.delta_X = - this.delta_X;
                this.willChangeDirectionAfterCooldown = false;
            }

            AffineTransform transform = new AffineTransform();
            transform.translate(delta_X, 0);
            currentShape.transform(transform);
            timeUntilNextMoveAvailable = MOVEMENT_COOLDOWN_UPDATE_TIME + 1;
        }
        timeUntilNextMoveAvailable = timeUntilNextMoveAvailable - 1;
    }

    public void MoveToNextLine(){
        AffineTransform transform = new AffineTransform();
        transform.translate(0, Game.INVADER_NEXT_LINE_HEIGHT);
        currentShape.transform(transform);
    }
    public void ChangeDirectionOfMovement(){
        this.willChangeDirectionAfterCooldown = true;
    }

    public boolean IsGoingToChangeDirection(){
        return this.willChangeDirectionAfterCooldown;
    }

    public Point GetLocation(){
        return new Point((int)currentShape.getBounds2D().getCenterX(), (int)currentShape.getBounds2D().getCenterY());
    }
    @Override
    public void Paint(Graphics2D graphics) {
        graphics.setColor(COLOR);
        Area heroShipDrawingPoints = currentShape;
        graphics.fill(heroShipDrawingPoints);
    }

    @Override
    public Area GetCollisionArea() {
        return currentShape;
    }

    private static Area GenerateShape(Point location){
        Area area = new Area(new Rectangle(
                location.x, location.y,
                (int)(WIDTH*DRAWING_SCALE),(int)(HEIGHT*DRAWING_SCALE)));

        Arrays.stream(new Area[]{
                getSingleShapePeace(location, 30, 40, 10, 10),
                getSingleShapePeace(location, 70, 40, 10, 10),

                getSingleShapePeace(location, 30, 0, 50, 10),
                getSingleShapePeace(location, 40, 10, 30, 10),
                getSingleShapePeace(location, 30, 60, 50, 10),
                getSingleShapePeace(location, 50, 70, 10, 10),

                getSingleShapePeace(location, 0, 0, 20, 10),
                getSingleShapePeace(location, (WIDTH - 20), 0, 20, 10),

                getSingleShapePeace(location, 0, 10, 30, 10),
                getSingleShapePeace(location, (WIDTH - 30), 10, 30, 10),

                getSingleShapePeace(location, 0, 20, 20, 10),
                getSingleShapePeace(location, (WIDTH - 20), 20, 20, 10),

                getSingleShapePeace(location, 0, 30, 10, 10),
                getSingleShapePeace(location, (WIDTH - 10), 30, 10, 10),

                getSingleShapePeace(location, 10, 50, 10, 10),
                getSingleShapePeace(location, (WIDTH - 10 - 10), 50, 10, 10),

                getSingleShapePeace(location, 10, 60, 10, 10),
                getSingleShapePeace(location, (WIDTH - 10 - 10), 60, 10, 10),

                getSingleShapePeace(location, 0, 70, 30, 10),
                getSingleShapePeace(location, (WIDTH - 30), 70, 30, 10),
        }).forEach(area::subtract);

        return area;
    }
    private static Area getSingleShapePeace(Point location, int xPosition, int yPosition, int width, int height){
        return new Area(new Rectangle(
                (int)(xPosition*DRAWING_SCALE) + location.x, (int)(yPosition*DRAWING_SCALE) + location.y,
                (int)(width*DRAWING_SCALE), (int)(height*DRAWING_SCALE)));
    }
}