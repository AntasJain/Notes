package com.jainantas.notes.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.jainantas.notes.Fragments.LoginFragment;
import com.jainantas.notes.Fragments.NotesFragment;
import com.jainantas.notes.Preferences.SharedPrefsHelper;
import com.jainantas.notes.R;

public class MainActivity extends AppCompatActivity {
LoginFragment loginFragment;
NotesFragment notesFragment;
FrameLayout frameLayout;
GoogleSignInClient mClient;
    private static final String userName="USERNAME";
    private static final String userID="USERID";
    private static final String userEmail="USER_EMAIL";
//    @Override
//    protected void onStart() {
//        super.onStart();
//        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginFragment=new LoginFragment();
        notesFragment=new NotesFragment();
        frameLayout=findViewById(R.id.main_frame);

        //mClient= GoogleSignIn.getClient(this,gso);
        SharedPrefsHelper.init(this);
       FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
       if(SharedPrefsHelper.getId(userID,null)==null)
       transaction.replace(R.id.main_frame,loginFragment).commit();
       else
           transaction.replace(R.id.main_frame,notesFragment).commit();
        //transaction.commit();

    }

}