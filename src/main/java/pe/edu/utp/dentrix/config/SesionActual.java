package pe.edu.utp.dentrix.config;

import pe.edu.utp.dentrix.model.Paciente;
import pe.edu.utp.dentrix.model.Rol;
import pe.edu.utp.dentrix.model.Usuario;

public final class SesionActual {

    private Usuario usuario;
    private Rol rol;
    private Paciente pacienteEnAtencion;
    private pe.edu.utp.dentrix.model.FichaClinica fichaCobro;
    private Paciente pacienteAgendable;
    private String pestanaCajaInicial;

    public void abrir(Usuario usuario, Rol rol) {
        this.usuario = usuario;
        this.rol = rol;
    }

    public void cerrar() {
        usuario = null;
        rol = null;
        pacienteEnAtencion = null;
        fichaCobro = null;
        pacienteAgendable = null;
        pestanaCajaInicial = null;
    }

    public String getPestanaCajaInicial() {
        return pestanaCajaInicial;
    }

    public void setPestanaCajaInicial(String pestanaCajaInicial) {
        this.pestanaCajaInicial = pestanaCajaInicial;
    }

    public pe.edu.utp.dentrix.model.FichaClinica getFichaCobro() {
        return fichaCobro;
    }

    public void setFichaCobro(pe.edu.utp.dentrix.model.FichaClinica fichaCobro) {
        this.fichaCobro = fichaCobro;
    }

    public Paciente getPacienteAgendable() {
        return pacienteAgendable;
    }

    public void setPacienteAgendable(Paciente pacienteAgendable) {
        this.pacienteAgendable = pacienteAgendable;
    }

    public Paciente getPacienteEnAtencion() {
        return pacienteEnAtencion;
    }

    public void setPacienteEnAtencion(Paciente pacienteEnAtencion) {
        this.pacienteEnAtencion = pacienteEnAtencion;
    }

    public boolean activa() {
        return usuario != null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombreCompleto() {
        if (usuario == null) {
            return "";
        }
        return usuario.getNombres() + " " + usuario.getApellidos();
    }

    public String getCodigoRol() {
        return rol == null ? "" : rol.getCodigo();
    }
}
