package com.example.menufinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String apiKey = "AIzaSyBPrlmxlXsYd48uQbJUd5IofiJBdoxr-Xo";
    private static List<String> placeIds;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.placesList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void search(View view){
        EditText name = findViewById(R.id.nameInput);
        EditText location = findViewById(R.id.locationInput);
        if(!name.getText().toString().trim().equals("") && !location.getText().toString().trim().equals(""))
        {
            getPlaces(name.getText().toString().trim(), location.getText().toString().trim());
        }
        else
        {
            Toast.makeText(this,"Please enter place name and location.",Toast.LENGTH_SHORT).show();
        }
    }

    private void getPlaces(String name, String location) {
        placeIds = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/" +
                "json?query=" + name + " in " + location + "&key=" + apiKey;

        JsonObjectRequest placesRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("results");
                            for(int i=0; i<arr.length(); i++)
                            {
                                placeIds.add(arr.getJSONObject(i).getString("place_id"));
                            }
                            adapter = new PlacesAdapter(arr);
                            recyclerView.setAdapter(adapter);
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

    public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceHolder> {

        private JSONArray places;
        public PlacesAdapter(JSONArray places)
        {
            this.places = places;
        }
        @NonNull
        @Override
        public PlacesAdapter.PlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_place, viewGroup, false);
            return new PlaceHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlacesAdapter.PlaceHolder myViewHolder, final int i) {
            try {
                String name = this.places.getJSONObject(i).getString("name");
                String address = this.places.getJSONObject(i).getString("formatted_address");
                myViewHolder.name.setText(name);
                myViewHolder.address.setText(address);
                myViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PlaceActivity.class);
                        intent.putExtra("id", placeIds.get(i));System.out.println(placeIds.get(i));
                        startActivity(intent);
                    }
                });
            } catch (Exception e)
            {
                System.out.println(e);
            }
        }

        @Override
        public int getItemCount() {
            return places.length();
        }

        public class PlaceHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private TextView address;
            private ConstraintLayout layout;
            public PlaceHolder(@NonNull View itemView) {
                super(itemView);
                this.name = itemView.findViewById(R.id.name);
                this.address = itemView.findViewById(R.id.address);
                this.layout = itemView.findViewById(R.id.holderPlaces);
            }
        }
    }
}
