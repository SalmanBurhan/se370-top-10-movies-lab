package com.salmanburhan;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

public class TMDB {
    
    static String BASE_URL = "https://api.themoviedb.org/3";
    static String API_KEY = "TMDB_API_KEY";

    public static JSONArray popular() {

        String endpoint = "/movie/popular";
        String filters = "&language=en-US&region=US&page=1";

        HttpRequest request = HttpRequest
        .newBuilder()
        .uri(URI.create(
            String.format(
                "%s%s?api_key=%s&%s",
                BASE_URL,
                endpoint,
                API_KEY,
                filters
            )
        ))
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
        
        try {

            HttpResponse<String> response = HttpClient
            .newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            
            JSONArray jsonResponse = new JSONObject(
                response.body()
            ).getJSONArray("results");

            return jsonResponse;

        } catch (IOException e) {
            // Give Up, For Now, And Just Return A Blank Array.
            return new JSONArray();

        } catch (InterruptedException e) {
            // Give Up, For Now, And Just Return A Blank Array.
            return new JSONArray();

        }
    }
}
