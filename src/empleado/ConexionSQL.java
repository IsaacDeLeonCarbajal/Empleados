package empleado;

import java.sql.*;
import javax.swing.JOptionPane;

public class ConexionSQL {

    /**
     * Contiene los nombres de cada uno de los campos en las tablas de la base
     * de datos.
     * <p>
     * Facilita la inserción, consulta y actualización de los datos desde
     * diferentes puntos del programa.
     * </p>
     */
    public static enum EMPLEADOS {
        Empleados, //Nombre de la tabla
        id,
        nombre,
        apellidoPaterno,
        apellidoMaterno,
        genero,
        fechaNacimiento,
        fechaIngreso,
        salarioBasico,
        imagen
    }

    public static enum GENEROS {
        Generos, //Nombre de la tabla
        id,
        genero
    }

    public Connection conexion;
    private Statement sentencia;
    public PreparedStatement pStatement;

    /**
     * Nombre de la base de datos
     */
    private String database;
    /**
     * Host
     */
    private String hostname;
    /**
     * Puerto
     */
    private String port;
    /**
     * Ruta de la base de datos
     */
    private String url;
    /**
     * Nombre de usuario
     */
    private String username;
    /**
     * Clave de usuario
     */
    private String password;

    /**
     * Crear una conexión con la base de datos especificada con los datos
     * recibidos.
     *
     * @param database Nombre de la base de datos.
     * @param hostname Host.
     * @param port Puerto.
     * @param username Nombre de usuario.
     * @param password Clave de usuario.
     */
    public ConexionSQL(String database, String hostname, String port, String username, String password) {
        initBDD(false, database, hostname, port, username, password);
    }

    /**
     * Crear una conexión con la base de datos especificada con los datos
     * recibidos.
     * <p>
     * Si <code>crearBDD</code> es <code>true</code>, se intenta crear la base
     * de datos de la clínica en el caso de que no se pueda establecer conexión
     * con ella.
     * </p>
     * <p>
     * Sólo debería usarse <code>crearBDD = true</code> una vez en todo el
     * programa y justo al inicio del mismo.
     * </p>
     *
     * @param crearBDD Indica si se debe intentar crear la base de datos en caso
     * de no poder establecer una conexión con ella.
     * @param database Nombre de la base de datos.
     * @param hostname Host.
     * @param port Puerto.
     * @param username Nombre de usuario.
     * @param password Clave de usuario.
     */
    public ConexionSQL(boolean crearBDD, String database, String hostname, String port, String username, String password) {
        initBDD(crearBDD, database, hostname, port, username, password);
    }

    /**
     * Crear una conexión con la base de datos por defecto y con los datos por
     * defecto.
     *
     * No se intenta crear la base de datos en caso de que esta no exista.
     */
    public ConexionSQL() {
        initBDD(false, "bddTemporal1", "localhost", "3306", "root", ""); //Inicializar con valores por defecto
    }

    /**
     * Crear una conexión con la base de datos por defecto y con los datos por
     * defecto.
     * <p>
     * Si <code>crearBDD</code> es <code>true</code>, se intenta crear la base
     * de datos de la clínica en el caso de que no se pueda establecer conexión
     * con ella.
     * </p>
     * <p>
     * Sólo debería usarse <code>crearBDD = true</code> una vez en todo el
     * programa y justo al inicio del mismo.
     * </p>
     *
     * @param crearBDD Indica si se debe intentar crear la base de datos en caso
     * de no poder establecer una conexión con ella.
     */
    public ConexionSQL(boolean crearBDD) {
        initBDD(crearBDD, "bddTemporal1", "localhost", "3306", "root", ""); //Inicializar con valores por defecto
    }

