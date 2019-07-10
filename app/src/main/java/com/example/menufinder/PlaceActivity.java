package com.example.menufinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.menufinder.MainActivity.apiKey;

public class PlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        getData();
    }

    public void getData()
    {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/details/" +
                "json?placeid=" + id + "&fields=name,formatted_address,photo" + "&key=" + apiKey;

        JsonObjectRequest placesRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getJSONObject("result").getString("name");
                            String address = response.getJSONObject("result").getString("formatted_address");
                            TextView nameText = findViewById(R.id.name2);
                            TextView addressTexr = findViewById(R.id.address2);
                            nameText.setText(name);
                            addressTexr.setText(address);

                        } catch (Exception e)
                        {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println(error.toString());
                    }
                });

        queue.add(placesRequest);


    }
}
