package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.FacilityAdapter;
import hr.unipu.duda.justintime.model.Facility;

public class FacilityListActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Facility> facilities;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);
        setTitle("Ustanove");

        recyclerView = (RecyclerView) findViewById(R.id.facilityRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        facilities = new ArrayList<>();

        progressDialog = new ProgressDialog(FacilityListActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();

        //Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://justin-time.herokuapp.com/facility/read-all";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               for(int i=0; i<response.length();i++) {
                   try {
                       JSONObject facilityObject = response.getJSONObject(i);
                       Facility facility = new Facility();
                       facility.setId(facilityObject.getString("id"));
                       facility.setName(facilityObject.getString("name"));

                       facilities.add(facility);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse", "onErrorResponse: " + error.getMessage());
                if(progressDialog.isShowing()) progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(FacilityListActivity.this);
                builder.setMessage("Neuspješan dohvat podataka, molim pokušajte ponovno!")
                        .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                recreate();
                            }
                        })
                        .create().show();
            }
        });

        queue.add(jsObjRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                adapter = new FacilityAdapter(FacilityListActivity.this, facilities);
                recyclerView.setAdapter(adapter);
            }
        });


    }
}
