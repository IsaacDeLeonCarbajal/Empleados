package empleado;

import java.sql.*;
import java.io.*;
import static empleado.ConexionSQL.EMPLEADOS;

public class Empleado {

    private ConexionSQL conexion;

    public Empleado(int id) {
        this.conexion = new ConexionSQL(true);
        
        this.insertarRegistro("C:\\Users\\PC1\\Documents\\Software\\Java\\projects\\Empleados\\res\\descarga.jpg");
    }

    public boolean insertarRegistro(String ruta) {
        String insert = "INSERT INTO " + EMPLEADOS.Empleados + " (" + EMPLEADOS.nombre + ", " + EMPLEADOS.apellidoPaterno + ", " + EMPLEADOS.apellidoMaterno + ", " + EMPLEADOS.genero + ", " + EMPLEADOS.fechaNacimiento + ", " + EMPLEADOS.fechaIngreso + ", " + EMPLEADOS.salarioBasico + ", " + EMPLEADOS.imagen + ") "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        FileInputStream fis = null;
        PreparedStatement ps = null;
        try {
//            conexion.setAutoCommit(false);
            File file = new File(ruta);
            fis = new FileInputStream(file);
            
            ps = conexion.preparedStatement(insert);
            
            ps.setString(1, "Isaac");
            ps.setString(2, "de León");
            ps.setString(3, "Carbajal");
            ps.setInt(4, 1);
            ps.setString(5, "2003-08-10");
            ps.setString(6, "2020-05-23");
            ps.setString(7, "20000");
            ps.setBinaryStream(8, fis, (int) file.length());
            ps.execute();
            
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
