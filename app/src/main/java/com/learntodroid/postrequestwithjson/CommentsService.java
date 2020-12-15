package com.learntodroid.postrequestwithjson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CommentsService {
    @POST("api/values/")
    Call<Comment> createComment(@Body Comment comment);

    @FormUrlEncoded
    @POST("api/values/")
    Call<Comment> createComment(@Field("author") String author);

    @FormUrlEncoded
    @POST("api/values/")
    Call<Comment> createComment(@FieldMap Map<String, String> fields);
}
