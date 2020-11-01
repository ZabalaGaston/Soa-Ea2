package com.example.ea2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView acelerometro;
    private TextView proximity;
    private TextView luminosidad;
    private TextView gravedad;
    private boolean estado = false;
    private String valorLuminosidad;
    private String valorDistancia;
    private String valorAcelerometroX;
    private String valorGravedadX;
    private long iniToken;
    RefreshToken2 rf2 = new RefreshToken2();
    private SharedPreferences preferences;

    Usuario usuario;

    DecimalFormat dosdecimales = new DecimalFormat("###.###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button btnParar =  findViewById(R.id.idBtnParar);
        Button btnComenzar =  findViewById(R.id.idIniciar);
        Button btnSalir =  findViewById(R.id.idSalir);

        acelerometro = (TextView) findViewById(R.id.idAcelerometro);
        proximity = (TextView) findViewById(R.id.idProximidad);
        luminosidad = (TextView) findViewById(R.id.idLuminosidad);
        gravedad = (TextView) findViewById(R.id.idGravedad);

        preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", MODE_PRIVATE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        btnComenzar.setOnClickListener(botonesListeners);
        btnParar.setOnClickListener(botonesListeners);
        btnSalir.setOnClickListener(botonesListeners);

        mostrarValoresIniciales();
        usuario = Usuario.getInstance();

        saveInSharedPreferencesLong("IniToken", System.currentTimeMillis());
        iniToken = preferences.getLong("IniToken",System.currentTimeMillis());

        //new RefreshToken2().execute();
        verificarFinToken();

    }

    private View.OnClickListener botonesListeners = new View.OnClickListener() {
        public void onClick(View v) {
            ConexionInternet connection = new ConexionInternet();
            if (!connection.checkearInternet(MainActivity.this))
                return;

            switch (v.getId()) {
                case R.id.idBtnParar:
                    if (estado) {
                        StopSensores();
                        estado = false;
                        eventReq("Apagar sensores");
                        verificarFinToken();
                    }

                    break;
                case R.id.idIniciar:
                    if (!estado) {
                        IniSensores();
                        estado = true;
                        eventReq("Encender sensores");
                        verificarFinToken();
                    }
                    break;
                case R.id.idSalir:
                    StopSensores();
                    eventReq("Salir de la app");
                    desloguearse();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Error en Listener de botones", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void desloguearse() {
        Usuario usuario = Usuario.getInstance();
        usuario.setToken_refresh("");
        usuario.setToken("");
        usuario.setNombre("");
        usuario.setEmail("");
        usuario.setPassword("");
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    // Metodo para iniciar el acceso a los sensores
    protected void IniSensores() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void StopSensores() {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
    }

    private void saveSharedSensores() {
        saveInSharedPreferences(Configuracion.SENSOR_DISTANCIA, valorDistancia);
        saveInSharedPreferences(Configuracion.SENSOR_LUZ, valorLuminosidad);
        saveInSharedPreferences(Configuracion.SENSOR_GRAVEDAD, valorGravedadX);
        saveInSharedPreferences(Configuracion.SENSOR_ACELEROMETRO, valorAcelerometroX);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        verificarFinToken();
        super.onStart();

    }

    @Override
    protected void onResume() {
        verificarFinToken();
        super.onResume();
        if (estado)
            IniSensores();
    }

    @Override
    protected void onPause() {
        verificarFinToken();
        StopSensores();
        super.onPause();
    }

    @Override
    protected void onStop() {
        verificarFinToken();
        super.onStop();
        StopSensores();
        saveSharedSensores();
    }

    @Override
    protected void onRestart() {
        verificarFinToken();
        super.onRestart();
        if (estado)
            IniSensores();
    }

    @Override
    protected void onDestroy() {
        StopSensores();
        saveSharedSensores();
        super.onDestroy();
    }

    public void mostrarValoresIniciales() {
        String txt = preferences.getString(Configuracion.SENSOR_ACELEROMETRO, "");
        if (txt.isEmpty()) {
            txt += "  Acelerometro:\n" + "  x: No hay valores previos\n  y: No hay valores previos\n  z:No hay valores previos";
        }
        acelerometro.setText(txt);

        txt = preferences.getString(Configuracion.SENSOR_DISTANCIA, "");
        if (txt.isEmpty()) {
            txt += "  Proximidad:\n" + "  No hay valores previos\n";
        }
        proximity.setText(txt);

        txt = preferences.getString(Configuracion.SENSOR_LUZ, "");
        if (txt.isEmpty()) {
            txt += "  Luminosidad:\n" + "  No hay valores previos\n";
        }
        luminosidad.setText(txt);

        txt = preferences.getString(Configuracion.SENSOR_ACELEROMETRO, "");
        if (txt.isEmpty()) {
            txt += "  Gravedad:\n" + "  x: No hay valores previos\n  y: No hay valores previos\n  z:No hay valores previos";
        }
        gravedad.setText(txt);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String txt = "";
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    txt += "  Acelerometro:\n";
                    txt += "  x: " + dosdecimales.format(event.values[0]) + " m/seg2\n";
                    txt += "  y: " + dosdecimales.format(event.values[1]) + " m/seg2\n";
                    txt += "  z: " + dosdecimales.format(event.values[2]) + " m/seg2\n";
                    acelerometro.setText(txt);
                    valorAcelerometroX = txt;
                    break;

                case Sensor.TYPE_PROXIMITY:
                    txt += "  Proximidad:\n";
                    txt += "  " + event.values[0] + "\n";
                    proximity.setText(txt);
                    valorDistancia = txt;
                    break;

                case Sensor.TYPE_LIGHT:
                    txt += "  Luminosidad:\n";
                    txt += "   " + event.values[0] + " Luz" + "\n";
                    luminosidad.setText(txt);
                    valorLuminosidad = txt;
                    break;

                case Sensor.TYPE_GRAVITY:
                    txt += "  Gravedad:\n";
                    txt += "  x: " + dosdecimales.format(event.values[0]) + "\n";
                    txt += "  y: " + dosdecimales.format(event.values[1]) + "\n";
                    txt += "  z: " + dosdecimales.format(event.values[2]) + "\n";
                    gravedad.setText(txt);
                    valorGravedadX = txt;
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void saveInSharedPreferences(String sensor, String valor) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(sensor, valor);
        editor.apply();
    }

    private void saveInSharedPreferencesLong(String sensor, long valor) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(sensor, valor);
        editor.apply();
    }


    private void verificarFinToken() {

        if (System.currentTimeMillis() - iniToken >= 1680000 && System.currentTimeMillis() <=1800000 ) {
            rf2.refrescarToken();
            iniToken = System.currentTimeMillis();
            saveInSharedPreferences("IniToken", String.valueOf(System.currentTimeMillis()));
        }else if (System.currentTimeMillis() - iniToken >= 1800000)
            desloguearse();
    }

    private void eventReq(String descrEvento) {
        JSONObject obj = new JSONObject();
        Usuario usuario = Usuario.getInstance();

        String token = usuario.getToken();
        try {
            obj.put("env", Configuracion.ENV);
            obj.put("type_events", Configuracion.TYPE_EVENT_BOTON);
            obj.put("description", descrEvento);
            Intent i = new Intent(MainActivity.this, ServicesHttp_POST.class);
            i.putExtra("uri", Configuracion.URI_EVENT);
            i.putExtra("datosJson", obj.toString());
            i.putExtra("operation", Configuracion.RESPUESTA_EVT);
            i.putExtra("typeRequest", "POST");
            i.putExtra("token", token);
            startService(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
