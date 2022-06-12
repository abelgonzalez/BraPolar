package br.example.com.brapolar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.R;

public class SMSAdapter {
}

/*public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.SMSViewHolder> {


    private Context mCtx;
    private List<EntitySms> artistList;

    public SMSAdapter(Context mCtx, List<EntitySms> artistList) {
        this.mCtx = mCtx;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public SMSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_artists, parent, false);
        return new SMSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SMSViewHolder holder, int position) {
        EntitySms artist = artistList.get(position);
        holder.textViewName.setText(artist.name);
        holder.textViewGenre.setText("Genre: " + artist.genre);
        holder.textViewAge.setText("Age: " + artist.age);
        holder.textViewCountry.setText("Country: " + artist.country);
    }

    class SMSViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewGenre, textViewAge, textViewCountry;

        public SMSViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewGenre = itemView.findViewById(R.id.text_view_genre);
            textViewAge = itemView.findViewById(R.id.text_view_age);
            textViewCountry = itemView.findViewById(R.id.text_view_country);
        }
    }

}
        */