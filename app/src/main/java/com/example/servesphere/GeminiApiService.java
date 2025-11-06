package com.example.servesphere;

import com.example.servesphere.GeminiApiModels.GenerateContentRequest;
import com.example.servesphere.GeminiApiModels.GenerateContentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeminiApiService {

    // POST https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key=API_KEY
    @POST("v1beta/models/{model}:generateContent")
    Call<GenerateContentResponse> generateContent(
            @Path("model") String model,
            @Query("key") String apiKey,
            @Body GenerateContentRequest request
    );
}
