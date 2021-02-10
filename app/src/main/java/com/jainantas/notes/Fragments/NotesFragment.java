package com.jainantas.notes.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jainantas.notes.Activities.MainActivity;
import com.jainantas.notes.Adapter.CustomNotesAdapter;
import com.jainantas.notes.Preferences.SharedPrefsHelper;
import com.jainantas.notes.R;
import com.jainantas.notes.model.NotesDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView recyclerView;
    private CustomNotesAdapter customNotesAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String mUserName;
    private static final String userName = "USERNAME";
    private static final String userID = "USERID";
    private static final String userEmail = "USER_EMAIL";
    private static final String dontShowAgain = "SHOW_AGAIN";
    private static final String counter = "COUNTER";
    private FloatingActionButton addNote;
    private String childName;
    private ChildEventListener mChildEventListener;
    private LinearLayoutManager layoutManager;
    TextView textView,nameVIew;
    ProgressBar progressBar;
    List<NotesDetails> notesDetail;
    List<String> keys;
    String key;
    GoogleSignInClient mClient;
    GoogleSignInAccount account;

    public NotesFragment() {
        // Required empty public constructor
    }


    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        mClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);
        SharedPrefsHelper.init(getContext());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        childName = SharedPrefsHelper.getId(userID, null);
        mDatabaseReference = mFirebaseDatabase.getReference().child(childName);
        keys = new ArrayList<>();
        SharedPrefsHelper.init(getContext());
        setHasOptionsMenu(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                signOut();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.item_menu, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        if (!SharedPrefsHelper.isShow(dontShowAgain, false)) {
            AlertDialog.Builder warn = new AlertDialog.Builder(getContext());
            warn.setTitle("Info");
            warn.setMessage("Please Note the following things:\n\n1- Swipe the note right to edit.\n2- Swipe the note left to delete.\n3- Notes get saved on cloud when internet is available.");
            warn.setNegativeButton("Don't Show this again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPrefsHelper.dontShowAgain(dontShowAgain, true);
                    dialog.dismiss();
                }
            });
            warn.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog show = warn.create();
            show.setCancelable(false);
            show.show();
        }
        nameVIew=rootView.findViewById(R.id.name);
        nameVIew.setText("Hey There, "+SharedPrefsHelper.getUser(userName,"Anonymous")+".");
        addNote = rootView.findViewById(R.id.addNote);
        textView = rootView.findViewById(R.id.noItem);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        notesDetail = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressBar);
        if (SharedPrefsHelper.getCount(counter, 0) != 0) {
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        //customNotesAdapter=new CustomNotesAdapter();
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final EditText note = new EditText(getContext());
                builder.setMessage("Type to add Note");
                builder.setTitle("New Note");
                builder.setView(note);
                note.setHeight(600);
                note.setGravity(Gravity.BOTTOM);
                LinearLayout linearLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(50, 0, 50, 0);
                linearLayout.addView(note, params);
                linearLayout.setGravity(Gravity.BOTTOM);
                builder.setView(linearLayout);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotesDetails notesDetails = new NotesDetails();
                        notesDetails.setNote(note.getText().toString());
                        key = mDatabaseReference.push().getKey();
                        notesDetails.setKey(key);
                        //keys.add(notesDetail.get(notesDetail.size()-1).getKey());
                        mDatabaseReference.child(key).setValue(notesDetails);
                        Toast.makeText(getContext(), "Note Added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Note not saved", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                //alertDialog.getWindow().setLayout(400,1000);
                alertDialog.setCancelable(false);
                alertDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.height = 1200;
                alertDialog.getWindow().setAttributes(lp);
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        });
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NotesDetails details = snapshot.getValue(NotesDetails.class);
                notesDetail.add(details);
                String n = snapshot.getKey();
                //  keys.removeAll();
                keys.add(n);
                customNotesAdapter = new CustomNotesAdapter(notesDetail);
                recyclerView.setAdapter(customNotesAdapter);
                customNotesAdapter.notifyDataSetChanged();
                SharedPrefsHelper.setCount(counter, keys.size());
                textView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NotesDetails details = snapshot.getValue(NotesDetails.class);
                String key1 = snapshot.getKey();
                notesDetail.set(keys.indexOf(key1), details);
                customNotesAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                customNotesAdapter.notifyDataSetChanged();
                if (customNotesAdapter.getItemCount() == 0) {
                    textView.setVisibility(View.VISIBLE);
                    SharedPrefsHelper.setCount(counter, keys.size());
                    progressBar.setVisibility(View.GONE);
                }
                // if(customNotesAdapter.getItemCount()==0)
                // textView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);


        return rootView;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.parseColor("#ff5555"))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightActionIcon(R.drawable.ic_outline_edit_24)
                    .addSwipeRightBackgroundColor(Color.parseColor("#5555ff")).create().decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    AlertDialog.Builder buildeDelete = new AlertDialog.Builder(getContext());
                    buildeDelete.setTitle("Delete Note?");

                    buildeDelete.setMessage("Are you Sure you want to delete the note?");
                    buildeDelete.setIcon(R.drawable.ic_baseline_delete_24);
                    buildeDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabaseReference.child(keys.get(pos)).removeValue();
                            notesDetail.remove(notesDetail.get(pos));
                            keys.remove(pos);
                            customNotesAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    buildeDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            customNotesAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDiag = buildeDelete.create();
                    alertDiag.setCancelable(false);
                    alertDiag.show();

                    break;
                case ItemTouchHelper.RIGHT:
                    String useKey = keys.get(pos);
                    //Log.e("KKKKKKKK",key1);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(R.drawable.ic_outline_edit_24);
                    builder.setTitle("Edit Note");
                    EditText editText = new EditText(getContext());
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(50, 50, 50, 50);
                    editText.setLayoutParams(layoutParams);
                    editText.setText(notesDetail.get(pos).getNote());
                    // builder.setView(editText);
                    linearLayout.addView(editText, layoutParams);
                    builder.setView(linearLayout);
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NotesDetails details = new NotesDetails();
                            details.setNote(editText.getText().toString());

                            try {
                                mDatabaseReference.child(useKey).setValue(details);
                            } catch (Exception e) {
                                Log.e("Error", e.toString());
                                Log.e("nkey", useKey);
                            }

                            //notesDetail.set(pos,details);
                            Toast.makeText(getContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.height = 800;
                    dialog.getWindow().setAttributes(lp);
                    customNotesAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void signOut() {
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(getContext());
        logoutDialog.setTitle("Logout");
        logoutDialog.setMessage("Are you Sure You Want to Logout?\n\nNote: You can re-login to access your data  anytime.");
        logoutDialog.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        SharedPrefsHelper.delUsage(account.getId(), account.getEmail(), account.getDisplayName());
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_frame, new LoginFragment());
                        Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        //transaction.remove(getFragmentManager().);
                        transaction.commit();
                    }
                });
            }
        });
        logoutDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        AlertDialog logoutdialog = logoutDialog.create();
        logoutdialog.setCancelable(false);
        logoutdialog.show();


    }
}

