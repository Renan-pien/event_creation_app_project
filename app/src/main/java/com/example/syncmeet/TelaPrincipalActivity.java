package com.example.syncmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class TelaPrincipalActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private TextView navFooterSair;
    private Button botaoCriarEvento;
    private Button botaoEditarEvento;
    private Button botaoExcluirEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_principal);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);
        navFooterSair = findViewById(R.id.nav_footer_sair);
        botaoCriarEvento = findViewById(R.id.botao_criar_evento);
        botaoEditarEvento = findViewById(R.id.botao_edit_evento);
        botaoExcluirEvento = findViewById(R.id.botao_exclui_evento);

        // --- Início da atualização do Header ---

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_header_username);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);

        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String email = intent.getStringExtra("EMAIL");

        if (username != null && !username.isEmpty()) {
            navUsername.setText(username);
        }
        if (email != null && !email.isEmpty()) {
            navEmail.setText(email);
        }

        // --- Fim da atualização do Header ---

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_eventos_agendados) {
                Intent agendaIntent = new Intent(TelaPrincipalActivity.this, AgendaEventoActivity.class);
                startActivity(agendaIntent);
            } else if (id == R.id.nav_historico) {
                Intent historicoIntent = new Intent(TelaPrincipalActivity.this, HistoricoEventoActivity.class);
                startActivity(historicoIntent);
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // --- Botão de sair no Drawer ---
        navFooterSair.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(TelaPrincipalActivity.this, MainActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
        });

        // --- Abrir a tela de Criar Evento ---
        botaoCriarEvento.setOnClickListener(v -> {
            Intent criarEventoIntent = new Intent(TelaPrincipalActivity.this, CriarEventoActivity.class);
            startActivity(criarEventoIntent);
        });

        // --- Abrir a tela de Editar Evento ---
        botaoEditarEvento.setOnClickListener(v -> {
            Intent editarEventoIntent = new Intent(TelaPrincipalActivity.this, EditarEventoActivity.class);
            startActivity(editarEventoIntent);
        });

        // --- Abrir a tela de Excluir Evento ---
        botaoExcluirEvento.setOnClickListener(v -> {
            Intent excluirEventoIntent = new Intent(TelaPrincipalActivity.this, ExcluirEventoActivity.class);
            startActivity(excluirEventoIntent);
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
