package actors;

import utilities.CollisionalShape;
import utilities.DynamicElement;
import utilities.GraphicalShape;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InvaderProjectile implements GraphicalShape, CollisionalShape, DynamicElement {
    
	// Configurações estáticas para o tamanho do tiro disparado pela invasor
	private static int WIDTH;
    private static int HEIGHT;
    private static double DRAWING_SCALE;
    private static int DELTA_Y;
    private static Color COLOR;
    private static boolean propertiesRead = false;
    
    // Variáveis globais
    private final Point location;
    private final int FRAME_RATE = 2;
    private int timeUntilNextFrame = 0;
    private int currentFrameIndex = 0;
    private final Area[] frames;

    // Método construtor
    public InvaderProjectile(Point location){
        
    	// Verifica se as propriedades já foram carregadas
    	if (!propertiesRead){
    		
    		// Informa que as propriedades já foram carregadas no primeiro
    		propertiesRead = true;
    		
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
				
				// Busca a lista de configuração do projétil do invasor
				NodeList nodeList = doc.getElementsByTagName("invaderProjectile");
				
				// Passa por todos os nós de configuração do projétil do invasor
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
						DELTA_Y = Integer.parseInt(eElement.getElementsByTagName("deltaY").item(0).getTextContent());
						COLOR = Color.decode(eElement.getElementsByTagName("color").item(0).getTextContent());
						
						// TODO: REMOVER
						System.out.println("Invasor Projétil");
						System.out.println("---------------------------------");
						System.out.println("Width : " + eElement.getElementsByTagName("width").item(0).getTextContent());
						System.out.println("Height : " + eElement.getElementsByTagName("height").item(0).getTextContent());
						System.out.println("drawingScale : " + eElement.getElementsByTagName("drawingScale").item(0).getTextContent());
						System.out.println("deltaY : " + eElement.getElementsByTagName("deltaY").item(0).getTextContent());
						System.out.println("color : " + eElement.getElementsByTagName("color").item(0).getTextContent());
						System.out.println("");
					}
				}
				
	    	} catch (Exception e) {
	    		// Tratamento de erros
	    		System.out.println("Erro na criação do projétil para o invasor: " + e);
				
			}
    	}
    	
    	// Preenche informações sobre a localidade e animação do projétil da nave invasora tentando acertar a nave heroi
    	this.location = location;
        frames = new Area[]{
            getAnimationFrame(0, location),
            getAnimationFrame(1, location)
        };
    }

    @Override
    public void Update(){
        // Atualiza coordenadas sobre o projétil da nave invasora
    	this.location.setLocation(this.location.getX(), this.location.getY() + DELTA_Y);
        this.timeUntilNextFrame = this.timeUntilNextFrame - 1;
        
        // Se já acabou o tempo projetado para o próximo ataque da nave invasora
        if(this.timeUntilNextFrame <= 0){
            currentFrameIndex = (currentFrameIndex + 1) % frames.length;
            timeUntilNextFrame = FRAME_RATE;
        }
        
        // Enquanto hover áreas do projétil
        for (Area area : frames) {
        	// Vai atualizando as coordenadas
            AffineTransform transformation = new AffineTransform();
            transformation.translate(0, DELTA_Y);
            area.transform(transformation);
        }
    }

    @Override
    public Area GetCollisionArea() {
        // Retorna informações sobre a área de colisão da nave invasora
    	return frames[currentFrameIndex];
    }
    
    @Override
    public void Paint(Graphics2D graphics) {
    	// Desenha o projétil da nave invasora
        graphics.setColor(COLOR);
        graphics.fill(frames[currentFrameIndex]);
    }

    private static Area getAnimationFrame(int frameIndex, Point location){
        Area area = new Area();
        
        // Desenha o projétil sincronizado tanto pela esquerda quanto pela direita
        if(frameIndex == 0){
            area.add(new Area(new Rectangle(
                location.x, location.y + 2,
                1, (int)(HEIGHT*DRAWING_SCALE) - 4
            )));

            area.add(new Area(new Rectangle(
                location.x + 2, location.y,
                1, (int)(HEIGHT* DRAWING_SCALE)
            )));
            area.add(new Area(new Rectangle(
                    location.x + 4, location.y,
                    1, (int)(HEIGHT* DRAWING_SCALE)
            )));
        }

        if(frameIndex == 1){
            area.add(new Area(new Rectangle(
                    location.x + 3, location.y,
                    1, (int)(HEIGHT* DRAWING_SCALE)
            )));
            area.add(new Area(new Rectangle(
                    location.x + 5, location.y,
                    1, (int)(HEIGHT* DRAWING_SCALE)
            )));
            area.add(new Area(new Rectangle(
                    location.x + 7, location.y + 2,
                    1, (int)(HEIGHT* DRAWING_SCALE) - 4
            )));
        }

        // Retorna a área do projeto desenhado
        return area;
    }
}
