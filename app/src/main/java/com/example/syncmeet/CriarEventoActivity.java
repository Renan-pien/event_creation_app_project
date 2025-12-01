package com.example.syncmeet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CriarEventoActivity extends AppCompatActivity {

    private EditText nomeEventoEditText;
    private EditText localEventoEditText;
    private EditText dataEventoEditText;
    private EditText horaEventoEditText;
    private ImageView backArrow;

    private Calendar dataSelecionada = Calendar.getInstance();
    private Calendar horaSelecionada = Calendar.getInstance();

    private static final String URL = "http://10.0.2.2/syncmeet/create_event.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_evento);

        nomeEventoEditText = findViewById(R.id.nome_evento);
        localEventoEditText = findViewById(R.id.local_evento);
        dataEventoEditText = findViewById(R.id.data_evento);
        horaEventoEditText = findViewById(R.id.hora_evento);
        backArrow = findViewById(R.id.back_arrow);

        backArrow.setOnClickListener(v -> finish());

        // Seleção da DATA
        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, day) -> {
            dataSelecionada.set(Calendar.YEAR, year);
            dataSelecionada.set(Calendar.MONTH, month);
            dataSelecionada.set(Calendar.DAY_OF_MONTH, day);

            // Zera hora ao trocar data
            dataSelecionada.set(Calendar.HOUR_OF_DAY, 0);
            dataSelecionada.set(Calendar.MINUTE, 0);
            dataSelecionada.set(Calendar.SECOND, 0);
            dataSelecionada.set(Calendar.MILLISECOND, 0);

            atualizarData();
        };

        dataEventoEditText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    dateListener,
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Seleção da HORA
        TimePickerDialog.OnTimeSetListener timeListener = (view, hour, minute) -> {
            horaSelecionada.set(Calendar.HOUR_OF_DAY, hour);
            horaSelecionada.set(Calendar.MINUTE, minute);

            atualizarHora();
        };

        horaEventoEditText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(
                    this,
                    timeListener,
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    true
            ).show();
        });

        findViewById(R.id.criar_evento_button).setOnClickListener(v -> validarEEnviar());
    }

    private void atualizarData() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dataEventoEditText.setText(sdf.format(dataSelecionada.getTime()));
    }

    private void atualizarHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        horaEventoEditText.setText(sdf.format(horaSelecionada.getTime()));
    }

    // ✔ VALIDAÇÃO 100% CORRETA
    private void validarEEnviar() {
        String nome = nomeEventoEditText.getText().toString().trim();
        String local = localEventoEditText.getText().toString().trim();
        String data = dataEventoEditText.getText().toString();
        String hora = horaEventoEditText.getText().toString();

        if (nome.isEmpty() || local.isEmpty() || data.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Junta a data e a hora em 1 só Calendar
            Calendar evento = Calendar.getInstance();
            evento.set(
                    dataSelecionada.get(Calendar.YEAR),
                    dataSelecionada.get(Calendar.MONTH),
                    dataSelecionada.get(Calendar.DAY_OF_MONTH),
                    horaSelecionada.get(Calendar.HOUR_OF_DAY),
                    horaSelecionada.get(Calendar.MINUTE),
                    0
            );

            Calendar agora = Calendar.getInstance();

            if (evento.getTimeInMillis() <= agora.getTimeInMillis()) {
                Toast.makeText(this, "Escolha uma data/hora no FUTURO!", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            Toast.makeText(this, "Data ou hora inválida!", Toast.LENGTH_SHORT).show();
            return;
        }

        enviarEvento(nome, local, data, hora);
    }

    private void enviarEvento(String nome, String local, String data, String hora) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.optBoolean("success");
                        String message = obj.optString("message");

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        if (success) finish();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Erro ao processar resposta do servidor",
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario_id", "1");
                params.put("nome", nome);
                params.put("local", local);
                params.put("data", data);
                params.put("hora", hora);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
