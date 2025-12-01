package com.example.syncmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
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

public class HistoricoEventoActivity extends AppCompatActivity {

    private ImageView backArrow;
    private LinearLayout container;

    private static final String BASE_URL = "http://10.0.2.2/syncmeet/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historico_evento);

        backArrow = findViewById(R.id.back_arrow);
        container = findViewById(R.id.container_historico); // ID CORRETO

        backArrow.setOnClickListener(v -> finish());

        carregarEventos();
    }

    private void carregarEventos() {
        String url = BASE_URL + "list_events.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (!response.getBoolean("success")) {
                            Toast.makeText(this, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray eventos = response.getJSONArray("eventos");
                        container.removeAllViews();

                        if (eventos.length() == 0) {
                            Toast.makeText(this, "Nenhum evento encontrado!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < eventos.length(); i++) {
                            JSONObject ev = eventos.getJSONObject(i);

                            int id = ev.getInt("id");
                            String nome = ev.getString("nome_evento");
                            String data = ev.getString("data_evento");
                            String hora = ev.getString("hora_evento");

                            adicionarBotaoEvento(id, nome, data, hora);
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao ler dados", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Falha ao carregar eventos", Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void adicionarBotaoEvento(int id, String nome, String data, String hora) {

        MaterialButton botao = new MaterialButton(
                this,
                null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle
        );

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 25);
        botao.setLayoutParams(lp);

        botao.setText(nome + " - " + data + " " + hora);
        botao.setAllCaps(false);
        botao.setCornerRadius(18);
        botao.setTextColor(getResources().getColor(android.R.color.white));
        botao.setBackgroundTintList(getResources().getColorStateList(R.color.purple_syncmeet));

        botao.setOnClickListener(v -> {
            Intent intent = new Intent(HistoricoEventoActivity.this, HistoricoEventoDetailActivity.class);
            intent.putExtra("id", id);  // Só o ID, o resto será buscado no servidor
            startActivity(intent);
        });

        container.addView(botao);
    }
}
