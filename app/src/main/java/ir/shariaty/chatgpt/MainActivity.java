package ir.shariaty.chatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ir.shariaty.chatgpt.databinding.ActivityMainBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    public static final MediaType JSON
            = MediaType.get("application/json;charset=utf-8");

    OkHttpClient clinet = new OkHttpClient();
    
    String imageUrl;
    String fileName = "code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.inputText.getText().toString();

                callAPI(text);
            }
        });

    }

    private void callAPI(String text) {

        progress(true);
        JSONObject object = new JSONObject();

        try {
            object.put("prompt" , text);
            object.put("size", "256x256");
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        RequestBody requestBody = RequestBody.create(object.toString(),JSON);
        Request request = new Request.Builder().url("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer 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")
                .post(requestBody)
                .build();

        clinet.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(MainActivity.this, "Failed To Generate Image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    imageUrl =  jsonObject.getJSONArray("data").getJSONObject(0).getString("url");

                    loadImage (imageUrl);
                    progress(false);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });









    }

    private void loadImage(String imageUrl) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide
                        .with(MainActivity.this)
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(binding.generatedimage);
            }
        });
















    }

    private void progress(boolean inProgress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (inProgress) {
                    binding.animationView.setVisibility(View.VISIBLE);
                    binding.cardView.setVisibility(View.GONE);
//                    binding.btnDownload.setVisibility(View.GONE);
                } else {
                    binding.animationView.setVisibility(View.GONE);
                    binding.cardView.setVisibility(View.VISIBLE);
//                    binding.btnDownload.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}