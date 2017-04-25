package actors;

import utilities.CollisionalShape;
import utilities.DynamicElement;
import utilities.GraphicalShape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HeroProjectile implements GraphicalShape, CollisionalShape, DynamicElement {
    
	// Configurações estáticas para o tamanho do tiro disparado pela nave
	private static int WIDTH;
    private static int HEIGHT;
    private static double DRAWING_SCALE;
    private static int DELTA_Y;
    private static Color COLOR;
    private static boolean propertiesRead = false;

    // Area para desenho do tiro disparado pela nave
    private final Area shape;

    // Método construtor
    public HeroProjectile(Point location){
    	
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
				
				// Busca a lista de configuração do projétil da nave
				NodeList nodeList = doc.getElementsByTagName("heroProjectile");
				
				// Passa por todos os nós de configuração do projétil da nave
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
						System.out.println("Heroi Projétil");
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
	    		System.out.println("Erro na criação do projétil para o heroi: " + e);
				
			}
    	}
    	
    	// Desenha uma área para movimentos do projétil da nave de acordo com configurações
        this.shape = new Area(new Rectangle(
                location.x, location.y,
                (int)(WIDTH * DRAWING_SCALE), (int)(HEIGHT * DRAWING_SCALE)));
    }

    @Override
    public void Update(){
    	// Atualiza as coordenadas da nave
        AffineTransform transformation = new AffineTransform();
        transformation.translate(0, - DELTA_Y);
        this.shape.transform(transformation);
    }

    @Override
    public Area GetCollisionArea() {
        // Pega a área específica para o projétil disparado
    	return this.shape;
    }
    
    @Override
    public void Paint(Graphics2D graphics) {
        // Seta a cor e area para o projétil disparado pela nave
    	graphics.setColor(COLOR);
        graphics.fill(GetCollisionArea());
    }
}
