package com.example.estacionamento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.estacionamento.model.Proprietario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Lista_Proprietario extends AppCompatActivity {
    Activity context;
    ListView lsproprietarios;
    AsyncHttpClient cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_proprietario);

        context = Lista_Proprietario.this;
        lsproprietarios = findViewById(R.id.lvProprietarios);
        cliente = new AsyncHttpClient();
        carregaProprietarios();
    }
    public void carregaProprietarios() {
        String url = "http://localhost:8081/proprietario";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode,cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listarTodosProprietarios(new String(responseBody));
                }else {
                    Toast.makeText(Lista_Proprietario.this, "Falha ao carregar proprietários. Código de status: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(Lista_Proprietario.this, "Erro ao carregar proprietários: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void listarTodosProprietarios(String resposta){
        final ArrayList<Proprietario> lista = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(resposta);
            for(int i = 0; i < jsonArray.length(); i++){
                Proprietario p = new Proprietario();

                p.setId(jsonArray.getJSONObject(i).getInt("id_proprietario"));

                p.setNome(jsonArray.getJSONObject(i).getString("nome"));

                p.setCpf(jsonArray.getJSONObject(i).getString("cpf"));
                lista.add(p);
            }
            AdapterProprietario adapter = new AdapterProprietario(context, R.layout.adapter, R.id.id_proprietario,lista);
            lsproprietarios.setAdapter(adapter);
        }
        catch (JSONException erro){
            Log.d("erro", "erro" + erro);
        }
        lsproprietarios.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Proprietario p = lista.get(i);
            String url = "http://localhost:8081/proprietario/"+p.getId();
            cliente.delete(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    if(statusCode == 200){
                        Toast.makeText(Lista_Proprietario.this,"Proprietário excluído com sucesso", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(2000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        carregaProprietarios();
                    }
                }
                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                    Toast.makeText(Lista_Proprietario.this,"Erro ao excluir proprietário: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        });

        lsproprietarios.setOnItemClickListener((adapterView, view, i, l) -> {
            final Proprietario p = lista.get(i);
            String b = "id_proprietario: " + p.getId() + "\n" +
                    "nome: " + p.getNome() + "\n" +
                    "cpf: " + p.getCpf() + "\n";
            AlertDialog.Builder a = new
                    AlertDialog.Builder(Lista_Proprietario.this);
            a.setCancelable(true);
            a.setTitle("Detalhes do proprietário");
            a.setMessage(b);
            a.setIcon(R.drawable.ic_launcher_background);

            a.setNegativeButton("Editar", (dialogInterface, i1) -> {
                Intent i2 = new Intent(Lista_Proprietario.this, Alterar.class);
                i2.putExtra("proprietario",p);
                startActivity(i2);
            });
            a.show();
        });
    }
}