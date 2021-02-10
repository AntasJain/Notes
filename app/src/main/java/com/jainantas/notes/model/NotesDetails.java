package com.jainantas.notes.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jainantas.notes.Adapter.CustomNotesAdapter;

public class NotesDetails {
    private String note;
    private String key;


    public NotesDetails() {

    }

//    @Override
//    public boolean equals(@Nullable Object obj) {
//        if(obj instanceof NotesDetails){
//            NotesDetails temp=(NotesDetails) obj;
//            if(this.key.equals(temp.key))
//                return true;
//        }
//        return false;
//    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
    public NotesDetails(String note, String key){
        this.key=key;
        this.note=note;
    }
    public String getNote(){
        return  note;
    }
    public void setNote(String note){
        this.note=note;
    }
    public String getKey(){
        return  key;
    }
    public void setKey(String key){
        this.key=key;
    }
}
