package com.example.srikkanth.chat.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.srikkanth.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private EditText emailEditText,passwordEditText,nameEditText;
    private RelativeLayout emailLayout,passwordLayout,nameLayout;
    private Button registerButton,loginUserButton,registerUserButton;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private String email,password,name;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        emailEditText=view.findViewById(R.id.email);
        nameEditText=view.findViewById(R.id.display_name_edit_text);
        passwordEditText=view.findViewById(R.id.password);
        registerButton=view.findViewById(R.id.register);
        loginUserButton=view.findViewById(R.id.login_user);
        registerUserButton=view.findViewById(R.id.register_user);
        emailLayout=view.findViewById(R.id.email_row);
        passwordLayout=view.findViewById(R.id.password_row);
        nameLayout=view.findViewById(R.id.display_name_row);
        mAuth=FirebaseAuth.getInstance();
        listeners();
        return view;
    }
    private void listeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLayout.setVisibility(View.VISIBLE);
                passwordLayout.setVisibility(View.VISIBLE);
                nameLayout.setVisibility(View.VISIBLE);
                registerUserButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.GONE);
                loginUserButton.setVisibility(View.GONE);
            }
        });
        loginUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailEditText.getText().toString();
                password=passwordEditText.getText().toString();
               if(validateForm("login")){
                   signInUser();
               }

            }
        });
        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailEditText.getText().toString();
                password=passwordEditText.getText().toString();
                name=nameEditText.getText().toString();
               if(validateForm("Register")){
                   createUser();
               }
            }
        });
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                user.updateProfile(profileUpdates);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rel_layout,new ChatFragment()).addToBackStack(null).commit();
                            }
                        } else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(),"User Already Exists",Toast.LENGTH_LONG).show();
                            Log.w(TAG,task.getException());
                        }else {
                            Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    private void signInUser() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rel_layout,new ChatFragment()).addToBackStack(null).commit();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
    private boolean validateForm(String type) {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
           passwordEditText.setError(null);
        }
        if(type.equals("Register")){
            String name=nameEditText.getText().toString();
            if(TextUtils.isEmpty(name)){
                nameEditText.setError("Required");
                valid=false;
            }
            else {
                nameEditText.setError(null);
            }
        }

        return valid;
    }

}
