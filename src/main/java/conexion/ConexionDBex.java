/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 *
 * author luis
 */
public class ConexionDBex {
    
    public static void main(String[] args) {
        PoolDataSource pds = null;
        Connection conexion = null;
        try {
            // con esto aseguramos que las conexiones se usan de forma eficiente (Pools Connection)
            //crea la instanca con elpool
            pds = PoolDataSourceFactory.getPoolDataSource();
            //introducimos las condiciones de la conexión
            pds.setConnectionFactoryClassName("com.mysql.cj.jdbc.Driver");
            pds.setURL("jdbc:mysql://localhost:3307/ecuamayferrr");
            pds.setUser("root");
            pds.setPassword("admin");
            pds.setInitialPoolSize(5);
            
            //Obtenemos la conexion con la base de datos.
            conexion = pds.getConnection();
            
            System.out.println("\nConnection obtained from users");
            
            //Se ejecutan las operaciones con las bases de datos
            Statement stmt = conexion.createStatement();
            stmt.execute("select * from users");
            
            //**********  Ejecutamos las operacion con las bases de datos *********
            
            
            // Si no se lanza ninguna excepción, la conexión fue exitosa
            System.out.println("Conexión exitosa a la base de datos");
            
            //Añadir usuario
            aniadirUsuario(conexion, "Luis", "1234");
            
            //Eliminar usuario
            eliminarUsuario(conexion, "usuariopepe");

            // Realizar operaciones con la conexión mostramos los usuarios dentro de la tabla
            String consulta = "select * from users where username = ?";
            String consultaTodo = "select * from users";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            PreparedStatement sentenciaTodo = conexion.prepareStatement(consultaTodo);
            sentencia.setString(1, "usuario1");

            try {
                ResultSet rs = sentencia.executeQuery();
                ResultSet rsTodo = sentenciaTodo.executeQuery();

                // Aquí puedes procesar el ResultSet según tus necesidades
                while (rsTodo.next()) {
                    // Por ejemplo, obtener valores de columnas
                    int id = rsTodo.getInt("idusers");
                    String username = rsTodo.getString("username");
                    String password = rsTodo.getString("password");
                    // Haz algo con estos valores
                    // Otros campos según la estructura de tu tabla
                    System.out.println("ID: " + id + ", Username: " + username + ", Password: " + password);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConexionDBex.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            // Manejar la excepción en caso de error de conexión
            System.out.println("Error al conectar a la base de datos: " + ex.getMessage());
            ex.printStackTrace(); // Esto imprime la traza de la excepción para obtener más detalles visto en clases
        } finally {
            // Cerrar la conexión en el bloque finally para asegurar que siempre se cierre
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                // Manejar cualquier excepción al cerrar la conexión
                ex.printStackTrace();
            }
        }
    }

    public static boolean aniadirUsuario(Connection conexion, String user, String password) {
        try {
            // Realizar operaciones con la conexión , IMPORTANTE; SI NO PONEMOS (USERNAME, PASSWORD) Y PONEMOS DIRECTAMENTE
            //VALUES TENEMOS QUE ESPECIFICAR EN EL CAMPO DE ID(NULL,?,?,?...) POR EJEMPLO.
            String insercion = "INSERT INTO users (username, password) VALUES (?, ?)";
            //Con las sentencias preparadas evitamos que nos hagan injectSql y también nos permite ahorrarnos la construccion
            //de planes de ejecución
            PreparedStatement sentencia = conexion.prepareStatement(insercion);
            sentencia.setString(1, user);
            sentencia.setString(2, password);

            // Ejecutar la inserción
            int filasAfectadas = sentencia.executeUpdate();
            
            System.out.println("Se ha añadido el usuario correctamente desde java");
            // Verificar si la inserción fue exitosa (al menos una fila afectada)
            return filasAfectadas > 0;

        } catch (SQLException ex) {
            // Manejar la excepción en caso de error de la consulta
            System.out.println("Error al insertar usuario: " + ex.getMessage());
            ex.printStackTrace(); // Esto imprime la traza de la excepción para obtener más detalles
            try {
                conexion.close();
            } catch (SQLException ex1) {
                //Para cerrar la conexion java nos ha pedido crear este catch
                Logger.getLogger(ConexionDBex.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return false; // Indicar que la operación falló
        }
    }
    
    public static boolean eliminarUsuario(Connection conexion, String user) {
    try {
        // Realizar operaciones con la conexión
        String eliminacion = "DELETE FROM users WHERE username = ?";
        PreparedStatement sentencia = conexion.prepareStatement(eliminacion);
        sentencia.setString(1, user);

        // Ejecutar la eliminación
        int filasAfectadas = sentencia.executeUpdate();

        // Verificar si la eliminación fue exitosa (al menos una fila afectada)
        if (filasAfectadas > 0) {
            System.out.println("Se ha eliminado el usuario correctamente desde Java");
            return true;
        } else {
            System.out.println("No se encontró el usuario para eliminar");
            return false;
        }

    } catch (SQLException ex) {
        // Manejar la excepción en caso de error de la consulta
        System.out.println("Error al eliminar usuario: " + ex.getMessage());
        ex.printStackTrace(); // Esto imprime la traza de la excepción para obtener más detalles
        try {
            conexion.close();
        } catch (SQLException ex1) {
            //Para cerrar la conexion java nos ha pedido crear este catch
            Logger.getLogger(ConexionDBex.class.getName()).log(Level.SEVERE, null, ex1);
        }
        return false; // Indicar que la operación falló
    }
}
    
}