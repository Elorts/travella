package com.visitormaker.travella.service;

public class ChatGptService {
    import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

    @Service
    public class ChatGptService {

        @Value("${openai.api.key}")
        private String apiKey;

        private final OkHttpClient client = new OkHttpClient();

        public String getChatGptResponse(String userMessage) throws IOException {
            RequestBody body = RequestBody.create(
                    new JSONObject()
                            .put("model", "gpt-3.5-turbo")
                            .put("messages", new JSONObject[] {
                                    new JSONObject().put("role", "user").put("content", userMessage)
                            })
                            .toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                JSONObject jsonResponse = new JSONObject(response.body().string());
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        }
    }

}
