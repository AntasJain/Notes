package com.jainantas.notes.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.jainantas.notes.Preferences.SharedPrefsHelper;
import com.jainantas.notes.R;

public class LoginFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String userName="USERNAME";
    private static final String userID="USERID";
    private static final String userEmail="USER_EMAIL";
    private static final int RC_SIGN_IN=100;


    private String mParam1;
    private String mParam2;
    private GoogleSignInClient mClient;
    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getContext());
        updatePreferences(account);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        GoogleSignInOptions options=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mClient= GoogleSignIn.getClient(getContext(),options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_login,container,false);
      //  Toast.makeText(getContext(),"Hi! from LoginFragment", Toast.LENGTH_LONG).show();
        SignInButton button=rootView.findViewById(R.id.sign_in_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        return  rootView;
    }
    public void updatePreferences(GoogleSignInAccount account)
    {
        SharedPrefsHelper.init(getContext());
        if(account!=null) {
            SharedPrefsHelper.putUser(userName, account.getDisplayName());
            SharedPrefsHelper.putEmail(userEmail, account.getEmail());
            SharedPrefsHelper.putId(userID, account.getId());
            Toast.makeText(getContext(), "Logged In", Toast.LENGTH_LONG).show();
            FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame,new NotesFragment());
            transaction.remove(this);
            transaction.commit();
        }
        //else Toast.makeText(getContext(),"Not Logged In",Toast.LENGTH_LONG).show();
    }
    private void signIn(){
        Intent signInIntent=mClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account=completedTask.getResult(ApiException.class);
            updatePreferences(account);
        }
        catch (ApiException e){
            Toast.makeText(getContext(),e.getStatusCode(),Toast.LENGTH_LONG).show();
            updatePreferences(null);
        }

    }
}