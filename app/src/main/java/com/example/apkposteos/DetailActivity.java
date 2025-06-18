package com.example.apkposteos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import java.io.FileOutputStream;

public class DetailActivity extends AppCompatActivity {

    private ListView detailListView;
    private Button saveButton;
    private boolean isSaved = false;

    private TextView postTitle, postBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailListView = findViewById(R.id.detailListView);
        saveButton = findViewById(R.id.saveButton);

        List<String> items = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            items.add("Item " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items
        );
        detailListView.setAdapter(adapter);

        postTitle = findViewById(R.id.postTitle);
        postBody = findViewById(R.id.postBody);

        RetrofitCliente.getInstance().getPostById(1).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();
                    postTitle.setText(post.getTitle());
                    postBody.setText(post.getBody());
                } else {
                    postTitle.setText("No se pudo obtener el post.");
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                postTitle.setText("Error al cargar.");
            }
        });

        // Botón con selector
        saveButton.setOnClickListener(v -> {
            if (!isSaved) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("¿Dónde quieres guardar?");
                builder.setItems(new CharSequence[]{"Preferencias", "Archivo"}, (dialog, which) -> {
                    if (which == 0) {
                        guardarEnPreferencias();
                    } else {
                        guardarEnArchivo();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(this, "Ya fue guardado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarEnPreferencias() {
        SharedPreferences prefs = getSharedPreferences("MisDatos", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("titulo", postTitle.getText().toString());
        editor.putString("contenido", postBody.getText().toString());
        editor.apply();

        saveButton.setText("Guardado");
        Toast.makeText(this, "Guardado en Preferencias", Toast.LENGTH_SHORT).show();
        isSaved = true;
    }

    private void guardarEnArchivo() {
        String texto = postTitle.getText().toString() + "\n\n" + postBody.getText().toString();
        try {
            FileOutputStream fos = openFileOutput("post_guardado.txt", MODE_PRIVATE);
            fos.write(texto.getBytes());
            fos.close();

            saveButton.setText("Guardado");
            Toast.makeText(this, "Guardado en Archivo", Toast.LENGTH_SHORT).show();
            isSaved = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar en archivo", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
