package br.example.com.brapolar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import br.example.com.brapolar.Entities.EntityMedication;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Integer> listDataHeader;
    private HashMap<Integer, List<EntityMedication>> listHashMap;
    MySqliteHandler mySqliteHandler;
    BaseExpandableListAdapter adapter;

    public ExpandableListAdapter(Context context, List<Integer> listDataHeader, HashMap<Integer, List<EntityMedication>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        mySqliteHandler = new MySqliteHandler(context);
        this.adapter = adapter;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).get(i1); // i = Group Item , i1 = ChildItem
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        int h = Integer.parseInt(getGroup(i).toString()) / 60;
        int m = Integer.parseInt(getGroup(i).toString()) % 60;
        String H = String.valueOf(h);
        if (h < 10) {
            H = "0" + H;
        }
        String M = String.valueOf(m);
        if (m < 10) {
            M = "0" + M;
        }
        String headerTitle = H + ":" + M;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) view.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final EntityMedication child = (EntityMedication) getChild(i, i1);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }
        final int I = i;
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView unit = (TextView) view.findViewById(R.id.unit);
        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        name.setText(child.getName());
        unit.setText(child.toString());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntityMedication EM = (EntityMedication) child;
                EM.TakePild();
                if (EM.getQuantity() == 0) {
                    mySqliteHandler.UpdateMedication(child, 0);
                } else {
                    mySqliteHandler.UpdateMedication(child, 1);
                }

                List<EntityMedication> em = listHashMap.get(listDataHeader.get(I));
                em.remove(child);
                if (em.isEmpty()) {
                    listHashMap.remove(listDataHeader.get(I));
                    listDataHeader.remove(I);
                } else {
                    listHashMap.put(listDataHeader.get(I), em);
                }
                refresh();
                Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    /* public void removeChildFromGroup(int groupPosition, int childPosition) {
         Integer parent = mParent.get(groupPosition);

         parent.getArrayChildren().remove(childPosition);

         if (parent.getArrayChildren().isEmpty()) {
             removeGroup(groupPosition);
         } else {
             notifyDataSetChanged();
         }
     }*/
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
