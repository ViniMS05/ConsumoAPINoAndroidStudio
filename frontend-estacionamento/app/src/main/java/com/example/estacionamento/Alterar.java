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

public class Alterar extends AppCompatActivity {
    EditText edtcodigo, edtnome, edtcpf;
    Button btnalterar;
    AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar);

        Intent intent = getIntent();
        Proprietario p = (Proprietario) intent.getSerializableExtra("proprietario");
        edtcodigo = findViewById(R.id.edtid);
        edtnome= findViewById(R.id.edtnome2);
        edtcpf= findViewById(R.id.edtcpf2);
        btnalterar = findViewById(R.id.button);
        cliente = new AsyncHttpClient();
        assert p != null;
        edtcodigo.setText(String.valueOf(p.getId()));
        edtnome.setText(String.valueOf(p.getNome()));
        edtcpf.setText(String.valueOf(p.getCpf()));
        btnalterar.setOnClickListener(view -> {
            if(edtnome.getText().toString().isEmpty() || edtcpf.getText().toString().isEmpty()){
                Toast.makeText(Alterar.this, "Existes campos em brancos!", Toast.LENGTH_SHORT).show();
            }
            else{
                Proprietario obj = new Proprietario();

                obj.setId(Integer.parseInt(edtcodigo.getText().toString()));
                obj.setNome(edtnome.getText().toString());
                obj.setCpf(edtcpf.getText().toString());
                alterarProprietario(obj);
            }
        });
    }
    public void alterarProprietario(Proprietario obj){
        String url;
        url = "http://localhost:8081/proprietario/"+obj.getId();
        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nome", obj.getNome());
            parametros.put("cpf", obj.getCpf());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(parametros.toString(), ContentType.APPLICATION_JSON);
        cliente.put(Alterar.this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,byte[] responseBody) {
                if(statusCode == 200){
                    Toast.makeText(Alterar.this, "Proprietário alterado com sucesso!",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Alterar.this, Lista_Proprietario.class);
                    startActivity(i);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Alterar.this,"Erro ao alterar proprietário: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}