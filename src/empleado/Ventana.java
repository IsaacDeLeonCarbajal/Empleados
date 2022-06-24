package empleado;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static empleado.Main.*;

/**
 * Ventana principal del software.
 * <p>
 * Es donde se mostrarán las diferentes interfaces del sistema, como la de
 * inicio de sesión, consulta y registro de historiales clínicos y de empleados.
 * </p>
 * <p>
 * Utiliza componentes de <code>Java.Swing</code>.
 * </p>
 */
public class Ventana extends JFrame {

    /**
     * Construye una ventana (inicialmente invisible) con los componentes
     * necesarios para operar el sistema.
     * <p>
     * Inicialmente se muestra la interfaz para iniciar sesión.
     * </p>
     */
    public Ventana() {
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationByPlatform(true);
        super.setMinimumSize(MIN_SIZE);
        super.setTitle("Registro de Empleados");
        super.getContentPane().setBackground(COLOR_FONDO);
//        super.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/images/logo.png")));

        super.getContentPane().setLayout(new Principal(super.getContentPane()));

        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LayoutManager lyt = Ventana.super.getContentPane().getLayout();

                if (lyt instanceof Principal) { //Si se está mostrando la pantalla principal
                    ((Principal) lyt).repaint(); //Actualizar el tamaño de los paneles
                }
            }
        });

        super.setSize(MIN_SIZE);
    }
    
}
