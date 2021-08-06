package sg.edu.rp.webservices.firebasetransportify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Incident> alIncident;
    CustomAdapter adapter;
    AsyncHttpClient client;
    FirebaseFirestore db;
    CollectionReference colRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv);
        alIncident = new ArrayList<>();
        adapter = new CustomAdapter(this, R.layout.row, alIncident);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("incidents");

        client = new AsyncHttpClient();
        client.addHeader("AccountKey", "4hMXtXzZQR+I8UTuxxO/qg==");
        client.get("http://datamall2.mytransport.sg/ltaodataservice/TrafficIncidents", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject jsonObject = null;
                alIncident.clear();
                try {
                    JSONArray jsonArray = response.getJSONArray("value");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        String type = jsonObject.getString("Type");
                        String message = jsonObject.getString("Message");
                        Date date = new SimpleDateFormat("(dd/MM)HH:mm").parse(message.split(" ")[0]);

                        Incident inc = new Incident(type, message, date);
                        alIncident.add(inc);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("JSONException: ", e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.upload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Upload to Firestore");
            builder.setMessage("Proceed to upload to Firestore?");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference documentReference = colRef.document(document.getId());
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Delete", "Successful");
                                    }
                                });
                            }
                            for (Incident incident : alIncident) {
                                colRef.add(incident)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("Add", "Successful");
                                    }
                                });
                            }
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (id == R.id.reload) {
            client.get("http://datamall2.mytransport.sg/ltaodataservice/TrafficIncidents", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONObject jsonObject = null;
                    alIncident.clear();
                    try {
                        JSONArray jsonArray = response.getJSONArray("value");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = (JSONObject) jsonArray.get(i);
                            String type = jsonObject.getString("Type");
                            String message = jsonObject.getString("Message");
                            Date date = new SimpleDateFormat("(dd/MM)HH:mm").parse(message.split(" ")[0]);

                            Incident inc = new Incident(type, message, date);
                            alIncident.add(inc);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("JSONException: ", e);
                    }
                }
            });
            Toast.makeText(MainActivity.this, "Data has been reloaded", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}