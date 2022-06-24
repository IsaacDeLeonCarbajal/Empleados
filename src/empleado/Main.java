package empleado;

import java.awt.*;
import javax.swing.*;

public class Main {

    /**
     * Tamaño mínimo de la ventana principal.
     */
    public static final Dimension MIN_SIZE = new Dimension(970, 640);
    /**
     * Fuente de la letra de los <code>JComponent</code> que no se encuentran en
     * un formulario.
     */
    public static final Font FUENTE = new Font("", 0, 20);
    /**
     * Altura de los <code>JComponent</code> que no se encuentran en un
     * formulario.
     */
    public static final int ALTURA = 40;
    /**
     * Fuente de la letra de los <code>JComponent</code> que se encuentran en un
     * formulario.
     */
    public static final Font FUENTE_FORMULARIO = new Font("", 0, 18);
    /**
     * Altura de los <code>JComponent</code> que se encuentran en un formulario.
     */
    public static final int ALTURA_FORMULARIO = 30;
    public static final Color COLOR_FONDO = new Color(250, 125, 45);
    public static final Color COLOR_DISABLED = new Color(80, 80, 80);
    public static final Color COLOR_TRANSPARENTE = new Color(0, 0, 0, 0);

    public static void main(String[] args) {
        ConexionSQL bdd = new ConexionSQL(true); //Crear la base de datos si no está creada
        UIManager.put("ComboBox.disabledForeground", COLOR_DISABLED);

        if (bdd.conectado()) { //Si la conexión fue correcta
            (new Ventana()).setVisible(true);
        }

        bdd.desconectar();
    }
    
}
