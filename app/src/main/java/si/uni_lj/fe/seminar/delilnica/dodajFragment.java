package si.uni_lj.fe.seminar.delilnica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class dodajFragment extends AppCompatActivity {
    String odgovor;
    String urlStoritve = "http://192.168.1.40:81/fragment.php";
    String urlFragmenta = urlStoritve + "?o=";
    int status_koda = 0;

    //TextView vsebina, ime, datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dodaj);

        //String oznaka = getIntent().getStringExtra("oznaka");
        TextView ime = (TextView) findViewById(R.id.dod_ime);
        TextView besedilo = (TextView) findViewById(R.id.dod_besedilo);
        Button gumb_dodaj = (Button) findViewById(R.id.dod_gumb);
        Button nazaj = (Button) findViewById(R.id.dod_nazaj);

        SharedPreferences sh = getSharedPreferences("nastavitve", MODE_PRIVATE);
        String zeton   = sh.getString("zeton", "");

        gumb_dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String oznaka = connect(urlStoritve, ime.getText().toString(), besedilo.getText().toString(), false, zeton);

                    AlertDialog.Builder builder = new AlertDialog.Builder(dodajFragment.this);
                    builder.setTitle("Uspeh");
                    builder.setMessage("Fragment je uspešno naložen z oznako " + oznaka + ".");

                    builder.setPositiveButton("OK", null);
                    builder.setNeutralButton("Ogled", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getBaseContext(), prikaziFragment.class);
                            intent.putExtra("oznaka", oznaka);
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //Toast.makeText(getApplicationContext(), "Odziv: " + status_koda,Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Napaka " + status_koda,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        nazaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showAlertDialogButtonClicked(View view, String naslov, String sporocilo) {
        // setup the alert builder

    }

    private String connect(String urlStoritve, String ime, String besedilo, boolean zaseben, String zeton) throws IOException {
        URL url = new URL(urlStoritve);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod("POST");
        if (!zeton.isEmpty()) {
            conn.setRequestProperty("Authorization", zeton);
            //Toast.makeText(getApplicationContext(), "zeton: " + zeton,Toast.LENGTH_LONG).show();
        }
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);

        try {
            JSONObject json = new JSONObject();
            json.put("ime", ime);
            json.put("besedilo", besedilo);
            json.put("zaseben", zaseben ? 1 : 0);

            // Starts the query
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json.toString());
            writer.flush();
            writer.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        status_koda = conn.getResponseCode();

        String responseAsString = convertStreamToString(conn.getInputStream());
        String _koda = "";

        try {
            _koda = new JSONObject(responseAsString).getString("response");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return _koda;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}