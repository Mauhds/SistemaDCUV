package sistemadcuv.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import sistemadcuv.modelo.ConexionBD;
import sistemadcuv.modelo.pojo.Desarrollador;

public class DesarrolladorDAO {
    public static HashMap<String, Object> verificarSesionDesarrollador(String usuario,String contrasenia){
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);
        Connection conexionBD = ConexionBD.obtenerConexion();
        if(conexionBD != null){
            try {
                String consulta = "SELECT idDesarrollador, nombreCompleto, "+
                        "semestre, matricula ,correo , estado, contrasenia, Proyecto_idProyecto,nombre \n" +
                        "FROM " +
                        "desarrollador, proyecto \n" +
                        "WHERE matricula = ? AND contrasenia = ? AND Proyecto_idProyecto = idProyecto";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                prepararSentencia.setString(1, usuario);
                prepararSentencia.setString(2, contrasenia);
                ResultSet resultado = prepararSentencia.executeQuery();
                if(resultado.next()){
                    respuesta.put("error", false);
                    respuesta.put("mensaje", "Credenciales correctas");
                    Desarrollador desarrollador = new Desarrollador();                    
                    desarrollador.setIdDesarrollador(resultado.getInt("idDesarrollador"));
                    desarrollador.setNombreCompleto(resultado.getString("nombreCompleto"));
                    desarrollador.setSemestre(resultado.getInt("semestre"));
                    desarrollador.setMatricula("matricula");
                    desarrollador.setEstado(resultado.getString("estado"));
                    desarrollador.setContrasenia(resultado.getString("contrasenia"));
                    desarrollador.setIdProyecto(resultado.getInt("Proyecto_idProyecto"));
                    desarrollador.setNombreProyecto(resultado.getString("nombre"));
                    desarrollador.setCorreo(resultado.getString("correo"));
                    respuesta.put("desarrollador", desarrollador);
                }else{
                    respuesta.put("mensaje", "Las credenciales son incorrectas");
                }
                conexionBD.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("desarrollador", "Error "+ex.getMessage());
            }
        }else{
            respuesta.put("mensaje", "Por el momento no hay conexion,"
                    + "por favor intentelo mas tarde.");
        }
        return respuesta;
    }
    public static HashMap<String,Object> verificarAsignaciones(Desarrollador desarrollador){
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);
        Connection conexionBD = ConexionBD.obtenerConexion();
        if(conexionBD != null){
            try {
                String consulta = "SELECT \n" +
                    "    (SELECT COUNT(*) FROM Cambio WHERE Desarrollador_idDesarrollador = d.idDesarrollador AND estado = 'pendiente') + \n" +
                    "    (SELECT COUNT(*) FROM SolicitudDeCambio WHERE Desarrollador_idDesarrollador = d.idDesarrollador AND estatus = 'pendiente') + \n" +
                    "    (SELECT COUNT(*) FROM Defecto WHERE Desarrollador_idDesarrollador = d.idDesarrollador AND estado = 'pendiente') + \n" +
                    "    (SELECT COUNT(*) FROM Actividad WHERE Desarrollador_idDesarrollador = d.idDesarrollador AND estado = 'pendiente') AS total_asignaciones_pendientes \n" +
                    "FROM Desarrollador d \n" +
                    "WHERE d.idDesarrollador = ?;";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                prepararSentencia.setInt(1, desarrollador.getIdDesarrollador());
                ResultSet resultado = prepararSentencia.executeQuery();
                if(resultado.next()){
                    respuesta.put("asignaciones", resultado.getInt("total_asignaciones_pendientes"));
                }else{
                    respuesta.put("mensaje", "Las credenciales son incorrectas");
                }
                conexionBD.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                respuesta.put("asignaciones", "Error "+ex.getMessage());
            }
        }else{
            respuesta.put("mensaje", "Por el momento no hay conexion,"
                    + "por favor intentelo mas tarde.");
        }
        return respuesta;
    }
    public static HashMap<String, Object> eliminarDesarrollador(Desarrollador desarrollador){
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);
        Connection conexionBD = ConexionBD.obtenerConexion();
        if(conexionBD != null){
            try {
                String consulta = "UPDATE desarrollador SET estado = 'INACTIVO'"  +
                                  "WHERE idDesarrollador = ?";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                prepararSentencia.setInt(1, desarrollador.getIdDesarrollador());
                int filasAfectadas = prepararSentencia.executeUpdate();
                conexionBD.close();
                if(filasAfectadas > 0){
                    respuesta.put("error", false);
                    respuesta.put("mensaje", "Desarrollador eliminado con exito");
                }else{
                    respuesta.put("mensaje", "Hubo un error al intentar modificar la informacion del paciente, por favor intentalo mas tarde");
                }
                respuesta.put("error", false);
            } catch (SQLException ex) {
                respuesta.put("mensaje", "Error: "+ex.getMessage());
            }
        }else{
            respuesta.put("mensaje", "Por el momento no hay conexion,"
                    + "por favor intentelo mas tarde.");
        }
        return respuesta;
    }
    public static HashMap<String, Object> registrarDesarrollador(Desarrollador desarrollador){
        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", true);        
        Connection conexionBD = ConexionBD.obtenerConexion();       
        if(conexionBD != null){
            try {
                String consulta = "INSERT INTO desarrollador (nombreCompleto,semestre,"
                        + "matricula,estado,correo,contrasenia,Proyecto_idProyecto)"
                        + " VALUES (?,?,?,?,?,?,?); ";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                prepararSentencia.setString(1, desarrollador.getNombreCompleto());
                prepararSentencia.setInt(2, desarrollador.getSemestre());
                prepararSentencia.setString(3, desarrollador.getMatricula());
                prepararSentencia.setString(4, "ACTIVO");
                prepararSentencia.setString(5, desarrollador.getCorreo());
                prepararSentencia.setString(6, desarrollador.getContrasenia());
                prepararSentencia.setInt(7, desarrollador.getIdProyecto());
                int filasAfectadas = prepararSentencia.executeUpdate();
                conexionBD.close();
                if(filasAfectadas > 0){
                    respuesta.put("error", false);
                    respuesta.put("mensaje", "El desarrollador fue guardado con exito");
                }else{
                    respuesta.put("mensaje", "Hubo un error al intentar registrar la informacion del desarrollador,"
                            + "  por favor intentalo mas tarde");
                }
                respuesta.put("error", false);
            } catch (SQLException ex) {
                respuesta.put("mensaje", "Error: "+ex.getMessage());
            }
        }else{
            respuesta.put("mensaje", "Por el momento no hay conexion,"
                    + "por favor intentelo mas tarde.");
        }
        return respuesta;
    }
}
