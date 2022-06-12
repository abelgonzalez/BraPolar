package br.example.com.brapolar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.example.com.brapolar.Entities.EntityMedication;

public class AdapterMedication extends BaseAdapter {
    public static LayoutInflater inflater = null;
    ArrayList<EntityMedication> lista;
    Context context;
    MySqliteHandler mySqliteHandler;
    AdapterMedication adapter;
    EntityMedication item;

    public AdapterMedication(ArrayList<EntityMedication> lista, Context context) {
        this.lista = lista;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mySqliteHandler = new MySqliteHandler(context);
        this.adapter = this;

    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public EntityMedication getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getId();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.item_medication, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView unit = (TextView) view.findViewById(R.id.unit);
//        ImageView edit = (ImageView)view.findViewById(R.id.edit);
        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        final EntityMedication item = lista.get(i);
        name.setText(lista.get(i).getName());
        unit.setText(lista.get(i).toString());
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mySqliteHandler.UpdateMedication(item,1);
//                Toast.makeText(context,"editar",Toast.LENGTH_LONG).show();
//
//            }
//        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySqliteHandler.UpdateMedication(item, 0);
                lista.remove(item);
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "cancel", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
}
