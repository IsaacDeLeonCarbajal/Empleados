package empleado;

import com.github.lgooddatepicker.components.*;
import com.toedter.calendar.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import static empleado.Main.*;
import static empleado.ConexionSQL.*;
import java.awt.image.*;
import java.io.*;
import java.sql.Date;
import java.text.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;
import javax.imageio.*;

/**
 * Interfaz donde se puede buscar y registrar la información de los empledaos de
 * la clínica.
 * <p>
 * Utiliza componentes de <code>Java.Swing</code>.
 * </p>
 */
public class Principal extends GroupLayout {

    /**
     * <code>JComboBox</code> para seleccionar si se quiere registrar o
     * consultar la información de un empleado.
     */
    private JComboBox cmbOperacion;
    /**
     * Guardará los <code>JComponent</code> correspondientes a cada campo para
     * poder modificar el estado de todos a la vez.
     */
    private Map<String, JComponent> campos = new HashMap<>();
    private JLabel lblNombreEmpleado;
    private JButton btnAccion;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnMostrar;
    private final JTabbedPane tbpPrincipal;
    private JPanel pnlEmpleado;
    private JLabel lblImagen;
    private JLabel lblEdad;
    private JTextField txtEdad;
    private JLabel lblAntiguedad;
    private JTextField txtAntiguedad;
    private JLabel lblPrestaciones;
    private JTextField txtPrestaciones;

