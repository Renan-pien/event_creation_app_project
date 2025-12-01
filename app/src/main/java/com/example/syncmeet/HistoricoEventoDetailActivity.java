package com.example.syncmeet;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HistoricoEventoDetailActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView nomeEvento, local, data, horarioInicio, horarioTermino;

    private static final String BASE_URL = "http://10.0.2.2/syncmeet/get_event.php";
    private int idEvento = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historico_evento_detail);

        backArrow = findViewById(R.id.back_arrow);
        nomeEvento = findViewById(R.id.detalhe_nome_evento);
        local = findViewById(R.id.detalhe_local);
        data = findViewById(R.id.detalhe_data);
        horarioInicio = findViewById(R.id.detalhe_horario_inicio);
        horarioTermino = findViewById(R.id.detalhe_horario_termino);

        backArrow.setOnClickListener(v -> finish());

        // Recebe ID real do evento
        idEvento = getIntent().getIntExtra("id", -1);

        if (idEvento == -1) {
            Toast.makeText(this, "Erro: Evento inválido!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDetalhes();
    }

    private void carregarDetalhes() {

        String url = BASE_URL + "?id=" + idEvento;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {

                        if (!response.getBoolean("success")) {
                            Toast.makeText(this, "Evento não encontrado!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        JSONObject evento = response.getJSONObject("evento");

                        nomeEvento.setText(evento.getString("nome_evento"));
                        local.setText(evento.getString("local_evento"));

                        // converter YYYY-MM-DD → DD/MM/YYYY
                        String dataMysql = evento.getString("data_evento");
                        String[] partes = dataMysql.split("-");
                        data.setText(partes[2] + "/" + partes[1] + "/" + partes[0]);

                        horarioInicio.setText(evento.getString("hora_evento"));

                        // Como só existe 1 horário no banco, repito:
                        horarioTermino.setText("--:--"); // Se quiser, deixe vazio

                    } catch (JSONException e) {
                        Log.e("HISTORICO_DETAIL", "Erro ao processar JSON", e);
                        Toast.makeText(this, "Erro ao ler dados!", Toast.LENGTH_SHORT).show();
                    }
                },

                error -> {
                    Toast.makeText(this, "Erro ao conectar ao servidor!", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
