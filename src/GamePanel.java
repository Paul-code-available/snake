import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import utils.AppFont;

import javax.swing.*;
import javax.swing.text.html.Option;

public class GamePanel extends JPanel {

	// Dimensiones del área de juego
	static final int WIDTH = 600;
	static final int HEIGHT = 600;

	// Tamaño de cada celda del tablero (grid)
	static final int UNIT_SIZE = 25;

	// Timer que controla el ciclo del juego
	Timer timer;

    int delay = 150;

	// contador de comidas
	int contadorComidas = 0;

    int contadorPuntaje = 0;
	
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
		JLabel lblPuntaje = new JLabel("Puntaje: " + contadorPuntaje);
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
            comerManzana();
            colisionCabezaConCuerpo();
            delimitationGame();
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
	}
	
	public void comerManzana() {
		if ((comidaX == snakeBody.get(0).x) && (comidaY == snakeBody.get(0).y)) {
			comidaX = (int) (Math.random() * 23 + 1) * 25;
			comidaY = (int) (Math.random() * 23 + 1) * 25;
			
			snakeBody.add(new Point(snakeBody.getLast().x - 25, snakeBody.getLast().y));

            if(contadorComidas > 5){
                contadorComidas = 0;
            }
			contadorComidas ++;
            contadorPuntaje++;
            aumentarVelocidad();
			
		}
	}
	
	public void colisionCabezaConCuerpo() {
		for (int i = 1; i < snakeBody.size(); i++) {
			if (snakeBody.get(0).x == snakeBody.get(i).x && snakeBody.get(0).y == snakeBody.get(i).y) {
                mensajeGameOver();
				timer.stop();
				resetGame();
				timer.start();
			}
		}
		
	}
	
	public void delimitationGame(){
        if ((snakeX > 600 || snakeX < 0 ) || (snakeY > 600 || snakeY < 0)){
            mensajeGameOver();
            timer.stop();
            resetGame();
            timer.start();

        }
    }

    public void resetGame(){
        snakeX = 250;
        snakeY = 100;

        delay = 150;

        contadorComidas = 0;
        contadorPuntaje = 0;

        direction = 'R';
        ultimaDireccion = 'R';

        snakeBody.clear();
        snakeBody.add(new Point(250, 100));
        snakeBody.add(new Point(225, 100));
        snakeBody.add(new Point(200, 100));

        comidaX = ((int) (Math.random() * 23) + 1) * 25;
        comidaX = ((int) (Math.random() * 23) + 1) * 25;
    }

    public void aumentarVelocidad(){
        if (contadorComidas == 5){
            delay -= 15;
            timer.setDelay(delay);
        }
    }



    public void mensajeGameOver(){
        JFrame frame = new JFrame();
        ImageIcon icono= new ImageIcon("src/img/icono.png");

        Image escalarIcono = icono.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        ImageIcon iconoFinal = new ImageIcon(escalarIcono);

        JLabel mensajes = new JLabel("<html><center>Puntaje:" + contadorPuntaje + "<br><br>¿Desea volver a jugar?</center></html>");
        mensajes.setFont(AppFont.medium());
        mensajes.setHorizontalAlignment(SwingConstants.CENTER);

        int opcion = JOptionPane.showOptionDialog(frame, mensajes, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, iconoFinal, null, null);

        if (opcion == JOptionPane.NO_OPTION){
            System.exit(0);
        }
    }
}