package com.jainantas.notes.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jainantas.notes.Fragments.NotesFragment;
import com.jainantas.notes.R;
import com.jainantas.notes.model.NotesDetails;

import java.util.List;

public class CustomNotesAdapter extends RecyclerView.Adapter<CustomNotesAdapter.MyViewHolder> {
    private List<NotesDetails> notesDetailsList;
    NotesFragment notesFragment;

    public CustomNotesAdapter(List<NotesDetails> notesDetails) {
        notesDetailsList = notesDetails;
        notesFragment = new NotesFragment();
    }



    @NonNull
    @Override
    public CustomNotesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomNotesAdapter.MyViewHolder holder, int position) {
        NotesDetails details = notesDetailsList.get(position);
        holder.mNote.setText(details.getNote());

    }

    @Override
    public int getItemCount() {
        return notesDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mNote;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mNote = (TextView) itemView.findViewById(R.id.noteHere);


        }
    }
}
