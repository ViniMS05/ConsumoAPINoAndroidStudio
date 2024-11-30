package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.estacionamento.model.Veiculo;
import com.example.estacionamento.model.Proprietario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Lista_Veiculo extends AppCompatActivity {
    Activity context;
    ListView lsveiculos;
    AsyncHttpClient cliente;
    ArrayList<Veiculo> listaVeiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_veiculo);

        context = Lista_Veiculo.this;
        lsveiculos = findViewById(R.id.lvVeiculos);
        cliente = new AsyncHttpClient();
        carregaVeiculos();
    }
    public void carregaVeiculos() {
        String urlProprietarios = "http://localhost:8081/proprietario";

        cliente.get(urlProprietarios, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    ArrayList<Proprietario> listaProprietarios = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Proprietario p = new Proprietario();
                            p.setId(jsonArray.getJSONObject(i).getInt("id_proprietario"));
                            p.setNome(jsonArray.getJSONObject(i).getString("nome"));
                            listaProprietarios.add(p);
                        }
                        carregaListaVeiculos(listaProprietarios);
                    } catch (JSONException erro) {
                        Log.d("erro", "Erro ao parsear JSON de proprietários: " + erro);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Lista_Veiculo.this, "Erro ao carregar proprietários: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregaListaVeiculos(ArrayList<Proprietario> listaProprietarios) {
        String url = "http://localhost:8081/veiculo";

        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listaVeiculos = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Veiculo v = new Veiculo();
                            v.setId(jsonArray.getJSONObject(i).getInt("id_veiculo"));
                            v.setPlaca(jsonArray.getJSONObject(i).getString("placa"));
                            v.setAno(jsonArray.getJSONObject(i).getInt("ano"));
                            v.setMensalidade(jsonArray.getJSONObject(i).getDouble("mensalidade"));
                            v.setFkProprietario(jsonArray.getJSONObject(i).getInt("fk_proprietario"));
                            listaVeiculos.add(v);
                        }
                        AdapterVeiculo adapter = new AdapterVeiculo(context, R.layout.item_veiculo, R.id.id_placa, listaVeiculos, listaProprietarios);
                        lsveiculos.setAdapter(adapter);

                        configuraItemClickListener();
                    } catch (JSONException erro) {
                        Log.d("erro", "Erro ao parsear JSON de veículos: " + erro);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Lista_Veiculo.this, "Erro ao carregar veículos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configuraItemClickListener() {
        lsveiculos.setOnItemClickListener((adapterView, view, i, l) -> {
            final Veiculo v = listaVeiculos.get(i);
            String b = "ID do Veículo: " + v.getId() + "\n" +
                    "Placa: " + v.getPlaca() + "\n" +
                    "Ano: " + v.getAno() + "\n" +
                    "Mensalidade: R$ " + String.format("%.2f", v.getMensalidade()) + "\n";

            AlertDialog.Builder a = new AlertDialog.Builder(Lista_Veiculo.this);
            a.setCancelable(true);
            a.setTitle("Detalhes do Veículo");
            a.setMessage(b);
            a.setIcon(R.drawable.ic_launcher_background);

            a.setNegativeButton("Editar", (dialogInterface, i1) -> {
                Intent i2 = new Intent(Lista_Veiculo.this, AlterarVeiculo.class);
                i2.putExtra("veiculo", v);
                startActivity(i2);
            });
            a.show();
        });
    }
}
