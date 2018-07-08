package com.example.srikkanth.chat;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.srikkanth.chat.Fragments.ChatFragment;
import com.example.srikkanth.chat.Fragments.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private RelativeLayout relativeLayout;
    private Fragment chatFragment,loginFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        loginFragment=LoginFragment.newInstance();
        chatFragment=ChatFragment.newInstance();
        relativeLayout=findViewById(R.id.rel_layout);
        if(!isInternetOn()){
            showSnack(false);
        }
        else{
            Log.e("Hi","hi");
                    Log.e("Hi","1");
                    FirebaseUser user=mAuth.getCurrentUser();
                    Log.e("Hi","2");
                    if(user!=null){
                        Log.e("Signed in","3");
                        getSupportFragmentManager().beginTransaction().replace(R.id.rel_layout,chatFragment).commit();
                    }
                    else {
                        Log.e("SignedOut","4");
                        LoginFragment fragment = new LoginFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.rel_layout,loginFragment).commit();
                        //startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(providers).setTheme(R.style.LoginTheme).setLogo(R.drawable.logo).build(),RC_SIGN_IN);
                    }

        }
    }
    private void showSnack(boolean isConnected) {
        String message="";
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

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
    protected void onResume() {
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==R.id.sign_out_menu) {
            FirebaseAuth.getInstance().signOut();
            LoginFragment fragment = new LoginFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.rel_layout, fragment);
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
