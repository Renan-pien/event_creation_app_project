package com.example.syncmeet;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class AgendaEventoActivity extends AppCompatActivity {

    private ImageView backArrow;
    private LinearLayout containerAgenda;

    private static final String BASE_URL = "http://10.0.2.2/syncmeet/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda_evento);

        backArrow = findViewById(R.id.back_arrow);
        containerAgenda = findViewById(R.id.container_agenda); // certifique-se que existe no XML

        backArrow.setOnClickListener(v -> finish());

        carregarAgendaDoDia();
    }

    private void carregarAgendaDoDia() {

        String url = BASE_URL + "list_events.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (!response.getBoolean("success")) {
                            Toast.makeText(this, "Erro ao carregar agenda", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray eventos = response.getJSONArray("eventos");

                        containerAgenda.removeAllViews();

                        if (eventos.length() == 0) {
                            Toast.makeText(this, "Nenhum evento encontrado.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < eventos.length(); i++) {

                            JSONObject ev = eventos.getJSONObject(i);

                            int id = ev.getInt("id");
                            String nome = ev.getString("nome_evento");
                            String data = ev.getString("data_evento");
                            String hora = ev.getString("hora_evento");

                            adicionarItemAgenda(id, nome, data, hora);
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao ler agenda", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Falha ao carregar agenda.", Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void adicionarItemAgenda(int id, String nome, String data, String hora) {

        MaterialButton item = new MaterialButton(
                this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle
        );

        item.setText(nome + "\n" + data + " • " + hora);
        item.setAllCaps(false);
        item.setCornerRadius(18);
        item.setClickable(false);

        // estilização
        item.setTextColor(getResources().getColor(android.R.color.white));
        item.setBackgroundTintList(getResources().getColorStateList(R.color.purple_syncmeet));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp.setMargins(0, 0, 0, 25);
        item.setLayoutParams(lp);

        containerAgenda.addView(item);
    }
}
