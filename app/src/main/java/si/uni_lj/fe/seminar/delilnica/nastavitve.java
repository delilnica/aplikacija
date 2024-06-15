package si.uni_lj.fe.seminar.delilnica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;

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
import static si.uni_lj.fe.seminar.delilnica.s_to_s.*;

public class nastavitve extends AppCompatActivity {
    String urlPrijave = "http://192.168.1.40:81/login.php";
    int status_koda = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nastavitve);

        TextView up_ime = (TextView) findViewById(R.id.up_ime);
        TextView up_geslo = (TextView) findViewById(R.id.up_geslo);
        TextView pocisti = (TextView) findViewById(R.id.pocisti);

        Button nazaj = (Button) findViewById(R.id.nazaj);

        SharedPreferences sh = getSharedPreferences("nastavitve", MODE_PRIVATE);
        String tmp_ime   = sh.getString("up_ime", "");
        String tmp_geslo = sh.getString("up_geslo", "");
        up_ime.setText(tmp_ime);
        up_geslo.setText(tmp_geslo);

        pocisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up_ime.setText("");
                up_geslo.setText("");

                SharedPreferences sharedPreferences = getSharedPreferences("nastavitve", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                // write all the data entered by the user in SharedPreference and apply
                myEdit.putString("up_ime", up_ime.getText().toString());
                myEdit.putString("up_geslo", up_geslo.getText().toString());
                myEdit.apply();

                Toast.makeText(getApplicationContext(), "Poverilnice počiščene.",Toast.LENGTH_LONG).show();
            }
        });

        nazaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!up_ime.getText().toString().isEmpty()) {
                    // Creating a shared pref object with a file name "MySharedPref" in private mode
                    SharedPreferences sharedPreferences = getSharedPreferences("nastavitve", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    // write all the data entered by the user in SharedPreference and apply
                    myEdit.putString("up_ime", up_ime.getText().toString());
                    myEdit.putString("up_geslo", up_geslo.getText().toString());
                    myEdit.apply();

                    SharedPreferences sh = getSharedPreferences("nastavitve", MODE_PRIVATE);
                    String tmp_ime = sh.getString("up_ime", "");
                    String tmp_geslo = sh.getString("up_geslo", "");

                    try {
                        String zeton = pridobi_zeton(urlPrijave, tmp_ime, tmp_geslo);
                        myEdit.putString("zeton", zeton);
                        myEdit.apply();
                        Toast.makeText(getApplicationContext(), "Poverilnice potrjene.", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Napaka " + status_koda, Toast.LENGTH_LONG).show();
                    }
                }
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String pridobi_zeton(String urlStoritve, String ime, String geslo) throws IOException {
        URL url = new URL(urlStoritve);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);

        try {
            JSONObject json = new JSONObject();
            json.put("vzdevek", ime);
            json.put("geslo", geslo);
            //json.put("zaseben", zaseben);

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
}
