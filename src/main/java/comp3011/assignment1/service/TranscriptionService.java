package comp3011.assignment1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TranscriptionService {

    private final String apiKey;
    private final RestClient restClient;

    public TranscriptionService(
            @Value("${OPENAI_API_KEY:}") String apiKey) {

        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com")
                .build();
    }

    public String transcribe(MultipartFile audio) {

        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is not set");
        }

        MultipartBodyBuilder body = new MultipartBodyBuilder();

        body.part("file", audio.getResource()).contentType(MediaType.parseMediaType("audio/webm"));

        body.part("model", "gpt-4o-mini-transcribe");

        System.out.println("Sending audio to OpenAI");

        TranscriptionResponse response = restClient.post()
                .uri("/v1/audio/transcriptions")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body.build())
                .retrieve()
                .body(TranscriptionResponse.class);

        if (response == null || response.getText() == null) {
            throw new IllegalStateException(
                    "No transcription returned");
        }

        return response.getText();
    }


    public static class TranscriptionResponse {

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}