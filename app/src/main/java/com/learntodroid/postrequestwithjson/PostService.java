package com.learntodroid.postrequestwithjson;

import org.w3c.dom.Comment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostService {
    @POST("api/values/")
    Call<Comment> sendPost(@Body Post post);
}
