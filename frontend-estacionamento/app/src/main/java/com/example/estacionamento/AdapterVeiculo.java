package com.example.estacionamento;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.estacionamento.model.Veiculo;
import com.example.estacionamento.model.Proprietario;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterVeiculo extends ArrayAdapter<Veiculo> {
    final int groupid;
    final ArrayList<Veiculo> listaVeiculos;
    final HashMap<Integer, String> proprietariosMap;
    final Context context;
    public AdapterVeiculo(Context context, int vg, int id, ArrayList<Veiculo> listaVeiculos, ArrayList<Proprietario> listaProprietarios) {
        super(context, vg, id, listaVeiculos);
        this.context = context;
        this.groupid = vg;
        this.listaVeiculos = listaVeiculos;

        proprietariosMap = new HashMap<>();
        for (Proprietario p : listaProprietarios) {
            proprietariosMap.put(p.getId(), p.getNome());
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View itemView = inflater.inflate(groupid, parent, false);

        TextView textPlaca = itemView.findViewById(R.id.id_placa);
        TextView textAno = itemView.findViewById(R.id.id_ano);
        TextView textMensalidade = itemView.findViewById(R.id.id_mensalidade);
        TextView textProprietario = itemView.findViewById(R.id.id_proprietario_nome);

        Veiculo veiculo = listaVeiculos.get(position);
        textPlaca.setText(veiculo.getPlaca());
        textAno.setText(String.valueOf(veiculo.getAno()));
        textMensalidade.setText(String.format("R$ %.2f", veiculo.getMensalidade()));

        String nomeProprietario = proprietariosMap.get(veiculo.getFkProprietario());
        if (nomeProprietario != null) {
            textProprietario.setText(nomeProprietario);
        } else {
            textProprietario.setText(R.string.propriet_rio_n_o_encontrado);
        }

        return itemView;
    }
}
