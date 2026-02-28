package com.geowatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private TextView tvHours, tvMinutes, tvSeconds, tvDate;
    private TextView tvLat, tvLon, tvAlt, tvAccuracy, tvStatus;

    private LocationManager locationManager;
    private Handler clockHandler;
    private Runnable clockRunnable;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH", Locale.getDefault());
    private final SimpleDateFormat minFormat  = new SimpleDateFormat("mm", Locale.getDefault());
    private final SimpleDateFormat secFormat  = new SimpleDateFormat("ss", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("it", "IT"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHours    = findViewById(R.id.tv_hours);
        tvMinutes  = findViewById(R.id.tv_minutes);
        tvSeconds  = findViewById(R.id.tv_seconds);
        tvDate     = findViewById(R.id.tv_date);
        tvLat      = findViewById(R.id.tv_lat);
        tvLon      = findViewById(R.id.tv_lon);
        tvAlt      = findViewById(R.id.tv_alt);
        tvAccuracy = findViewById(R.id.tv_accuracy);
        tvStatus   = findViewById(R.id.tv_status);

        clockHandler = new Handler(Looper.getMainLooper());
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                clockHandler.postDelayed(this, 1000);
            }
        };
        clockHandler.post(clockRunnable);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkAndRequestPermission();

        findViewById(R.id.btn_refresh).setOnClickListener(v -> startLocationUpdates());
    }

    private void updateClock() {
        Date now = new Date();
        tvHours.setText(timeFormat.format(now));
        tvMinutes.setText(minFormat.format(now));
        tvSeconds.setText(secFormat.format(now));
        tvDate.setText(dateFormat.format(now));
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        tvStatus.setText("Acquisizione posizione...");
        tvLat.setText("—");
        tvLon.setText("—");
        tvAlt.setText("—");
        tvAccuracy.setText("Precisione: —");

        // Registra su tutti i provider disponibili sul dispositivo
        List<String> providers = locationManager.getAllProviders();
        boolean anyRegistered = false;

        for (String provider : providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    locationManager.requestLocationUpdates(provider, 2000, 0, this);
                    anyRegistered = true;
                }
            } catch (Exception ignored) {}
        }

        if (!anyRegistered) {
            tvStatus.setText("Nessun provider di posizione disponibile");
            return;
        }

        // Mostra subito l'ultima posizione nota dal provider migliore
        Location best = null;
        for (String provider : providers) {
            try {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l != null) {
                    if (best == null || l.getAccuracy() < best.getAccuracy()) {
                        best = l;
                    }
                }
            } catch (Exception ignored) {}
        }
        if (best != null) onLocationChanged(best);

        String providerInfo = providers.contains(LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ? "GPS" : "Rete/WiFi";
        tvStatus.setText("Acquisizione via " + providerInfo + "...");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String lat = formatCoord(location.getLatitude(), true);
        String lon = formatCoord(location.getLongitude(), false);
        String alt = location.hasAltitude()
                ? String.format(Locale.getDefault(), "%.0f m", location.getAltitude())
                : "— m";
        String acc = String.format(Locale.getDefault(), "±%.0f m", location.getAccuracy());

        tvLat.setText(lat);
        tvLon.setText(lon);
        tvAlt.setText(alt);
        tvAccuracy.setText("Precisione: " + acc + " via " + location.getProvider());

        SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        tvStatus.setText("Aggiornato: " + hms.format(new Date()));
    }

    private String formatCoord(double val, boolean isLat) {
        String dir = isLat ? (val >= 0 ? "N" : "S") : (val >= 0 ? "E" : "W");
        return String.format(Locale.US, "%.6f° %s", Math.abs(val), dir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                tvStatus.setText("Permesso posizione negato");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        clockHandler.removeCallbacks(clockRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockHandler.post(clockRunnable);
        checkAndRequestPermission();
    }
}
