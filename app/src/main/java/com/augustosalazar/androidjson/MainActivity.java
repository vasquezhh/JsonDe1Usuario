package com.augustosalazar.androidjson;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;
    //private static String url = "http://api.androidhive.info/contacts/";
    private static String url = "http://api.randomuser.me/?results=1&format=jsaon";
    JSONArray usuarios = null;
    ArrayList<DataEntry> listaUsuarios;
    private ListView listView;
    private Firebase myFirebaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaUsuarios = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        // Create a new Adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
                Log.d("List","Edit index "+index);
                //callback.onFragment1EditClick((DataEntry)view.getTag());
                Intent i = new Intent(MainActivity.this,DetailActivity.class);
                i.putExtra("data",(DataEntry)view.getTag());
                startActivity(i);
            }
        });

        Firebase.setAndroidContext(this);
        new Firebase("https://vasquezhjson.firebaseio.com/")
                .addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add((String)dataSnapshot.child("text").getValue());
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.remove((String)dataSnapshot.child("text").getValue());
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isNetworkAvaible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvaible = true;
            Toast.makeText(this, "Network is available ", Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(this, "Network not available ", Toast.LENGTH_LONG)
                    .show();
        }
        return isNetworkAvaible;
    }


    public void requestData(View view) {

        new GetData().execute();


    }

    public void checkInternet(View view) {
        isNetworkAvailable();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    usuarios = jsonObj.getJSONArray("results");
                    Log.d("Response length: ", "> " + usuarios.length());

                    for (int i = 0; i < usuarios.length(); i++) {
                        JSONObject c = usuarios.getJSONObject(i);

                        DataEntry dataEntry = new DataEntry();

                        dataEntry.setGender(c.getString("gender"));

                        JSONObject name = c.getJSONObject("name");

                        dataEntry.setFistName(name.getString("first"));
                        dataEntry.setLastName(name.getString("last"));

                        JSONObject imageObject = c.getJSONObject("picture");

                        dataEntry.setPicture(imageObject.getString("large"));

                        listaUsuarios.add(dataEntry);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            CustomAdapter adapter = new CustomAdapter(MainActivity.this, listaUsuarios);
            listView.setAdapter(adapter);
        }

    }

}
