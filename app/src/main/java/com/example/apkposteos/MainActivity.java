package com.example.apkposteos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        RetrofitCliente.getInstance().getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Post post : response.body()) {
                        titles.add(post.getTitle());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            MainActivity.this,
                            R.layout.list_item,
                            R.id.titleTextView,
                            titles
                    );
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

