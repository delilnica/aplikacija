package si.uni_lj.fe.seminar.delilnica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText oznaka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oznaka = (EditText) findViewById(R.id.oznaka);

        Button odpri = (Button) findViewById(R.id.odpri);
        odpri.setOnClickListener(this);
        Button ustvari = (Button) findViewById(R.id.ustvari);
        ustvari.setOnClickListener(this);
        Button meni = (Button) findViewById(R.id.meni);
        meni.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();// cases applied over different buttons

        if (id == R.id.odpri) {
            if (oznaka.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Manjka oznaka.", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getApplicationContext(), "Poizvedba: " + name.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), prikaziFragment.class);
                intent.putExtra("oznaka", oznaka.getText().toString());
                startActivity(intent);
            }
        } else if (id == R.id.ustvari) {
            Intent intent = new Intent(getBaseContext(), dodajFragment.class);
            startActivity(intent);
        } else if (id == R.id.meni) {
            Intent intent = new Intent(getBaseContext(), nastavitve.class);
            startActivity(intent);
        }
    }

}