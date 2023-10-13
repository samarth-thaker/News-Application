package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface {
    //1c50c16354ad4a7b9d1d525f232ef35e
    private RecyclerView newsRV,categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<CategoryRVModal>categoryRVModalArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private NewsRVAdapter newsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsRV = findViewById(R.id.idRVNews);
        categoryRV = findViewById(R.id.idRVCategories);
        loadingPB = findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        categoryRVModalArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList,this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModalArrayList, this, this::onCategoryClick);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);
        categoryRV.setAdapter(categoryRVAdapter);
        getCategories();
        getNews("All");
        newsRVAdapter.notifyDataSetChanged();

    }
    private void getCategories(){
        categoryRVModalArrayList.add(new CategoryRVModal("All", "https://unsplash.com/photos/Mwuod2cm8g4"));
        categoryRVModalArrayList.add(new CategoryRVModal("Technology", "https://unsplash.com/photos/XJXWbfSo2f0"));
        categoryRVModalArrayList.add(new CategoryRVModal("Science", "https://unsplash.com/photos/lQGJCMY5qcM"));
        categoryRVModalArrayList.add(new CategoryRVModal("Sports", "https://unsplash.com/photos/9HI8UJMSdZA"));
        categoryRVModalArrayList.add(new CategoryRVModal("General", "https://unsplash.com/photos/QK1OhZmopBo"));
        categoryRVModalArrayList.add(new CategoryRVModal("Business", "https://unsplash.com/photos/ZVprbBmT8QA"));
        categoryRVModalArrayList.add(new CategoryRVModal("Entertainment", "https://unsplash.com/photos/Qnlp3FCO2vc"));
        categoryRVModalArrayList.add(new CategoryRVModal("Health", "https://unsplash.com/photos/kcRFW-Hje8Y"));
        categoryRVAdapter.notifyDataSetChanged();

    }
    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String categoryURL = "https://newsapi.org/v2/top-headlines/sources?categories="+category+"&apiKey=1c50c16354ad4a7b9d1d525f232ef35e";
        String url = "https://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apiKey=1c50c16354ad4a7b9d1d525f232ef35e";
        String BASE_URL = "https://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        if(category.equals("All")){
            call = retrofitAPI.getAllNews(url);


        }else{
            call = retrofitAPI.getNewsByCategory(categoryURL);
        }
        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                    NewsModal newsModal = response.body();
                    loadingPB.setVisibility(View.GONE);
                    ArrayList<Articles>articles = newsModal.getArticles();
                    for(int i=0; i< articles.size(); i++){
                        articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(),
                                articles.get(i).getUrlToImage(),articles.get(i).getUrl(),
                                articles.get(i).getContent()
                                ));

                    }
                    newsRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail to get news", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCategoryClick(int position) {
            String category = categoryRVModalArrayList.get(position).getCategory();
            getNews(category);
    }
}