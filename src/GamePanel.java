import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import utils.AppFont;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.sound.sampled.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.html.Option;

public class GamePanel extends JPanel {

	// Dimensiones del área de juego
	static final int WIDTH = 600;
	static final int HEIGHT = 600;

	// Tamaño de cada celda del tablero (grid)
	static final int UNIT_SIZE = 25;

    int inicioMapa = 25;

	// Timer que controla el ciclo del juego
	Timer timer;

    int delay = 150;

	// contador de comidas
	int contadorComidas = 0;

	// contador puntaje
	int contadorPuntaje = 0;
	JLabel lblPuntaje;
	
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

    boolean pausado = false;

	// Lista enlazada que almacena todas las posiciones del cuerpo de la serpiente
	LinkedList<Point> snakeBody;

    BufferedImage fondo;
    BufferedImage spriteSnake;
    BufferedImage cabezaActual;
    BufferedImage cabezaArriba;
    BufferedImage cabezaDerecha;
    BufferedImage cabezaAbajo;
    BufferedImage cabezaIzquierda;
    BufferedImage cuerpoActual;
    BufferedImage cuerpoHorizontal;
    BufferedImage cuerpoVertical;
    BufferedImage colaActual;
    BufferedImage colaArriba;
    BufferedImage colaAbajo;
    BufferedImage colaDerecha;
    BufferedImage colaIzquierda;
    BufferedImage curva1;
    BufferedImage curva2;
    BufferedImage curva3;
    BufferedImage curva4;
    Image imgManzana;
    ImageIcon iconoManzanaOriginal;
    Image imagenEscaladaIcono;
    ImageIcon iconoManzanaFinal;
    JLabel lblIcono;


    Point anterior;
    Point actual;
    Point siguiente;

    Clip musicaFondo;

	public GamePanel() {
        cargarMusica();
        reproducirMusica();

        // Posición inicial de la cabeza de la serpiente
		snakeX = 250;
		snakeY = 100;

		// Dirección inicial de movimiento
		direction = 'R';

		// Crear la lista que almacenará el cuerpo de la serpiente
		snakeBody = new LinkedList<Point>();

		// Agregar los primeros segmentos de la serpiente
		snakeBody.add(new Point(250, 100));
		snakeBody.add(new Point(225, 100));
		snakeBody.add(new Point(200, 100));

        setBackground(Color.black);

        generarManzana();

        cargarSprites();
        imgManzana = new ImageIcon("src/img/apple.png").getImage();

		// Configurar tamaño del panel
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setLayout(new BorderLayout());

		// panel de puntaje
        /*
		Panel panelPuntaje = new Panel();
		panelPuntaje.setBounds(530, 0, 50, 50);

        JPanel panelPrincipal = new JPanel();
		add(panelPuntaje);
         */

		// etiqueta de puntaje
		//lblPuntaje = new JLabel("0");
		//lblPuntaje.setPreferredSize(new Dimension(50, 50));
		//lblPuntaje.setForeground(Color.WHITE); // Texto blanco sobre fondo azul

		//panelPuntaje.add(lblPuntaje);

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
                    if (ultimaDireccion != 'R') {
                        direction = 'L';
                    }
                    break;

				case KeyEvent.VK_RIGHT:
                    if (ultimaDireccion != 'L') {
                        direction = 'R';
                    }
                    break;

				case KeyEvent.VK_UP:
                    if (ultimaDireccion != 'D') {
                        direction = 'U';
                    }
                    break;

				case KeyEvent.VK_DOWN:
                    if (ultimaDireccion != 'U') {
                        direction = 'D';
                    }
                    break;
				}

                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (pausado) {
                        timer.start();
                        reproducirMusica();
                    } else {
                        timer.stop();
                        detenerMusica();
                    }
                    pausado = !pausado;
                    repaint(); // para que se dibuje el overlay de pausa
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

