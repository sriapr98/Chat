package com.example.srikkanth.chat.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srikkanth.chat.HelperAdapter.MessageAdapter;
import com.example.srikkanth.chat.HelperClass.ChatMessage;
import com.example.srikkanth.chat.MainActivity;
import com.example.srikkanth.chat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private String mUsername="", mPhotoUrl, mMessage;
    private ChildEventListener mChildEventListener;
    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private RelativeLayout relativeLayout;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private ImageView photoImageView;
    private ImageButton mSendButton;
    private ArrayList<ChatMessage> chatMessages;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        initialize(view);
        listeners();
        return view;
    }

    private void listeners() {
        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                Log.e("send","button");
                if (isInternetOn()) {
                    ChatMessage chatMessage = new ChatMessage(mMessageEditText.getText().toString(), null, mAuth.getCurrentUser().getDisplayName());
                    databaseReference.push().setValue(chatMessage);
                    // Clear input box
                    mMessageEditText.setText("");
                }

            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e("Hi", "1");
                FirebaseUser user = mAuth.getCurrentUser();
                Log.e("Hi", "2");
                if (user != null) {
                    Log.e("Signed in", "3");
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    Log.e("SignedOut", "4");
                    onSignedOutCleanup();
                    //startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(providers).setTheme(R.style.LoginTheme).setLogo(R.drawable.logo).build(), RC_SIGN_IN);
                }
            }
        };
        mMessageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                alertDialogBuilder.setMessage("Are you sure,You wanted delete this message?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                mMessageAdapter.remove(mMessageAdapter.getItem(position));
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "No", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialogBuilder.show();
                return true;
            }
        });

    }
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }
    private void initialize(View view) {
        mUsername = ANONYMOUS;
        // Initialize references to views
        relativeLayout=view.findViewById(R.id.item_rel_layout);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mMessageListView = (ListView)view.findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton)view.findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText)view.findViewById(R.id.messageEditText);
        mSendButton = (ImageButton)view.findViewById(R.id.sendButton);
        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        // Initialize message ListView and its adapter
        chatMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(getActivity(), R.layout.item_message,chatMessages);
        mMessageListView.setAdapter(mMessageAdapter);
        //providers= Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.GoogleBuilder().build());
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("messages");
        mAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("chatPhotos");
    }
    private void attachDatabaseReadListener(){
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.hasChildren()){
                        ChatMessage chatMessage=dataSnapshot.getValue(ChatMessage.class);
                        mMessageAdapter.add(chatMessage);
                    }
                    else {
                        ChatMessage chatMessage=new ChatMessage();
                        String message=dataSnapshot.getValue(String.class);
                        Log.e("message", message);
                        chatMessage.setMessage(message);
                        chatMessage.setPhotoUrl(null);
                        chatMessage.setUsername("Admin");
                        mMessageAdapter.add(chatMessage);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            databaseReference.addChildEventListener(mChildEventListener);
        }
    }
    private void detachDatabaseReadListener() {
        if(mChildEventListener!=null) {
            databaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }
    private void onSignedInInitialize(String displayName) {
        mUsername=displayName;
        attachDatabaseReadListener();
    }
    private void onSignedOutCleanup(){
        mUsername=ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rel_layout, new LoginFragment()).addToBackStack(null).commit();
    }
    private void showSnack(boolean isConnected) {
        String message="";
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar
                    .make(getView().findViewById(R.id.rel_layout), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            showSnack(true);
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            showSnack(false);
            return false;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(getActivity(),"Signed In",Toast.LENGTH_LONG).show();
            }
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getActivity(),"Cancelled Sign in",Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode==RC_PHOTO_PICKER && resultCode==RESULT_OK){
            Uri selectedImageUri=data.getData();
            final StorageReference photoRef=storageReference.child(selectedImageUri.getLastPathSegment());
            uploadTask=photoRef.putFile(selectedImageUri);
            Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        ChatMessage chatMessage=new ChatMessage(null,downloadUri.toString(),mUsername);
                        databaseReference.push().setValue(chatMessage);
                    } else {
                        Log.e("completed","failed");
                    }
                }
            });
        }
    }
}