package com.example.ea2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Intent intent;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin, btnRegistrar;
    private ProgressBar spinner;
    public IntentFilter filtro;

    private ReceptorOperacion receiver = new ReceptorOperacion();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinner = findViewById(R.id.id_progress);
        spinner.setVisibility(View.GONE);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegistrar = (Button) findViewById(R.id.buttonRegistrarse);
        txtEmail = (EditText) findViewById(R.id.idEmail);
        txtPassword = (EditText) findViewById(R.id.idPass);
        configurarBroadcastReciever();
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConexionInternet connection = new ConexionInternet();
                Usuario usuario = Usuario.getInstance();
                if (!connection.checkearInternet(LoginActivity.this))
                    return;

                String email = txtEmail.getText().toString();
                String passw = txtPassword.getText().toString();

                usuario.setEmail(email);
                usuario.setPassword(passw);

                if (!usuario.validarLogin(LoginActivity.this))
                    return;
                spinner.setVisibility(View.VISIBLE);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", txtEmail.getText().toString());
                    obj.put("password", txtPassword.getText().toString());
                    Intent i = new Intent(LoginActivity.this, ServicesHttp_POST.class);
                    i.putExtra("uri", Configuracion.URI_LOGIN);
                    i.putExtra("datosJson", obj.toString());
                    i.putExtra("operation", Configuracion.RESPUESTA_LOGIN);
                    i.putExtra("typeRequest", "POST");
                    i.putExtra("token", "");
                    startService(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
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

    private void configurarBroadcastReciever() {
        filtro = new IntentFilter(Configuracion.RESPUESTA_LOGIN);
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

                spinner.setVisibility(View.GONE);
                if (rta) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);

                    Usuario usuario = Usuario.getInstance();
                    usuario.setToken(datosJson.getString("token"));
                    usuario.setTokenRefresh(datosJson.getString("token_refresh"));
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Los datos ingresados no son correctos", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