        panelSuperior();
	}

	// Método encargado de dibujar los elementos del juego
	public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);


        Graphics2D g2 = (Graphics2D) g;
        float[] dash = {6f, 6f};

        g2.setStroke(new BasicStroke(
                1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0,
                dash,
                0
        ));
        for (int j = 25; j < getHeight(); j+=25){
            g.setColor(new Color(255, 255, 255, 30));

            g.drawLine(j, 25, j, 600);
            g.drawLine(0, j, 600, j);
        }


        // Recorrer todas las partes de la serpiente
        for (int i = 0; i < snakeBody.size(); i++) {

            // La cabeza se dibuja de color amarillo
            if (i == 0) {
                switch (direction) {
                    case 'U':
                        cabezaActual = cabezaArriba;
                        break;
                    case 'D':
                        cabezaActual = cabezaAbajo;
                        break;
                    case 'L':
                        cabezaActual = cabezaIzquierda;
                        break;
                    default:
                        cabezaActual = cabezaDerecha;
                }
                g.drawImage(cabezaActual, snakeBody.get(i).x, snakeBody.get(i).y, UNIT_SIZE, UNIT_SIZE, null);
            }
            else if (i != snakeBody.size() - 1) {

                anterior = snakeBody.get(i - 1);
                actual = snakeBody.get(i);
                siguiente = snakeBody.get(i + 1);

                //cuando el cuerpo es horizontal
                if (anterior.y == siguiente.y){
                    cuerpoActual = cuerpoHorizontal;
                }

                //cuando el cuerpo es vertical
                else if(anterior.x == siguiente.x){
                    cuerpoActual = cuerpoVertical;
                }
                else {
                    if ((anterior.y < actual.y && siguiente.x > actual.x) ||
                            (siguiente.y < actual.y && anterior.x > actual.x)) {
                        cuerpoActual = curva1;
                    }
                    else if ((anterior.y > actual.y && siguiente.x > actual.x) ||
                            (siguiente.y > actual.y && anterior.x > actual.x)) {
                        cuerpoActual = curva4;
                    }
                    else if ((anterior.y < actual.y && siguiente.x < actual.x) ||
                            (siguiente.y < actual.y && anterior.x < actual.x)) {
                        cuerpoActual = curva3;
                    }
                    else {
                        cuerpoActual = curva2;
                    }
                }
                g.drawImage(cuerpoActual, snakeBody.get(i).x, snakeBody.get(i).y, UNIT_SIZE, UNIT_SIZE, null);
            } else if (i == snakeBody.size() - 1) {

                Point cola = snakeBody.get(i);
                anterior = snakeBody.get(i-1);

                if (cola.x == anterior.x){
                    if (cola.y < anterior.y){
                        colaActual = colaAbajo;
                    }
                    else {
                        colaActual = colaArriba;
                    }
                }
                else if(cola.y == anterior.y){
                    if (cola.x < anterior.x){
                        colaActual = colaDerecha;
                    }
                    else {
                        colaActual = colaIzquierda;
                    }

                }
                g.drawImage(colaActual, snakeBody.get(i).x, snakeBody.get(i).y, UNIT_SIZE, UNIT_SIZE, null);
            }

            // dibuja la manzana
            g.drawImage(imgManzana, comidaX, comidaY, UNIT_SIZE, UNIT_SIZE, null);
        }

        if (pausado) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, WIDTH, HEIGHT);

            g2.setColor(Color.WHITE);
            g2.setFont(AppFont.title());
            g2.drawString("PAUSA", WIDTH / 2 - 80, HEIGHT / 2);
        }
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

            new Thread(() -> reproducirSonido("/sounds/apple_bite.wav")).start();

			generarManzana();

			snakeBody.add(new Point(snakeBody.getLast().x - 25, snakeBody.getLast().y));

            if(contadorComidas > 5){
                contadorComidas = 0;
            }

			contadorComidas++;

            aumentarVelocidad();

            contadorPuntaje += 10;

            lblPuntaje.setText(String.valueOf(contadorPuntaje));


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
        if ((snakeX > 600 || snakeX < 0 ) || (snakeY > 600 || snakeY < inicioMapa)){
            mensajeGameOver();
            timer.stop();
            resetGame();
            timer.start();

        }
    }

    public void resetGame(){
        cargarMusica();
        reproducirMusica();

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

        generarManzana();
    }

    public void aumentarVelocidad(){
        if (contadorComidas == 5){
            delay -= 15;
            timer.setDelay(delay);
        }
    }

    public void mensajeGameOver(){

        detenerMusica();

        JFrame frame = new JFrame();
        ImageIcon icono= new ImageIcon("src/img/icono.png");

        Image escalarIcono = icono.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        ImageIcon iconoFinal = new ImageIcon(escalarIcono);

        JLabel mensajes = new JLabel("<html><center>Puntaje:" + contadorPuntaje + "<br><br>¿Desea volver a jugar?</center></html>");
        mensajes.setFont(AppFont.large());
        mensajes.setHorizontalAlignment(SwingConstants.CENTER);

        int opcion = JOptionPane.showOptionDialog(frame, mensajes, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, iconoFinal, null, null);

        if (opcion == JOptionPane.NO_OPTION){
            System.exit(0);
        }
    }


    //Metodo que genera las manzanas y evita que se generen sobre el cuerpo de la serpiente
    public void generarManzana(){

        boolean manzanaGeneradaExitosamente;

        do {
            manzanaGeneradaExitosamente = true;

            comidaX = ((int) (Math.random() * 23) + 1) * 25;
            comidaY = ((int) (Math.random() * 23) + 1) * 25;

            for (int i = 0; i < snakeBody.size(); i++){
                if ((comidaX == snakeBody.get(i).x) && (comidaY == snakeBody.get(i).y)){
                    manzanaGeneradaExitosamente = false;
                    break;
                }
            }

        }while(!manzanaGeneradaExitosamente);
    }

    public void cargarSprites(){
        try{
            spriteSnake = ImageIO.read(new File("src/img/snake.png"));

            int TILE = 40;

            cabezaArriba = spriteSnake.getSubimage(3 * TILE, 0, TILE, TILE);
            cabezaAbajo = spriteSnake.getSubimage(3 * TILE, TILE, TILE, TILE);
            cabezaDerecha = spriteSnake.getSubimage(4 * TILE, 0, TILE, TILE);
            cabezaIzquierda = spriteSnake.getSubimage(4 * TILE, TILE, TILE, TILE);

            cuerpoHorizontal = spriteSnake.getSubimage(2 * TILE, TILE, TILE, TILE);
            cuerpoVertical = spriteSnake.getSubimage(2 * TILE, 0, TILE, TILE);

            colaArriba = spriteSnake.getSubimage(0 , 0, TILE, TILE);
            colaAbajo = spriteSnake.getSubimage(TILE, TILE, TILE, TILE);
            colaDerecha = spriteSnake.getSubimage(TILE, 0, TILE, TILE);
            colaIzquierda = spriteSnake.getSubimage(0, TILE, TILE, TILE);

            curva1 = spriteSnake.getSubimage(5 * TILE, 0, TILE, TILE);
            curva2 = spriteSnake.getSubimage(6 * TILE, 0, TILE, TILE);
            curva3 = spriteSnake.getSubimage(6 * TILE, TILE, TILE, TILE);
            curva4 = spriteSnake.getSubimage(5 * TILE, TILE, TILE, TILE);


        } catch  (IOException e){
            e.printStackTrace();
        }

        try {
            fondo = ImageIO.read(getClass().getResource("/img/background purple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reproducirSonido(String ubicacion) {
        try {
            AudioInputStream musica = AudioSystem.getAudioInputStream(
                    getClass().getResource(ubicacion)
            );

            Clip clipMusica = AudioSystem.getClip();
            clipMusica.open(musica);
            clipMusica.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarMusica() {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sounds/sound_background.WAV")
            );

            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audio);

            FloatControl volume = (FloatControl) musicaFondo.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f); // bajar volumen

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reproducirMusica() {
        if (musicaFondo != null) {
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
        }
    }

    public void panelSuperior(){
        JPanel panelSuperiorHorizontal = new JPanel();
        panelSuperiorHorizontal.setLayout(new BoxLayout(panelSuperiorHorizontal, BoxLayout.X_AXIS));
        panelSuperiorHorizontal.setPreferredSize(new Dimension(getWidth(), inicioMapa));
        panelSuperiorHorizontal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelSuperiorHorizontal.setBackground(Color.BLACK);

        iconoManzanaOriginal = new ImageIcon(getClass().getResource("/img/apple.png"));
        imagenEscaladaIcono = iconoManzanaOriginal.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        iconoManzanaFinal = new ImageIcon(imagenEscaladaIcono);

        lblIcono = new JLabel(iconoManzanaFinal);

        lblPuntaje = new JLabel("0");
        lblPuntaje.setPreferredSize(new Dimension(50, 50));
        lblPuntaje.setForeground(Color.WHITE);
        lblPuntaje.setFont(AppFont.small());

        panelSuperiorHorizontal.add(lblIcono);
        panelSuperiorHorizontal.add(Box.createHorizontalStrut(10));
        panelSuperiorHorizontal.add(lblPuntaje);


        add(panelSuperiorHorizontal, BorderLayout.NORTH);
    }



}