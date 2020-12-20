package com.learntodroid.postrequestwithjson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class PostRepository {
    private static PostRepository instance;

    private PostService commentsService;

    public static PostRepository getInstance() {
        if (instance == null) {
            instance = new PostRepository();
        }
        return instance;
    }

    public PostRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://almanetapi.azurewebsites.net/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        commentsService = retrofit.create(PostService.class);
    }

    public PostService getCommentsService() {
        return commentsService;
    }
}
