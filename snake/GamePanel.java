package snake;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel {

	// Dimensiones del área de juego
	static final int WIDTH = 600;
	static final int HEIGHT = 600;

	// Tamaño de cada celda del tablero (grid)
	static final int UNIT_SIZE = 25;

	// Timer que controla el ciclo del juego
	Timer timer;

	// contador de comidas
	int contadorComidas = 0;
	
	// Coordenadas de la cabeza de la serpiente
	int snakeX;
	int snakeY;
	
	// coordenadas de la comida
	int comidaX;
	int comidaY;

	// Dirección actual de movimiento de la serpiente
	// U = Up, D = Down, L = Left, R = Right
	char direction;
	
	// ultima direccion del movimiento de la serpiente
	char ultimaDireccion;

	// Lista enlazada que almacena todas las posiciones del cuerpo de la serpiente
	LinkedList<Point> snakeBody;
	LinkedList<Point> almacenComida;

	public GamePanel() {

		// Posición inicial de la cabeza de la serpiente
		snakeX = 250;
		snakeY = 100;
		
		// posicion inicial de la manzana
		comidaX = (int) (Math.random() * 23 + 1) * 25;
		comidaY = (int) (Math.random() * 23 + 1) * 25;

		// Dirección inicial de movimiento
		direction = 'R';

		// Crear la lista que almacenará el cuerpo de la serpiente
		snakeBody = new LinkedList<Point>();

		// Agregar los primeros segmentos de la serpiente
		snakeBody.add(new Point(250, 100));
		snakeBody.add(new Point(225, 100));
		snakeBody.add(new Point(200, 100));		

		// Configurar tamaño del panel
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setLayout(null);

		// Color de fondo
		this.setBackground(Color.black);
		
		// panel de puntaje
		Panel panelPuntaje = new Panel();
		panelPuntaje.setBounds(550, 0, 70, 50);
		panelPuntaje.setBackground(Color.BLUE);
		add(panelPuntaje);
		
		// etiqueta de puntaje
		JLabel lblPuntaje = new JLabel("Puntaje: " + contadorComidas);
		panelPuntaje.add(lblPuntaje);

		// Permitir que el panel reciba eventos de teclado
		this.setFocusable(true);

		// Evita que las teclas de navegación (como el TAB) cambien el foco entre componentes
		setFocusTraversalKeysEnabled(false);

		// Solicitar el foco cuando el panel ya esté visible
		SwingUtilities.invokeLater(() -> requestFocusInWindow());

		// Listener para detectar las teclas presionadas
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				// Cambiar la dirección dependiendo de la tecla presionada
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					direction = 'L';
					break;

				case KeyEvent.VK_RIGHT:
					direction = 'R';
					break;

				case KeyEvent.VK_UP:
					direction = 'U';
					break;

				case KeyEvent.VK_DOWN:
					direction = 'D';
					break;
				}
			}
		});

		// Timer que ejecuta el ciclo del juego cada 150 ms
		timer = new Timer(150, e -> {
			move();     // Actualiza la posición de la serpiente
			repaint();  // Redibuja el panel (se ejecuta paintComponent)
		});

		timer.start(); //Inicia el timer, para detenerlo se puede usar timer.stop();
	}

	// Método encargado de dibujar los elementos del juego
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Recorrer todas las partes de la serpiente
		for (int i = 0; i < snakeBody.size(); i++) {
			
			// La cabeza se dibuja de color amarillo
			if (i == 0) {
				g.setColor(Color.YELLOW);
			} 
			// El resto del cuerpo se dibuja de color verde
			else {
				g.setColor(Color.GREEN);
			}

			// Dibujar cada parte de la serpiente
			g.fillRoundRect(
				snakeBody.get(i).x,
				snakeBody.get(i).y,
				UNIT_SIZE,
				UNIT_SIZE,
				5,
				5
			);
		}
		
		// dibuja la manzana
		g.setColor(Color.RED);
		g.fillRoundRect(comidaX, comidaY, UNIT_SIZE, UNIT_SIZE, 5, 5);
	}

	// Método que actualiza la posición de la serpiente
	public void move() {

		// Cambiar la posición de la cabeza dependiendo de la dirección
		switch (direction) {
		case 'U':
			if (ultimaDireccion != 'D') {
				snakeY -= UNIT_SIZE;
				ultimaDireccion = 'U';
			} else {
				snakeY += UNIT_SIZE;
			}
			break;
			
		case 'D':
			if (ultimaDireccion != 'U') {
				snakeY += UNIT_SIZE;
				ultimaDireccion = 'D';
			} else {
				snakeY -= UNIT_SIZE;
			}
			break;
		case 'L':
			if (ultimaDireccion != 'R') {
				snakeX -= UNIT_SIZE;
				ultimaDireccion = 'L';
			} else {
				snakeX += UNIT_SIZE;
			}
			break;

		case 'R':
			if (ultimaDireccion != 'L') {
				snakeX += UNIT_SIZE;
				ultimaDireccion = 'R';
			} else {
				snakeX -= UNIT_SIZE;
			}
			
			break;
		}

		// Agregar una nueva cabeza en la posición actual
		snakeBody.addFirst(new Point(snakeX, snakeY));

		// Eliminar el último elemento para mantener el mismo tamaño
		snakeBody.removeLast();
		
		// cambia la ubicacion de la manzana y añade un elemento a la serpiente
		comerManzana();
		
		// termina el juego si la cabeza choca con cualquier parte del cuerpo 
		colisionCabezaConCuerpo();
		
		// si la cabeza toca los limites del panel se termina el juego
		delimitationGame();
	}
	
	public void comerManzana() {
		if ((comidaX == snakeBody.get(0).x) && (comidaY == snakeBody.get(0).y)) {
			comidaX = (int) (Math.random() * 23 + 1) * 25;
			comidaY = (int) (Math.random() * 23 + 1) * 25;
			
			snakeBody.add(new Point(snakeBody.getLast().x - 25, snakeBody.getLast().y));
			contadorComidas += 1;
			
		}
	}
	
	public void colisionCabezaConCuerpo() {
		for (int i = 1; i < snakeBody.size(); i++) {
			if (snakeBody.get(0).x == snakeBody.get(i).x && snakeBody.get(0).y == snakeBody.get(i).y) {
				timer.stop();
				resetGame();
				timer.start();
				break;
			}
		}
		
	}
	
	public void delimitationGame(){
        if ((snakeX > 600 || snakeX < 0 ) || (snakeY > 600 || snakeY < 0)){
            timer.stop();
            resetGame();
            timer.start();

        }
    }

    public void resetGame(){
        snakeX = 250;
        snakeY = 100;

        direction = 'R';
        ultimaDireccion = 'R';

        snakeBody.clear();
        snakeBody.add(new Point(250, 100));
        snakeBody.add(new Point(225, 100));
        snakeBody.add(new Point(200, 100));

        comidaX = ((int) (Math.random() * 23) + 1) * 25;
        comidaX = ((int) (Math.random() * 23) + 1) * 25;
    }
}