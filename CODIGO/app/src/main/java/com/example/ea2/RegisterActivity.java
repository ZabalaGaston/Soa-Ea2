package com.example.ea2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtNombre;
    private EditText txtApellido;
    private EditText txtDni;
    private EditText txtPassword;
    private EditText txtComision;
    private Intent intent;

    private ReceptorOperacion receiver = new ReceptorOperacion();
    public static Activity register;
    public IntentFilter filtro;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        register = this;
        Button btnRegistrar = (Button) findViewById(R.id.idIniciar);
        Button btnVolver = (Button) findViewById(R.id.buttonVolver);

        txtEmail = (EditText) findViewById(R.id.idEmail);
        txtPassword = (EditText) findViewById(R.id.idContrasenia);
        txtNombre = (EditText) findViewById(R.id.idNombre);
        txtApellido = (EditText) findViewById(R.id.idApellido);
        txtDni = (EditText) findViewById(R.id.idDni);
        txtComision = (EditText) findViewById(R.id.idCommission);

        context = this;

        configurarBroadcastReciever();

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegistrar.setOnClickListener(HandlerCmdRegistrar);
    }

    private View.OnClickListener HandlerCmdRegistrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ConexionInternet connection = new ConexionInternet();

            if (!connection.checkearInternet(RegisterActivity.this)) {
                return;
            }

            String nombre = txtNombre.getText().toString();
            String apellido = txtApellido.getText().toString();
            int dni = Integer.parseInt((txtDni.getText().toString()).isEmpty() ? String.valueOf(0) : txtDni.getText().toString());
            String email = txtEmail.getText().toString();
            String passw = txtPassword.getText().toString();
            int comision = Integer.parseInt((txtComision.getText().toString()).isEmpty() ? String.valueOf(0) : txtDni.getText().toString());

            Usuario usuario = Usuario.getInstance();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setEmail(email);
            usuario.setPassword(passw);
            usuario.setComision(comision);

            JSONObject obj = new JSONObject();
            try {
                obj.put("env", Configuracion.ENV);
                obj.put("name", nombre);
                obj.put("lastname", apellido);
                obj.put("dni", dni);
                obj.put("email", email);
                obj.put("password", passw);
                obj.put("commission", comision);

                if (!usuario.validarFormularioRegistro(RegisterActivity.this))
                    return;

                Intent i = new Intent(RegisterActivity.this, ServicesHttp_POST.class);

                i.putExtra("uri", Configuracion.URI_REGISTER);
                i.putExtra("datosJson", obj.toString());
                i.putExtra("operation", Configuracion.RESPUESTA_REGISTRO);
                i.putExtra("typeRequest", "POST");
                i.putExtra("token", "");
                startService(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void configurarBroadcastReciever() {
        filtro = new IntentFilter(Configuracion.RESPUESTA_REGISTRO);
        filtro.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filtro);
    }

    public class ReceptorOperacion extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            try {
                String datosJsonString = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJsonString);

                boolean rta;
                rta = datosJson.getBoolean("success");

                if (rta) {

                    Usuario usuario = Usuario.getInstance();
                    usuario.setToken(datosJson.getString("token"));
                    usuario.setToken_refresh(datosJson.getString("token_refresh"));

                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Los datos ingresados no son correctos", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        configurarBroadcastReciever();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}