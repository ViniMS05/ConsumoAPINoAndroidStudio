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

public class AlterarVeiculo extends AppCompatActivity {
    EditText edtIdVeiculo, edtPlaca, edtAno, edtMensalidade;
    Spinner spnProprietarios;
    Button btnAlterarVeiculo;
    AsyncHttpClient cliente;
    ArrayList<Proprietario> listaProprietarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_veiculo);

        Veiculo veiculo = (Veiculo) getIntent().getSerializableExtra("veiculo");

        edtIdVeiculo = findViewById(R.id.edtIdVeiculo);
        edtPlaca = findViewById(R.id.edtPlaca);
        edtAno = findViewById(R.id.edtAno);
        edtMensalidade = findViewById(R.id.edtMensalidade);
        spnProprietarios = findViewById(R.id.spnProprietarios);
        btnAlterarVeiculo = findViewById(R.id.btnAlterarVeiculo);
        cliente = new AsyncHttpClient();
        listaProprietarios = new ArrayList<>();

        assert veiculo != null;
        edtIdVeiculo.setText(String.valueOf(veiculo.getId()));
        edtPlaca.setText(veiculo.getPlaca());
        edtAno.setText(String.valueOf(veiculo.getAno()));
        edtMensalidade.setText(String.format("%.2f", veiculo.getMensalidade()));

        carregaProprietarios(veiculo.getFkProprietario());

        btnAlterarVeiculo.setOnClickListener(view -> {
            if (edtPlaca.getText().toString().isEmpty() ||
                    edtAno.getText().toString().isEmpty() ||
                    edtMensalidade.getText().toString().isEmpty() ||
                    spnProprietarios.getSelectedItem() == null) {
                Toast.makeText(AlterarVeiculo.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            } else {
                Veiculo obj = new Veiculo();
                obj.setId(Integer.parseInt(edtIdVeiculo.getText().toString()));
                obj.setPlaca(edtPlaca.getText().toString());
                obj.setAno(Integer.parseInt(edtAno.getText().toString()));
                obj.setMensalidade(Double.parseDouble(edtMensalidade.getText().toString()));

                Proprietario proprietarioSelecionado = listaProprietarios.get(spnProprietarios.getSelectedItemPosition());
                obj.setFkProprietario(proprietarioSelecionado.getId());

                alterarVeiculo(obj);
            }
        });
    }

    private void carregaProprietarios(int fkProprietarioAtual) {
        String url = "http://localhost:8081/proprietario";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        int proprietarioIndex = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Proprietario p = new Proprietario();
                            p.setId(jsonArray.getJSONObject(i).getInt("id_proprietario"));
                            p.setNome(jsonArray.getJSONObject(i).getString("nome"));
                            listaProprietarios.add(p);

                            if (p.getId() == fkProprietarioAtual) {
                                proprietarioIndex = i;
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AlterarVeiculo.this,
                                android.R.layout.simple_spinner_item,
                                getProprietarioNomes(listaProprietarios)
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnProprietarios.setAdapter(adapter);

                        spnProprietarios.setSelection(proprietarioIndex);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AlterarVeiculo.this, "Erro ao carregar proprietários: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void alterarVeiculo(Veiculo obj) {
        String url = "http://localhost:8081/veiculo/" + obj.getId();
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("placa", obj.getPlaca());
            parametros.put("ano", obj.getAno());
            parametros.put("mensalidade", obj.getMensalidade());
            parametros.put("fk_proprietario", obj.getFkProprietario());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(parametros.toString(), ContentType.APPLICATION_JSON);
        cliente.put(AlterarVeiculo.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Toast.makeText(AlterarVeiculo.this, "Veículo alterado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AlterarVeiculo.this, Lista_Veiculo.class);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AlterarVeiculo.this, "Erro ao alterar veículo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
