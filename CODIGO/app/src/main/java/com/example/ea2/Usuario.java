package com.example.ea2;

import android.content.Context;
import android.widget.Toast;

public class Usuario {

    private static Usuario miUsuario;
    String email;
    String Password;
    String apellido;
    Integer comision;
    Integer dni;
    String Nombre;
    String Token;
    String Token_refresh;


    public static Usuario getInstance() {
        if (miUsuario == null) {
            miUsuario = new Usuario();
        }
        return miUsuario;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getToken_refresh() {
        return Token_refresh;
    }

    public void setToken_refresh(String token_refresh) {
        Token_refresh = token_refresh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getComision() {
        return comision;
    }

    public void setComision(Integer comision) {
        this.comision = comision;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }


    boolean validarFormularioRegistro(Context context) {
        if (this.apellido.isEmpty() || this.Nombre.isEmpty() || this.Password.isEmpty() ||
                this.comision.toString().isEmpty() || this.dni.toString().isEmpty() ||
                this.email.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Los campos no deben estar vacíos", Toast.LENGTH_SHORT).show();
            return false;
        } else if (this.Password.length() < 8) {
            Toast.makeText(context.getApplicationContext(), "La contraseña debe tener más de 8 caracteres"+ this.Password , Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }
}
