import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame{

    GamePanel panel;

    public GameFrame() {
        panel = new GamePanel();

        add(panel);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        pausarJuego();
        agregarMenu();
    }

    public void pausarJuego(){
        addWindowListener(new WindowAdapter() {

            public void windowIconified(WindowEvent e){
                if (!panel.pausado){
                    panel.pauseGame();
                }

            }

            public void windowDeiconified(WindowEvent e){
                if (panel.pausado){
                    panel.pauseGame();
                }
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                if (!panel.pausado){
                    panel.pauseGame();
                }
            }

            @Override
            public void windowActivated(WindowEvent e){
                if (panel.pausado){
                    panel.pauseGame();
                }
            }
        });
    }

    public void agregarMenu(){
        miMenuListeners listener = new miMenuListeners(panel);

        JMenuBar barraMenu  = new JMenuBar();

        JMenu opciones = new JMenu("Opciones");
        barraMenu.add(opciones);

        JMenuItem nuevoGame = new JMenuItem("Nuevo");
        nuevoGame.addActionListener(listener);
        opciones.add(nuevoGame);

        JMenuItem pausa = new JMenuItem("Pausa");
        pausa.setActionCommand("pausar");
        pausa.addActionListener(listener);
        opciones.add(pausa);

        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(listener);
        opciones.add(salir);

        JMenu dificultad = new JMenu("Dificultad"); //es JMenu ya que tendras mas hijos
        barraMenu.add(dificultad);

        JMenuItem facil = new JMenuItem("Facil");
        facil.setActionCommand("facil");
        facil.addActionListener(listener);
        dificultad.add(facil);

        JMenuItem normal = new JMenuItem("Normal");
        normal.setActionCommand("normal");
        normal.addActionListener(listener);
        dificultad.add(normal);

        JMenuItem dificil = new JMenuItem("Dificil");
        dificil.setActionCommand("dificil");
        dificil.addActionListener(listener);
        dificultad.add(dificil);


        setJMenuBar(barraMenu);
    }

}