    /**
     * Construye un <code>GroupLayout</code> con un formulario para los datos
     * del empleado.
     * <p>
     * Requiere acceso a la base de datos para insertar y consultar registros.
     * </p>
     *
     * @param host El contenedor donde se mostrarán los componentes.
     *
     * @see GroupLayout#GroupLayout(java.awt.Container)
     */
    public Principal(Container host) {
        super(host);

        ConexionSQL bdd = new ConexionSQL();

        cmbOperacion = new JComboBox(new String[]{"Registrar", "Consultar"}); //Sólo hay dos operaciones disponibles
        cmbOperacion.setFont(FUENTE);
        cmbOperacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean cmpActivados = ((String) cmbOperacion.getSelectedItem()).equals("Registrar"); //Si se está en panel de registrar empleados

                //Actualizar la interfaz
                btnAccion.setText((String) cmbOperacion.getSelectedItem());
                btnActualizar.setVisible(!cmpActivados);
                btnEliminar.setVisible(!cmpActivados);
                lblNombreEmpleado.setVisible(!cmpActivados);
                lblEdad.setVisible(!cmpActivados);
                txtEdad.setVisible(!cmpActivados);
                lblAntiguedad.setVisible(!cmpActivados);
                txtAntiguedad.setVisible(!cmpActivados);
                lblImagen.setIcon(null);

                for (String s : campos.keySet()) {
                    if (s.equals(EMPLEADOS.matricula.toString())) {
                        continue; //No hacer nada con el campo de clave
                    }

                    JComponent cmp = campos.get(s);

                    //Reiniciar todos los campos del formulario
                    if (cmp instanceof JTextField) {
                        ((JTextField) cmp).setText("");
                    } else if (cmp instanceof JTextArea) {
                        ((JTextArea) cmp).setText("");
                    } else if (cmp instanceof JComboBox) {
                        ((JComboBox) cmp).setSelectedIndex(0);
                    }
                }
            }
        });

        lblNombreEmpleado = new JLabel("[Nombre del empleado]");
        lblNombreEmpleado.setOpaque(true);
        lblNombreEmpleado.setBackground(Color.WHITE);
        lblNombreEmpleado.setFont(FUENTE);
        lblNombreEmpleado.setHorizontalAlignment(JLabel.CENTER);

        btnAccion = new JButton("Registrar");
        btnAccion.setFont(FUENTE);
        btnAccion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sentencia = "";
                PreparedStatement pStatement;

                switch ((String) cmbOperacion.getSelectedItem()) {
                    case "Registrar":
                        for (String s : campos.keySet()) { //Revisar que todos los campos se hallan llenado correctamente
                            boolean error = false;

                            JComponent cmp = campos.get(s);

                            if (cmp instanceof JTextField) {
                                error = ((JTextField) cmp).getText().isBlank();
                            } else if (cmp instanceof JTextArea) {
                                error = ((JTextArea) cmp).getText().isBlank();
                            }

                            if (error) {
                                cmp.requestFocus();
                                JOptionPane.showMessageDialog(null, "Faltan campos por llenar", "Error", JOptionPane.ERROR_MESSAGE);
                                return; //No hacer el registro
                            }
                        }

                        sentencia = "INSERT INTO " + EMPLEADOS.Empleados + " "
                                + "(" + EMPLEADOS.id + ", " + EMPLEADOS.matricula + ", " + EMPLEADOS.nombre + ", " + EMPLEADOS.apellidoPaterno + ", " + EMPLEADOS.apellidoMaterno + ", " + EMPLEADOS.genero + ", " + EMPLEADOS.fechaNacimiento + ", " + EMPLEADOS.fechaIngreso + ", " + EMPLEADOS.salarioBasico + ", " + EMPLEADOS.imagen + ") "
                                + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        try {
                            File file = new File(getDato(EMPLEADOS.imagen));

                            pStatement = bdd.preparedStatement(sentencia);

                            pStatement.setString(1, getDato(EMPLEADOS.matricula));
                            pStatement.setString(2, getDato(EMPLEADOS.nombre));
                            pStatement.setString(3, getDato(EMPLEADOS.apellidoPaterno));
                            pStatement.setString(4, getDato(EMPLEADOS.apellidoMaterno));
                            pStatement.setInt(5, (((JRadioButton) getComponent(EMPLEADOS.genero + "1")).isSelected()) ? 1 : 2);
                            pStatement.setString(6, getDato(EMPLEADOS.fechaNacimiento));
                            pStatement.setString(7, getDato(EMPLEADOS.fechaIngreso));
                            pStatement.setFloat(8, Float.parseFloat(getDato(EMPLEADOS.salarioBasico)));
                            pStatement.setBinaryStream(9, new FileInputStream(file), (int) file.length());

                            pStatement.execute();

                            JOptionPane.showMessageDialog(null, "Empleado " + getDato(EMPLEADOS.nombre) + " registrado", "Correcto", JOptionPane.INFORMATION_MESSAGE); //Si no hubo error
                        } catch (SQLException | FileNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, "Error al registrar al empleado " + getDato(EMPLEADOS.nombre) + "\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case "Consultar":
                        sentencia = "SELECT " + EMPLEADOS.id + ", " + EMPLEADOS.matricula + ", " + EMPLEADOS.nombre + ", " + EMPLEADOS.apellidoPaterno + ", " + EMPLEADOS.apellidoMaterno + ", " + EMPLEADOS.genero + ", " + EMPLEADOS.fechaNacimiento + ", " + EMPLEADOS.fechaIngreso + ", " + EMPLEADOS.salarioBasico + ", " + EMPLEADOS.imagen + " "
                                + "FROM " + EMPLEADOS.Empleados + " "
                                + "WHERE (" + EMPLEADOS.matricula + " = ?) "
                                + "LIMIT 1";

                        try {
                            pStatement = bdd.preparedStatement(sentencia);
                            pStatement.setString(1, getDato(EMPLEADOS.matricula));

                            ResultSet result = pStatement.executeQuery();

                            while (result.next()) {
                                lblNombreEmpleado.setText(result.getString(EMPLEADOS.nombre.toString()) + " " + result.getString(EMPLEADOS.apellidoPaterno.toString()) + " " + result.getString(EMPLEADOS.apellidoMaterno.toString()));
                                setDato(EMPLEADOS.nombre, result.getString(EMPLEADOS.nombre.toString()));
                                setDato(EMPLEADOS.apellidoPaterno, result.getString(EMPLEADOS.apellidoPaterno.toString()));
                                setDato(EMPLEADOS.apellidoMaterno, result.getString(EMPLEADOS.apellidoMaterno.toString()));
                                setDato(EMPLEADOS.salarioBasico, result.getString(EMPLEADOS.salarioBasico.toString()));
                                setDato(EMPLEADOS.fechaNacimiento, result.getString(EMPLEADOS.fechaNacimiento.toString()));
                                setDato(EMPLEADOS.fechaIngreso, result.getString(EMPLEADOS.fechaIngreso.toString()));
                                ((JRadioButton) getComponent(EMPLEADOS.genero.toString() + result.getInt(EMPLEADOS.genero.toString()))).setSelected(true);

                                Blob blob = result.getBlob(EMPLEADOS.imagen.toString());
                                if (blob != null) {
                                    byte[] data = blob.getBytes(1, (int) blob.length());
                                    lblImagen.setIcon(new ImageIcon(new ImageIcon(data).getImage().getScaledInstance(210, 245, Image.SCALE_SMOOTH), "Imagen"));
                                } else {
                                    JOptionPane.showMessageDialog(null, "Error al cargar la imagen del empleado con matricula " + getDato(EMPLEADOS.matricula), "ERROR", JOptionPane.ERROR_MESSAGE);
                                }

                                Calendar nacimiento = Calendar.getInstance();
                                nacimiento.setTime(result.getDate(EMPLEADOS.fechaNacimiento.toString()));
                                Period edad = Period.between(LocalDate.of(nacimiento.get(Calendar.YEAR), nacimiento.get(Calendar.MONTH) + 1, nacimiento.get(Calendar.DAY_OF_MONTH)), LocalDate.now());
                                txtEdad.setText(edad.getYears() + " años");

                                Calendar ingreso = Calendar.getInstance();
                                ingreso.setTime(result.getDate(EMPLEADOS.fechaIngreso.toString()));
                                Period antiguedad = Period.between(LocalDate.of(ingreso.get(Calendar.YEAR), ingreso.get(Calendar.MONTH) + 1, ingreso.get(Calendar.DAY_OF_MONTH)), LocalDate.now());
                                txtAntiguedad.setText(antiguedad.getYears() + " años, " + antiguedad.getMonths() + " meses");

                                double base = (double) result.getFloat(EMPLEADOS.salarioBasico.toString());
                                int aniosAnt = antiguedad.getYears();
                                
                                int diasVacaciones;
                                if (aniosAnt <= 4) {
                                    diasVacaciones = 4 + (2 * aniosAnt);
                                } else {
                                    diasVacaciones = Math.min(22, (14 + (((((int) (aniosAnt / 2)) % 4) - 1) * 2)));
                                }
                                
                                double salarioDiario = (base / 15);

                                txtPrestaciones.setText("Días de vacaciones: " + diasVacaciones + ". Salario diario: " + salarioDiario);
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al consultar al empleado con matricula " + getDato(EMPLEADOS.matricula) + "\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Sólo se deberían poder registrar y consultar los datos");
                }
            }
        }
        );

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setFont(FUENTE_FORMULARIO);
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Función sólo disponible en el panel de consultar
                for (String s : campos.keySet()) { //Revisar que todos los campos se hallan llenado correctamente
                    boolean error = false;

                    JComponent cmp = campos.get(s);

                    if (cmp instanceof JTextField) {
                        error = ((JTextField) cmp).getText().isBlank();
                    } else if (cmp instanceof JTextArea) {
                        error = ((JTextArea) cmp).getText().isBlank();
                    }

                    if (error) {
                        cmp.requestFocus();
                        JOptionPane.showMessageDialog(null, "Faltan campos por llenar", "Error", JOptionPane.ERROR_MESSAGE);
                        return; //No hacer el registro
                    }
                }

                String sentencia = "UPDATE " + EMPLEADOS.Empleados + " "
                        + "SET " + EMPLEADOS.nombre + " = ?, " + EMPLEADOS.apellidoPaterno + " = ?, " + EMPLEADOS.apellidoMaterno + " = ?, " + EMPLEADOS.genero + " = ?, " + EMPLEADOS.fechaNacimiento + " = ?, " + EMPLEADOS.fechaIngreso + " = ?, " + EMPLEADOS.salarioBasico + " = ?, " + EMPLEADOS.imagen + " = ? "
                        + "WHERE (" + EMPLEADOS.matricula + " = ?)";

                try {
                    File file = new File(getDato(EMPLEADOS.imagen));

                    PreparedStatement pStatement = bdd.preparedStatement(sentencia);

                    pStatement.setString(1, getDato(EMPLEADOS.nombre));
                    pStatement.setString(2, getDato(EMPLEADOS.apellidoPaterno));
                    pStatement.setString(3, getDato(EMPLEADOS.apellidoMaterno));
                    pStatement.setInt(4, (((JRadioButton) getComponent(EMPLEADOS.genero + "1")).isSelected()) ? 1 : 2);
                    pStatement.setString(5, getDato(EMPLEADOS.fechaNacimiento));
                    pStatement.setString(6, getDato(EMPLEADOS.fechaIngreso));
                    pStatement.setFloat(7, Float.parseFloat(getDato(EMPLEADOS.salarioBasico)));
                    pStatement.setBinaryStream(8, new FileInputStream(file), (int) file.length());
                    pStatement.setString(9, getDato(EMPLEADOS.matricula));

                    pStatement.execute();

                    JOptionPane.showMessageDialog(null, "Información del empleado " + getDato(EMPLEADOS.nombre) + " actualizada", "Correcto", JOptionPane.INFORMATION_MESSAGE); //Si no hubo error
                } catch (SQLException | FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Error al actualizar la información del empleado " + getDato(EMPLEADOS.nombre) + "\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(FUENTE_FORMULARIO);
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Función sólo disponible en el panel de consultar
                String sentencia = "DELETE FROM " + EMPLEADOS.Empleados + " "
                        + "WHERE (" + EMPLEADOS.matricula + " = ?)";

                try {
                    PreparedStatement pStatement = bdd.preparedStatement(sentencia);

                    pStatement.setString(1, getDato(EMPLEADOS.matricula));

                    pStatement.execute();

                    JOptionPane.showMessageDialog(null, "Empleado  eliminado", "Correcto", JOptionPane.INFORMATION_MESSAGE); //Si no hubo error
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar al empleado\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tbpPrincipal = new JTabbedPane();
        tbpPrincipal.add("Datos del empleado", crearFormularioEmpleado());

        super.setAutoCreateGaps(true);
        super.setAutoCreateContainerGaps(true);
        super.setHorizontalGroup(
                super.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(super.createSequentialGroup()
                                .addComponent(cmbOperacion, 115, 115, 115)
                                .addComponent(lblNombreEmpleado, 350, Short.MAX_VALUE, Short.MAX_VALUE)
                                .addComponent(btnAccion)
                                .addGap(7, 7, Short.MAX_VALUE))
                        .addComponent(tbpPrincipal)
        );
        super.setVerticalGroup(
                super.createSequentialGroup()
                        .addGroup(super.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(cmbOperacion, ALTURA, ALTURA, ALTURA)
                                .addComponent(lblNombreEmpleado, ALTURA, ALTURA, ALTURA)
                                .addComponent(btnAccion, ALTURA, ALTURA, ALTURA))
                        .addComponent(tbpPrincipal)
        );

        cmbOperacion.setSelectedItem("Registrar"); //Inicializar la pantalla
    }

    /**
     * Crear un <code>JPanel</code> con los campos necesarios para ingresar
     * información del empleado.
     * <p>
     * Guarda los <code>JComponent</code> en los que se ingresa información en
     * el mapa <code>campos</code> para su posterior acceso.
     * </p>
     *
     * @return Un <code>JScrollPane</code> con un <code>JPanel</code> con los
     * campos necesarios para ingresar la información del empleado.
     */
    private JScrollPane crearFormularioEmpleado() {
        pnlEmpleado = new JPanel();

        GroupLayout lytEmpleado = new GroupLayout(pnlEmpleado);
        lytEmpleado.setAutoCreateGaps(true);
        lytEmpleado.setAutoCreateContainerGaps(true);

        JPanel sprEmpleado = crearSeparador("Datos del empleado"); //Campos para los datos del empleado
        JLabel lblMatricula = crearJLabel("Matrícula:");
        JTextField txtMatricula = crearJTextField();
        JLabel lblSalario = crearJLabel("Salario Básico:");
        JTextField txtSalario = crearJTextField();
        JLabel lblNombre = crearJLabel("Nombre(s):");
        JTextField txtNombre = crearJTextField();
        JLabel lblApellidoP = crearJLabel("Ap. Paterno:");
        JTextField txtApellidoP = crearJTextField();
        JLabel lblApellidoM = crearJLabel("Ap. Materno:");
        JTextField txtApellidoM = crearJTextField();
        JLabel lblGenero = crearJLabel("Género:");
        ButtonGroup grpGenero = new ButtonGroup();
        JRadioButton[] btnGenero = new JRadioButton[]{
            new JRadioButton("Masculino", true),
            new JRadioButton("Femenino", false)
        };
        btnGenero[0].setFont(FUENTE_FORMULARIO);
        btnGenero[0].setOpaque(false);
        grpGenero.add(btnGenero[0]);
        btnGenero[1].setFont(FUENTE_FORMULARIO);
        btnGenero[1].setOpaque(false);
        grpGenero.add(btnGenero[1]);
        JLabel lblFechaNacimiento = crearJLabel("Fecha Nacimiento:");
        JDateChooser dtcFechaNacimiento = new JDateChooser();
        dtcFechaNacimiento.setMaximumSize(new Dimension(250, ALTURA_FORMULARIO));
        dtcFechaNacimiento.setFont(FUENTE_FORMULARIO);
        JLabel lblFechaIngreso = crearJLabel("Fecha Ingreso:");
        JDateChooser dtcFechaIngreso = new JDateChooser();
        dtcFechaIngreso.setMaximumSize(new Dimension(250, ALTURA_FORMULARIO));
        dtcFechaIngreso.setFont(FUENTE_FORMULARIO);
        JLabel lblRutaImagen = crearJLabel("Ruta Imagen:");
        JTextField txtRutaImagen = crearJTextField();
        lblImagen = new JLabel();
        lblImagen.setIcon(new ImageIcon(new ImageIcon("").getImage().getScaledInstance(210, 245, Image.SCALE_SMOOTH), "Imagen"));
        btnMostrar = new JButton("Mostrar");
        btnMostrar.setFont(FUENTE_FORMULARIO);
        btnMostrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblImagen.setIcon(new ImageIcon(new ImageIcon(getDato(EMPLEADOS.imagen)).getImage().getScaledInstance(210, 245, Image.SCALE_SMOOTH), "Imagen"));
            }
        });

        //Componentes para información que no se guarda en la base de datos, como edad, antigüedad, etc.
        lblEdad = crearJLabel("Edad:");
        txtEdad = crearJTextField();
        txtEdad.setEnabled(false);
        lblAntiguedad = crearJLabel("Antigüedad:");
        txtAntiguedad = crearJTextField();
        txtAntiguedad.setEnabled(false);
        lblPrestaciones = crearJLabel("Prestaciones:");
        txtPrestaciones = crearJTextField();
        txtPrestaciones.setEnabled(false);

        lytEmpleado.setHorizontalGroup(
                lytEmpleado.createParallelGroup(Alignment.LEADING)
                        .addComponent(sprEmpleado)
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addComponent(lblMatricula)
                                .addComponent(txtMatricula)
                                .addComponent(lblSalario)
                                .addComponent(txtSalario, 150, 150, 150))
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addComponent(txtNombre)
                                .addComponent(lblApellidoP)
                                .addComponent(txtApellidoP)
                                .addComponent(lblApellidoM)
                                .addComponent(txtApellidoM))
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addComponent(lblGenero)
                                .addComponent(btnGenero[0])
                                .addComponent(btnGenero[1])
                                .addComponent(lblFechaNacimiento)
                                .addComponent(dtcFechaNacimiento)
                                .addComponent(lblFechaIngreso)
                                .addComponent(dtcFechaIngreso))
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addComponent(lblEdad)
                                .addComponent(txtEdad)
                                .addComponent(lblAntiguedad)
                                .addComponent(txtAntiguedad))
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addComponent(lblPrestaciones)
                                .addComponent(txtPrestaciones))
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addComponent(lblImagen, 200, 200, 200)
                                .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                        .addComponent(lblRutaImagen)
                                        .addComponent(txtRutaImagen)
                                        .addComponent(btnMostrar)))
                        .addGroup(lytEmpleado.createSequentialGroup()
                                .addGap(0, Short.MAX_VALUE, Short.MAX_VALUE)
                                .addComponent(btnActualizar)
                                .addComponent(btnEliminar))
        );
        lytEmpleado.setVerticalGroup(
                lytEmpleado.createSequentialGroup()
                        .addComponent(sprEmpleado)
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblMatricula)
                                .addComponent(txtMatricula)
                                .addComponent(lblSalario)
                                .addComponent(txtSalario))
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblNombre)
                                .addComponent(txtNombre)
                                .addComponent(lblApellidoP)
                                .addComponent(txtApellidoP)
                                .addComponent(lblApellidoM)
                                .addComponent(txtApellidoM))
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblGenero)
                                .addComponent(btnGenero[0])
                                .addComponent(btnGenero[1])
                                .addComponent(lblFechaNacimiento)
                                .addComponent(dtcFechaNacimiento)
                                .addComponent(lblFechaIngreso)
                                .addComponent(dtcFechaIngreso))
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblEdad)
                                .addComponent(txtEdad)
                                .addComponent(lblAntiguedad)
                                .addComponent(txtAntiguedad))
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblPrestaciones)
                                .addComponent(txtPrestaciones))
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblImagen, 200, 200, 200)
                                .addGroup(lytEmpleado.createSequentialGroup()
                                        .addComponent(lblRutaImagen)
                                        .addComponent(txtRutaImagen)
                                        .addComponent(btnMostrar)))
                        .addGroup(lytEmpleado.createParallelGroup(Alignment.LEADING)
                                .addComponent(btnActualizar)
                                .addComponent(btnEliminar))
        );

        pnlEmpleado.setLayout(lytEmpleado);
        pnlEmpleado.setBackground(COLOR_FONDO);
        JScrollPane scrEmpleado = new JScrollPane(pnlEmpleado);
        scrEmpleado.getVerticalScrollBar().setUnitIncrement(16);

        campos.put(EMPLEADOS.matricula.toString(), txtMatricula);
        campos.put(EMPLEADOS.salarioBasico.toString(), txtSalario);
        campos.put(EMPLEADOS.nombre.toString(), txtNombre);
        campos.put(EMPLEADOS.apellidoPaterno.toString(), txtApellidoP);
        campos.put(EMPLEADOS.apellidoMaterno.toString(), txtApellidoM);
        campos.put(EMPLEADOS.genero.toString() + "1", btnGenero[0]);
        campos.put(EMPLEADOS.genero.toString() + "2", btnGenero[1]);
        campos.put(EMPLEADOS.fechaNacimiento.toString(), dtcFechaNacimiento);
        campos.put(EMPLEADOS.fechaIngreso.toString(), dtcFechaIngreso);
        campos.put(EMPLEADOS.imagen.toString(), txtRutaImagen);

        return scrEmpleado;
    }

    private JComponent getComponent(String campo) {
        return campos.get(campo);
    }

    private JComponent getComponent(EMPLEADOS e) {
        return getComponent(e.toString());
    }

    /**
     * Obtener el dato contenido en el <code>JComponent</code> correspondiente
     * al campo indicado.
     *
     * @param campo El campo del que se desea obtener el dato.
     *
     * @return El dato del <code>JComponent</code> en forma de
     * <code>String</code>.
     */
    private String getDato(String campo) {
        JComponent cmp = campos.get(campo);

        if (cmp instanceof JTextField) {
            return ((JTextField) cmp).getText();
        } else if (cmp instanceof JTextArea) {
            return ((JTextArea) cmp).getText();
        } else if (cmp instanceof JComboBox) {
            return (String) ((JComboBox) cmp).getSelectedItem();
        } else if (cmp instanceof JDateChooser) {
            return (new SimpleDateFormat("yyyy-MM-dd").format(((JDateChooser) cmp).getDate()));
        } else if (cmp instanceof TimePicker) {
            return (((TimePicker) cmp).getText() + ":00");
        } else {
            return "ERROR"; //Si el campo no es correcto, es decir, no corresponde a nigún campo actual
        }
    }

    /**
     * Obtener el dato contenido en el <code>JComponent</code> correspondiente
     * al campo indicado.
     * <p>
     * Se utiliza una instancia del <code>enum</code> para faciltar su uso.
     * </p>
     *
     * @param campo El campo del que se desea obtener el dato.
     *
     * @return El dato del <code>JComponent</code> en forma de
     * <code>String</code>.
     */
    private String getDato(EMPLEADOS e) {
        return getDato(e.toString());
    }

    /**
     * Cambiar el dato que se muestra en el <code>JComponent</code>
     * correspondiente al campo indicado.
     *
     * @param campo El campo del que se desea modificar el dato.
     * @param valor El nuevo valor que se le desea dar al campo.
     */
    private void setDato(String campo, String valor) {
        JComponent cmp = campos.get(campo);

        if (cmp instanceof JTextField) {
            ((JTextField) cmp).setText(valor);
        } else if (cmp instanceof JTextArea) {
            ((JTextArea) cmp).setText(valor);
        } else if (cmp instanceof JComboBox) {
            ((JComboBox) cmp).setSelectedItem(valor);
        } else if (cmp instanceof JDateChooser) {
            try {
                ((JDateChooser) cmp).setDate(new SimpleDateFormat("yyyy-MM-dd").parse(valor));
            } catch (ParseException ex) { //si el formato de la fecha es incorrecto
                JOptionPane.showMessageDialog(null, "Error al recuperar información de las fechas:\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Cambiar el dato que se muestra en el <code>JComponent</code>
     * correspondiente al campo indicado.
     * <p>
     * Se utiliza una instancia del <code>enum</code> para faciltar su uso.
     * </p>
     *
     * @param campo El campo del que se desea modificar el dato.
     * @param valor El nuevo valor que se le desea dar al campo.
     */
    private void setDato(EMPLEADOS e, String valor) {
        setDato(e.toString(), valor);
    }

    /**
     * Actualizar el tamaño preferido del <code>JPanel</code> para mostrarlo
     * correctamente en pantalla.
     */
    public void repaint() {
        Dimension d = tbpPrincipal.getSize();
        pnlEmpleado.setPreferredSize(new Dimension(d.width - 23, pnlEmpleado.getPreferredSize().height)); //Actualizar el tamaño preferido
        pnlEmpleado.revalidate();
        pnlEmpleado.repaint();
    }

    /**
     * Crear un <code>JLabel</code> con las propiedades adecuadas para
     * mostrarlas en los formularios, como tamaño, fuente de la letra,
     * alineación, entre otras.
     *
     * @param texto El texto que se desea mostrar en este <code>JLabel</code>.
     *
     * @return Un <code>JLabel</code> con las propiedades adecuadas para
     * mostrarlas en los formularios.
     */
    private JLabel crearJLabel(String texto) {
        JLabel lbl = new JLabel(" " + texto + " "); //Agregar espacios para que no se vea justo en el borde
        lbl.setMaximumSize(new Dimension(lbl.getMaximumSize().width, ALTURA_FORMULARIO));
        lbl.setOpaque(false);
        lbl.setFont(FUENTE_FORMULARIO);
        lbl.setHorizontalAlignment(JLabel.CENTER);

        return lbl;
    }

    /**
     * Crear un <code>JTextField</code> con las propiedades adecuadas para
     * mostrarlas en los formularios, como tamaño, fuente de la letra,
     * alineación, entre otras.
     *
     * @return Un <code>JTextField</code> con las propiedades adecuadas para
     * mostrarlas en los formularios.
     */
    private JTextField crearJTextField() {
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(Short.MAX_VALUE, ALTURA_FORMULARIO));
        txt.setMinimumSize(new Dimension(0, ALTURA_FORMULARIO));
        txt.setFont(FUENTE_FORMULARIO);
        txt.setMargin(new Insets(0, 0, 0, 0));
        txt.setHorizontalAlignment(JTextField.LEFT);
        txt.setDisabledTextColor(COLOR_DISABLED);

        return txt;
    }

    /**
     * Crear un <code>JPanel</code> con el aspecto de un separador con el título
     * deseado.
     *
     * @param texto El título del separador.
     *
     * @return Un <code>JPanel</code> con el aspecto de un separador.
     */
    private JPanel crearSeparador(String texto) {
        JPanel pnl = new JPanel();
        LayoutManager lyt = new BoxLayout(pnl, BoxLayout.X_AXIS);
        pnl.setBackground(COLOR_TRANSPARENTE);

        pnl.setLayout(lyt);

        JLabel lbl = new JLabel(" " + texto + "  "); //Label que funciona como título. Agregar espacios para que no se vea justo en el borde
        lbl.setFont(new Font("", 0, 14));
        lbl.setOpaque(false);
        lbl.setHorizontalAlignment(JLabel.LEFT);

        JLabel spr = new JLabel(); //Label que funciona como separador
        spr.setOpaque(true);
        spr.setBackground(Color.GRAY);
        spr.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));

        pnl.add(lbl);
        pnl.add(spr);

        return pnl;
    }

}
