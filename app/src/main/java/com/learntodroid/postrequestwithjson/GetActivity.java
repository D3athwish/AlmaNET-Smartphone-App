package com.learntodroid.postrequestwithjson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetActivity extends AppCompatActivity
{
    private Button postButton;
    private Button getButton;

    private String TAG = GetActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);

        postButton = findViewById(R.id.postButton);
        getButton = findViewById(R.id.getButton);

        getButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                contactList = new ArrayList<>();

                lv = (ListView) findViewById(R.id.list);

                new GetContacts().execute();
            }
        });

        //Swap back to PostActivity
        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openPostActivity();
            }
        });
    }

    private void openPostActivity() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }

    private class GetContacts extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(GetActivity.this, "JSON se prenaša...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            //Making a request to url and getting a response

            String url = "https://almanetapi.azurewebsites.net/api/values/1";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if(jsonStr != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    //Getting JSON Array node
                    //JSONArray contacts = jsonObj.getJSONArray(null);

                    //Looping through all contacts

                    for(int i = 0; i < 1; i++){
                        JSONObject c = jsonObj;
                        String id = c.getString("ID");
                        String naprava = c.getString("Naprava");

                        //Phone node is JSON object

                        JSONObject gps = c.getJSONObject("gps");
                        String longitude = gps.getString("longitude");
                        String latitude = gps.getString("latitude");

                        // Temporary hashmap for single contact

                        HashMap<String, String> contact = new HashMap<>();

                        // Adding each child node to HashMap key => value

                        contact.put("id", id);
                        contact.put("naprava", naprava);
                        contact.put("longitude", longitude);
                        contact.put("latitude", latitude);

                        // adding contact to contact list

                        contactList.add(contact);
                    }
                } catch (final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "JSON parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else{
                Log.e(TAG, "Couldn't get JSON from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get JSON from the server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(GetActivity.this, contactList,
                    R.layout.list_item, new String[] {"id", "naprava", "longitude", "latitude"},
                    new int[]{R.id.id, R.id.naprava, R.id.longitude, R.id.latitude});
            lv.setAdapter(adapter);
        }
    }

}