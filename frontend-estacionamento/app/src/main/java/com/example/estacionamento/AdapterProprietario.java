package com.example.estacionamento;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.estacionamento.model.Proprietario;

import java.util.ArrayList;

public class AdapterProprietario extends ArrayAdapter<Proprietario> {
    final int groupid;
    final ArrayList<Proprietario> lista;
    final Context context;
    public AdapterProprietario(Context context, int vg, int id, ArrayList<Proprietario> lista) {
        super(context, vg, id, lista);
        this.context = context;
        groupid = vg;
        this.lista = lista;
    }
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View itemView = inflater.inflate(groupid, parent, false);
        TextView textName = itemView.findViewById(R.id.id_proprietario);
        textName.setText(lista.get(position).getNome());
        return itemView;
    }
} 