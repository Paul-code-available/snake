import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.beans.beancontext.BeanContextServiceRevokedEvent;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class miMenuListeners implements ActionListener {
    GamePanel panel;

    public miMenuListeners(GamePanel panel){
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando){
            case "pausar":
                panel.pauseGame();
                break;
            case "Nuevo":
                panel.resetGame();
                break;
            case "Salir":
                System.exit(0);
                break;
            case "facil":
                panel.seleccionarDificultad("facil");
                break;
            case "normal":
                panel.seleccionarDificultad("normal");
                break;
            case "dificil":
                panel.seleccionarDificultad("dificil");
                break;
        }
    }
}