    /**
     * Inicializar la conexión con la base de datos especificada con los datos
     * recibidos.
     * <p>
     * Hace uso del método <code>conectar()</code> para establecer la conexión
     * con la base de datos.
     * </p>
     * <p>
     * Si <code>crearBDD</code> es <code>true</code>, se intenta crear la base
     * de datos de la clínica en el caso de que no se pueda establecer conexión
     * con ella.
     * </p>
     * <p>
     * Sólo debería usarse <code>crearBDD = true</code> una vez en todo el
     * programa y justo al inicio del mismo.
     * </p>
     *
     * @param crearBDD Indica si se debe intentar crear la base de datos en caso
     * de no poder establecer una conexión con ella.
     * @param database Nombre de la base de datos.
     * @param hostname Host.
     * @param port Puerto.
     * @param username Nombre de usuario.
     * @param password Clave de usuario.
     *
     * @see #conectar()
     */
    private void initBDD(boolean crearBDD, String database, String hostname, String port, String username, String password) {
        this.database = database;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.url = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?useSSL=false&serverTimezone=UTC";

        this.conectar();

        if (sentencia == null && crearBDD) { //Si no se encontró la base de datos
            url = "jdbc:mysql://" + this.hostname + ":" + this.port + "?useSSL=false&serverTimezone=UTC";

            JOptionPane.showMessageDialog(null, "Intentando crear la base de datos \'" + this.database + "\'", "Excepción", JOptionPane.INFORMATION_MESSAGE);

            this.conectar();

            try { //Crear la base de datos
                sentencia.execute("CREATE DATABASE IF NOT EXISTS " + this.database);
                sentencia.execute("USE " + this.database);
                sentencia.execute("CREATE TABLE IF NOT EXISTS " + GENEROS.Generos + "("
                        + GENEROS.id + " INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                        + GENEROS.genero + " VARCHAR(15) NOT NULL"
                        + ") ENGINE = InnoDB CHARACTER SET = utf8");
                sentencia.execute("CREATE TABLE IF NOT EXISTS " + EMPLEADOS.Empleados + "("
                        + EMPLEADOS.id + " INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                        + EMPLEADOS.nombre + " VARCHAR(30) NOT NULL, "
                        + EMPLEADOS.apellidoPaterno + " VARCHAR(20) NOT NULL, "
                        + EMPLEADOS.apellidoMaterno + " VARCHAR(20) NOT NULL, "
                        + EMPLEADOS.genero + " INT NOT NULL, "
                        + EMPLEADOS.fechaNacimiento + " DATE NOT NULL, "
                        + EMPLEADOS.fechaIngreso + " DATE NOT NULL, "
                        + EMPLEADOS.salarioBasico + " FLOAT NOT NULL, "
                        + EMPLEADOS.imagen + " BLOB, "
                        + "FOREIGN KEY (" + EMPLEADOS.genero + ") "
                        + "    REFERENCES " + GENEROS.Generos + "(" + GENEROS.id + ") "
                        + "    ON UPDATE CASCADE"
                        + "    ON DELETE CASCADE"
                        + ") ENGINE = InnoDB CHARACTER SET = utf8");
                sentencia.execute("INSERT INTO " + GENEROS.Generos + " ("+ GENEROS.genero + ") "
                        + "VALUES ('Masculino')");
                sentencia.execute("INSERT INTO " + GENEROS.Generos + " ("+ GENEROS.genero + ") "
                        + "VALUES ('Femenino')");
            } catch (SQLException | NullPointerException ex) {
                JOptionPane.showMessageDialog(null, "Error al crear la base de datos \'" + this.database + "\': " + ex.getMessage(), "Excepción", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Ejecutar una sentencia MySQL.
     * <p>
     * Si ocurre algún error al intentar ejecutar la sentencia, se muestra un
     * <code>JOptionPane</code> indicando el problema.
     * </p>
     *
     * @param sentencia La sentencia MySQL que se desa ejecutar en la base de
     * datos.
     *
     * @return <code>true</code> si la sentencia se ejecutó correctamente,
     * <code>false</code> si no.
     */
    public boolean ejecutar(String sentencia) {
        try {
            this.sentencia.execute(sentencia);

            return true; //Indicar que no hubo error
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al ejecutar la sentencia MySQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            return false; //Indicar que hubo algún error
        }
    }

    /**
     * Ejecutar una sentencia MySQL de consulta.
     * <p>
     * Si ocurre algún error al intentar ejecutar la sentencia, se muestra un
     * <code>JOptionPane</code> indicando el problema.
     * </p>
     *
     * @param sentencia La sentencia MySQL que se desa ejecutar en la base de
     * datos.
     *
     * @return Un <code>ResultSet</code> con la información obtenida si la
     * sentencia se ejecutó correctamente, <code>null</code> si no.
     */
    public ResultSet ejecutarQuery(String sentencia) {
        try {
            return this.sentencia.executeQuery(sentencia);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al ejecutar la sentencia MySQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            return null; //Indicar que hubo algún error
        }
    }

    /**
     * Ejecutar una sentencia MySQL de actualización.
     * <p>
     * Si ocurre algún error al intentar ejecutar la sentencia, se muestra un
     * <code>JOptionPane</code> indicando el problema.
     * </p>
     *
     * @param sentencia La sentencia MySQL que se desa ejecutar en la base de
     * datos.
     *
     * @return 1 si la sentencia se ejecutó correctamente, 0 si no.
     */
    public int ejecutarUpdate(String sentencia) {
        try {
            return this.sentencia.executeUpdate(sentencia);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al ejecutar la sentencia MySQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            return -1; //Indicar que hubo algún error
        }
    }

    /**
     * Intentar establecer una conexión con la base de datos.
     * <p>
     * Si la conexión es exitosa, crea una estancia de <code>Connection</code> y
     * una de <code>Statement</code> para realizar posteriormente operaciones en
     * la base de datos.
     * </p>
     * <p>
     * Si la conexión falla, se muestra un <code>JOptionPane</code> indicando el
     * problema.
     * </p>
     */
    public final void conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); //Nombre del driver (controlador de base de datos)

            conexion = DriverManager.getConnection(url, username, password);

            sentencia = conexion.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Excepción", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public PreparedStatement preparedStatement(String sent) throws SQLException {
        return this.conexion.prepareStatement(sent);
    }

    /**
     * Intentar cerrar la conexión con la base de datos.
     * <p>
     * Si ocurre un error al intentar desconectar, se muestra un
     * <code>JOptionPane</code> indicando el problema y se cierra el programa.
     * </p>
     */
    public final void desconectar() {
        try {
            if (conexion != null) {
                if (sentencia != null) {
                    sentencia.close();
                    sentencia = null;
                }

                conexion.close();
                conexion = null;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Excepción", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Indica si la conexión establecida con la base de datos está vigente.
     *
     * @return <code>true</code> Si la conexión es correcta, <code>false</code>
     * si no.
     */
    public boolean conectado() {
        return sentencia != null;
    }

}
