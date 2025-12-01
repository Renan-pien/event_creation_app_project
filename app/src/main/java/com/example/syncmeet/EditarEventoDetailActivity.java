package com.example.syncmeet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditarEventoDetailActivity extends AppCompatActivity {

    private ImageView backArrow;
    private EditText nomeEvento, local, data, horario;
    private Button salvarButton;

    private int idEvento = -1;

    private static final String BASE_URL = "http://10.0.2.2/syncmeet/";
    private static final String TAG = "EditarEventoDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_evento_detail);

        backArrow = findViewById(R.id.back_arrow);
        nomeEvento = findViewById(R.id.detalhe_nome_evento);
        local = findViewById(R.id.detalhe_local);
        data = findViewById(R.id.detalhe_data);
        horario = findViewById(R.id.detalhe_horario_inicio);
        salvarButton = findViewById(R.id.salvar_edicao_button);

        backArrow.setOnClickListener(v -> finish());

        idEvento = getIntent().getIntExtra("id", -1);
        if (idEvento == -1) {
            Toast.makeText(this, "Erro: ID inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDetalhesDoEvento();

        salvarButton.setOnClickListener(v -> atualizarEvento(idEvento));
    }

    private void carregarDetalhesDoEvento() {

        String url = BASE_URL + "get_event.php?id=" + idEvento;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (!response.getBoolean("success")) {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        JSONObject evento = response.getJSONObject("evento");

                        nomeEvento.setText(evento.getString("nome_evento"));
                        local.setText(evento.getString("local_evento"));

                        // converte yyyy-mm-dd → dd/mm/yyyy
                        String[] p = evento.getString("data_evento").split("-");
                        String dataFormatada = p[2] + "/" + p[1] + "/" + p[0];
                        data.setText(dataFormatada);

                        horario.setText(evento.getString("hora_evento"));

                    } catch (JSONException e) {
                        Log.e(TAG, "Erro JSON", e);
                        Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                error -> {
                    Toast.makeText(this, "Falha de conexão", Toast.LENGTH_SHORT).show();
                    finish();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void atualizarEvento(int idEvento) {

        String nome = nomeEvento.getText().toString().trim();
        String loc = local.getText().toString().trim();
        String dat = data.getText().toString().trim();
        String hor = horario.getText().toString().trim();

        if (nome.isEmpty() || loc.isEmpty() || dat.isEmpty() || hor.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 Validar se nova data/hora está no futuro
        if (!validarDataHora(dat, hor)) return;

        String url = BASE_URL + "update_event.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.optBoolean("success", false);
                        String message = obj.optString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (success) {

                            // cancela lembrete antigo e cria o novo
                            cancelarNotificacao(idEvento);
                            agendarNotificacao(idEvento, nome, dat, hor);

                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao interpretar resposta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao conectar servidor", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idEvento));
                params.put("nome", nome);
                params.put("local", loc);
                params.put("data", dat);
                params.put("hora", hor);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // ✔ Validação forte de data + hora
    private boolean validarDataHora(String dataStr, String horaStr) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            sdf.setLenient(false);

            Calendar evento = Calendar.getInstance();
            evento.setTime(sdf.parse(dataStr + " " + horaStr));

            Calendar agora = Calendar.getInstance();

            if (evento.getTimeInMillis() <= agora.getTimeInMillis()) {
                Toast.makeText(this,
                        "O evento deve ser no futuro!",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            return true;

        } catch (Exception e) {
            Toast.makeText(this, "Data ou hora inválida!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void cancelarNotificacao(int idEvento) {
        Intent intent = new Intent(this, EventoLembreteReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                idEvento,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);
    }

    private void agendarNotificacao(int idEvento, String nome, String dataStr, String horaStr) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();

            cal.setTime(sdf.parse(dataStr));

            String[] h = horaStr.split(":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(h[1]));
            cal.set(Calendar.SECOND, 0);

            // 40 minutos antes
            long triggerAt = cal.getTimeInMillis() - 40 * 60 * 1000;

            if (triggerAt <= System.currentTimeMillis()) return;

            Intent intent = new Intent(this, EventoLembreteReceiver.class);
            intent.putExtra(EventoLembreteReceiver.EXTRA_ID, idEvento);
            intent.putExtra(EventoLembreteReceiver.EXTRA_NOME, nome);
            intent.putExtra(EventoLembreteReceiver.EXTRA_DATA, dataStr);
            intent.putExtra(EventoLembreteReceiver.EXTRA_HORA, horaStr);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    idEvento,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
