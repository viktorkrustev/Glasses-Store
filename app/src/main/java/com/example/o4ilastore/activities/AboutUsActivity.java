package com.example.o4ilastore.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.o4ilastore.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView facebookIcon, instagramIcon, twitterIcon;
    private TextView phoneNumberText;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Задължително за osmdroid!
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_about_us);

        // Инициализация на изгледите
        facebookIcon = findViewById(R.id.facebookIcon);
        instagramIcon = findViewById(R.id.instagramIcon);
        twitterIcon = findViewById(R.id.twitterIcon);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        mapView = findViewById(R.id.mapView);

        // Настройки на картата
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        // Задаване на географска точка (София)
        GeoPoint sofiaPoint = new GeoPoint(42.6977, 23.3219);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(sofiaPoint);

        // Добавяне на маркер за GlassesStore
        Marker marker = new Marker(mapView);
        marker.setPosition(sofiaPoint);
        marker.setTitle("GlassesStore – София");
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        // Обработване на клик за социални мрежи
        facebookIcon.setOnClickListener(v -> openSocialLink("https://www.facebook.com/profile.php?id=61574982463367"));
        instagramIcon.setOnClickListener(v -> openSocialLink("https://www.instagram.com/nnoir8012/?fbclid=IwY2xjawJqIM9leHRuA2FlbQIxMAABHrEz0xbgUIfdF9gQG8aQrjPbAMiSnRIMWNGryfO_y_75GeQFHFqg_sX78i9D_aem_u9b9F8vGJPms6W0R9Cf3bg"));
        twitterIcon.setOnClickListener(v -> openSocialLink("https://twitter.com/GlassesStore"));

        // Обработване на клик за телефонно обаждане
        phoneNumberText.setOnClickListener(v -> makePhoneCall("tel:+359888123456"));
    }

    // Отваряне на линк към социална мрежа
    private void openSocialLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // Осуществяване на телефонно обаждане
    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        startActivity(intent);
    }

    // За osmdroid
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // За osmdroid
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // За osmdroid
    }
}
