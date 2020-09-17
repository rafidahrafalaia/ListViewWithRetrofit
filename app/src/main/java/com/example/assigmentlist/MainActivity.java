package com.example.assigmentlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrinterId;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assigmentlist.Entity.Contact;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.androidhive.info/";
    private List<Contact> listContact = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressBar myProgressBar = findViewById(R.id.myProgressBar);
        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        MyApiService myApiService = RetrofitClientInstance.getRetrofitInstance().create(MyApiService.class);
        Call<JsonObject> call = myApiService.getContacts();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                myProgressBar.setVisibility(View.GONE);
                try {
                    JsonArray jsonArray = response.body().get("contacts").getAsJsonArray();


//                    System.out.println(jsonContext);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Contact ct = new Contact();
                        JsonObject dataObject = jsonArray.get(i).getAsJsonObject();
                        String dataNama = dataObject.get("name").getAsString();
                        String dataEmail = dataObject.get("email").getAsString();
                        String dataPhone = dataObject.get("phone").getAsJsonObject().get("mobile").getAsString();
//                        String dataPhone1 = dataObject.get("phone").getAsJsonObject().get("mobile").getAsString();
                        ct.setName(dataNama);
                        ct.setEmail(dataEmail);
                        ct.setPhone(dataPhone);
                        listContact.add(ct);
                    }


//                    JSONArray jsonArray = JSONArray(jsonContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                populateListView(listContact);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                myProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    interface MyApiService {
        @GET("contacts")
        Call<JsonObject> getContacts();
    }

    static class RetrofitClientInstance {
        private static Retrofit retrofit;

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    class ListViewAdapter extends BaseAdapter {
        private List<Contact> contacts;
        private Context context;

        public ListViewAdapter(Context context, List<Contact> contacts) {
            this.context = context;
            this.contacts = contacts;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int pos) {
            return contacts.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.model, viewGroup, false);
            }

            TextView name = view.findViewById(R.id.textName);
            TextView email = view.findViewById(R.id.textEmail);
            TextView number = view.findViewById(R.id.textNumber);

            final Contact thisContact = contacts.get(position);

            name.setText(thisContact.getName());
            email.setText(thisContact.getEmail());
            number.setText(thisContact.getPhone());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] contacts = {
                            thisContact.getName(),
                            thisContact.getEmail(),
                            thisContact.getPhone()
                    };
                    openDetailActivity(contacts);
                }
            });

            return view;
        }

        private void openDetailActivity(String[] data) {
            Intent nextDetail = new Intent(MainActivity.this, DetailActivity.class);
            nextDetail.putExtra("NAME", data[0]);
            nextDetail.putExtra("EMAIL", data[1]);
            nextDetail.putExtra("PHONE", data[2]);
            startActivity(nextDetail);
        }
    }

    private ListViewAdapter adapter;
    private ListView mListView;
    ProgressBar myProgressBar;

    private void populateListView(List<Contact> contacts) {
        mListView = findViewById(R.id.mListView);
        adapter = new ListViewAdapter(this, contacts);
        mListView.setAdapter(adapter);
    }


}