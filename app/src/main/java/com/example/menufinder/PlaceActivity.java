package com.example.menufinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.menufinder.MainActivity.apiKey;

public class PlaceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        recyclerView = findViewById(R.id.imageList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
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
                            JSONArray arr = response.getJSONObject("result").getJSONArray("photos");
                            List<String> lst = new ArrayList<>();
                            for(int i=0; i<arr.length(); i++)
                            {
                                JSONObject o = arr.getJSONObject(i);
                                lst.add(o.getString("photo_reference"));
                            }
                            adapter = new ImageAdapter(lst);
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







    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

        private List<String> images;
        public ImageAdapter(List<String> images)
        {
            this.images = images;
        }
        @NonNull
        @Override
        public ImageAdapter.ImageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_image, viewGroup, false);
            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ImageHolder myViewHolder, final int i) {
            try {
                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=700&photoreference=" +
                        this.images.get(i)+ "&key=" + apiKey;System.out.println(url);

                Picasso.get().load(url).into(myViewHolder.image);
            } catch (Exception e)
            {
                System.out.println(e);
            }
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ImageHolder extends RecyclerView.ViewHolder {
            private ImageView image;
            public ImageHolder(@NonNull View itemView) {
                super(itemView);
                this.image = itemView.findViewById(R.id.imageView);
            }
        }
    }
}
