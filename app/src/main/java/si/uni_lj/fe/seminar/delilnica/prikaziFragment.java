package si.uni_lj.fe.seminar.delilnica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import static si.uni_lj.fe.seminar.delilnica.s_to_s.*;

public class prikaziFragment extends AppCompatActivity {
    int status_koda = 0;

    TextView vsebina, ime, datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        Button nazaj = (Button) findViewById(R.id.nazaj);

        String oznaka = getIntent().getStringExtra("oznaka");

        vsebina = (TextView) findViewById(R.id.vsebina);
        ime = (TextView) findViewById(R.id.ime);
        datum = (TextView) findViewById(R.id.datum);

        SharedPreferences sh = getSharedPreferences("nastavitve", MODE_PRIVATE);
        String zeton   = sh.getString("zeton", "");

        try {
            String api_ip = sh.getString("api_ip", "");
            String urlStoritve = "http://"+api_ip+":81/fragment.php?o=";
            String odgovor = connect(urlStoritve + oznaka, zeton);
            String _ime, _datum, _besedilo;
            try {
                JSONObject jObject = new JSONObject(odgovor).getJSONObject("response");
                _ime = jObject.getString("ime");
                _datum = jObject.getString("datum");
                _besedilo = jObject.getString("besedilo");

                ime.setText(_ime);
                datum.setText(_datum);
                vsebina.setText(_besedilo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Napaka " + status_koda + " - fragment ne obstaja.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        nazaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String connect(String naslov, String zeton) throws IOException {
        URL url = new URL(naslov);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("Accept", "application/json");
        if (!zeton.isEmpty()) {
            conn.setRequestProperty("Authorization", zeton);
            //Toast.makeText(getApplicationContext(), "zeton: " + zeton,Toast.LENGTH_LONG).show();
        }
        conn.connect();
        status_koda = conn.getResponseCode();

        String responseAsString = convertStreamToString(conn.getInputStream());
        return responseAsString;
    }

}