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
	private final StatisticsService statisticsService;

	public TranscriptionService(@Value("${OPENAI_API_KEY:}") String apiKey, StatisticsService statisticsService) {

		this.apiKey = apiKey;
		this.statisticsService = statisticsService;

		this.restClient = RestClient.builder().baseUrl("https://api.openai.com").build();
	}
	public static class Usage {

	    private long input_tokens;
	    private long output_tokens;

	    public long getInputTokens() {
	        return input_tokens;
	    }

	    public void setInput_tokens(long input_tokens) {
	        this.input_tokens = input_tokens;
	    }

	    public long getOutputTokens() {
	        return output_tokens;
	    }

	    public void setOutput_tokens(long output_tokens) {
	        this.output_tokens = output_tokens;
	    }
	}

	public String transcribe(MultipartFile audio) {

		if (apiKey.isBlank()) {
			throw new IllegalStateException("OPENAI_API_KEY is not set");
		}

		MultipartBodyBuilder body = new MultipartBodyBuilder();

		body.part("file", audio.getResource()).contentType(MediaType.parseMediaType("audio/webm"));

		body.part("model", "gpt-4o-mini-transcribe");

		System.out.println("Sending audio to OpenAI");

		TranscriptionResponse response = restClient.post().uri("/v1/audio/transcriptions")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).contentType(MediaType.MULTIPART_FORM_DATA)
				.body(body.build()).retrieve().body(TranscriptionResponse.class);

		if (response == null || response.getText() == null) {
			throw new IllegalStateException("No transcription returned");
		}

		if (response.getUsage() != null) {

			statisticsService.addTokens(response.getUsage().getInputTokens(), response.getUsage().getOutputTokens());
		}

		return response.getText();
	}

	public static class TranscriptionResponse {

		private String text;
		private Usage usage;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Usage getUsage() {
			return usage;
		}

		public void setUsage(Usage usage) {
			this.usage = usage;
		}
	}
}