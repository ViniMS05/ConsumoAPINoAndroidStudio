package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.estacionamento.model.Veiculo;
import com.example.estacionamento.model.Proprietario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AdicionarVeiculo extends AppCompatActivity {
    EditText edtPlaca, edtAno, edtMensalidade;
    Spinner spnProprietarios;
    Button btnCadastrarVeiculo, btnListar;
    AsyncHttpClient cliente;
    ArrayList<Proprietario> listaProprietarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_veiculo);

        edtPlaca = findViewById(R.id.edtPlaca);
        edtAno = findViewById(R.id.edtAno);
        edtMensalidade = findViewById(R.id.edtMensalidade);
        spnProprietarios = findViewById(R.id.spnProprietarios);
        btnCadastrarVeiculo = findViewById(R.id.button1);
        btnListar = findViewById(R.id.button2);

        cliente = new AsyncHttpClient();
        listaProprietarios = new ArrayList<>();

        carregaProprietarios();

        btnListar = findViewById(R.id.button2);

        btnListar.setOnClickListener(view -> {
            Intent i = new Intent(AdicionarVeiculo.this, Lista_Veiculo.class);
            startActivity(i);
        });

        btnCadastrarVeiculo.setOnClickListener(v -> {
            if (edtPlaca.getText().toString().isEmpty() ||
                    edtAno.getText().toString().isEmpty() ||
                    edtMensalidade.getText().toString().isEmpty() ||
                    spnProprietarios.getSelectedItem() == null) {
                Toast.makeText(AdicionarVeiculo.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            } else {
                Veiculo veiculo = new Veiculo();
                veiculo.setPlaca(edtPlaca.getText().toString());
                veiculo.setAno(Integer.parseInt(edtAno.getText().toString()));
                veiculo.setMensalidade(Double.parseDouble(edtMensalidade.getText().toString()));

                Proprietario proprietarioSelecionado = listaProprietarios.get(spnProprietarios.getSelectedItemPosition());
                veiculo.setFkProprietario(proprietarioSelecionado.getId());

                cadastrarVeiculo(veiculo);
            }
        });
    }
    private void carregaProprietarios() {
        String url = "http://localhost:8081/proprietario";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Proprietario p = new Proprietario();
                            p.setId(jsonArray.getJSONObject(i).getInt("id_proprietario"));
                            p.setNome(jsonArray.getJSONObject(i).getString("nome"));
                            listaProprietarios.add(p);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AdicionarVeiculo.this,
                                android.R.layout.simple_spinner_item,
                                getProprietarioNomes(listaProprietarios)
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnProprietarios.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AdicionarVeiculo.this, "Erro ao carregar proprietários: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<String> getProprietarioNomes(ArrayList<Proprietario> lista) {
        ArrayList<String> nomes = new ArrayList<>();
        for (Proprietario p : lista) {
            nomes.add(p.getNome());
        }
        return nomes;
    }

    private void cadastrarVeiculo(Veiculo veiculo) {
        String url = "http://localhost:8081/veiculo";
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("placa", veiculo.getPlaca());
            parametros.put("ano", veiculo.getAno());
            parametros.put("mensalidade", veiculo.getMensalidade());
            parametros.put("fk_proprietario", veiculo.getFkProprietario());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(parametros.toString(), ContentType.APPLICATION_JSON);
        cliente.post(AdicionarVeiculo.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Toast.makeText(AdicionarVeiculo.this, "Veículo cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    edtPlaca.setText(null);
                    edtAno.setText(null);
                    edtMensalidade.setText(null);
                    spnProprietarios.setSelection(0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AdicionarVeiculo.this, "Erro ao cadastrar veículo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
