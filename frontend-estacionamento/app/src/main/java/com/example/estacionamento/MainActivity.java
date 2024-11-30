package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.estacionamento.model.Proprietario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
    EditText edtnome, edtcpf;
    Button btncad, btnListar, btnAddVeiculo, btnListar2;
    AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtnome= findViewById(R.id.edtnome);
        edtcpf= findViewById(R.id.edtcpf);
        cliente = new AsyncHttpClient();
        btncad= findViewById(R.id.button1);
        btncad.setOnClickListener(view -> {

            if(edtnome.getText().toString().isEmpty()||edtcpf.getText().toString().isEmpty()){
                Toast.makeText(MainActivity.this,"Existes campos em brancos!", Toast.LENGTH_SHORT).show();
            }
            else{
                Proprietario obj = new Proprietario();
                obj.setNome(edtnome.getText().toString());
                obj.setCpf(edtcpf.getText().toString());
                cadastrarProprietario(obj);
            }
        });
        btnListar = findViewById(R.id.button2);

        btnListar.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, Lista_Proprietario.class);
            startActivity(i);
        });
        btnAddVeiculo = findViewById(R.id.button3);

        btnAddVeiculo.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, AdicionarVeiculo.class);
            startActivity(i);
        });

        btnListar2 = findViewById(R.id.button4);

        btnListar2.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, Lista_Veiculo.class);
            startActivity(i);
        });
    }
    public void cadastrarProprietario(Proprietario obj){
        String url;
        url = "http://localhost:8081/proprietario";
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nome", obj.getNome());
            parametros.put("cpf", obj.getCpf());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(parametros.toString(),
                ContentType.APPLICATION_JSON);
        cliente.post(MainActivity.this, url, entity,
                "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == 200){
                            Toast.makeText(MainActivity.this, "Proprietário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            edtnome.setText(null);
                            edtcpf.setText(null);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody,Throwable error) {
                        Toast.makeText(MainActivity.this,
                                "Erro ao cadstrar proprietário: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}