package com.example.syncmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class EditarEventoActivity extends AppCompatActivity {

    private ImageView backArrow;
    private LinearLayout eventosContainer;

    private static final String BASE_URL = "http://10.0.2.2/syncmeet/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_evento);

        backArrow = findViewById(R.id.back_arrow);
        eventosContainer = findViewById(R.id.container);

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

                        boolean success = response.optBoolean("success", false);
                        if (!success) {
                            Toast.makeText(this, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray eventosArray = response.optJSONArray("eventos");

                        if (eventosArray == null || eventosArray.length() == 0) {
                            Toast.makeText(this, "Nenhum evento encontrado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        eventosContainer.removeAllViews();

                        for (int i = 0; i < eventosArray.length(); i++) {

                            JSONObject obj = eventosArray.getJSONObject(i);

                            int id = obj.getInt("id");
                            String nome = obj.getString("nome_evento");
                            String local = obj.getString("local_evento");
                            String data = obj.getString("data_evento");
                            String hora = obj.getString("hora_evento");

                            adicionarBotaoEvento(id, nome);

                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar eventos", Toast.LENGTH_LONG).show();
                    }

                },

                error -> Toast.makeText(this, "Erro de conexão ao carregar eventos", Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void adicionarBotaoEvento(int id, String nome) {

        MaterialButton botao = new MaterialButton(
                this,
                null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        int marginPx = (int) (16 * getResources().getDisplayMetrics().density);
        params.setMargins(0, 0, 0, marginPx);
        botao.setLayoutParams(params);

        botao.setText(nome);
        botao.setAllCaps(false);
        botao.setTextColor(getResources().getColor(android.R.color.white));
        botao.setCornerRadius(16);
        botao.setBackgroundTintList(getResources().getColorStateList(R.color.purple_syncmeet));

        botao.setOnClickListener(v -> {
            Intent intent = new Intent(EditarEventoActivity.this, EditarEventoDetailActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });

        eventosContainer.addView(botao);
    }
}
