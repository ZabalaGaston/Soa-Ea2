package com.example.ea2;

import android.content.Context;
import android.widget.Toast;

public class Usuario {

    private static Usuario miUsuario;
    String email;
    String password;
    String apellido;
    Integer comision;
    Integer dni;
    String nombre;
    String token;
    String tokenRefresh;


    public static Usuario getInstance() {
        if (miUsuario == null) {
            miUsuario = new Usuario();
        }
        return miUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenRefresh() {
        return tokenRefresh;
    }

    public void setTokenRefresh(String tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    boolean validarFormularioRegistro(Context context) {
        if (this.apellido.isEmpty() || this.nombre.isEmpty() || this.password.isEmpty() ||
                this.comision.toString().isEmpty() || this.dni.toString().isEmpty() ||
                this.email.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Los campos no deben estar vacíos", Toast.LENGTH_SHORT).show();
            return false;
        } else if (this.password.length() < Configuracion.MIN_CARACTER) {
            Toast.makeText(context.getApplicationContext(), "La contraseña debe tener más de 8 caracteres", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    boolean validarLogin(Context context) {
        if (this.password.isEmpty() && this.email.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Los campos usuario y contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return false;
        } else if (this.password.isEmpty() && !this.email.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Completar la contraseña", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!this.password.isEmpty() && this.email.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Completar el mail", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
