package com.example.ea2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;

public class BatteryActivity extends AppCompatActivity {

    private TextView bateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        bateria = (TextView) findViewById(R.id.idBatteria);
        mostrarEstadoBatteria();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BatteryActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

    void mostrarEstadoBatteria() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int) (level * 100 / (float) scale);
        bateria.setText("Batería: " + batteryPct + "%");
    }
}
