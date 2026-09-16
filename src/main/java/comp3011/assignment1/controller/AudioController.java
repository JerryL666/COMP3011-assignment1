package comp3011.assignment1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.service.TranscriptionService;

@RestController
public class AudioController {

    private final TranscriptionService transcriptionService;

    public AudioController(
            TranscriptionService transcriptionService) {

        this.transcriptionService = transcriptionService;
    }

    @PostMapping("/api/transcribe")
    public ResponseEntity<String> receiveAudio(
            @RequestParam("audio") MultipartFile audio) {
    	

        if (audio.isEmpty()) {
            return ResponseEntity.badRequest().body("No audio received");
        }

        System.out.println(
                "Audio received. Size: " + audio.getSize());

        try {

            String transcription = transcriptionService.transcribe(audio);

            return ResponseEntity.ok(transcription);

        } catch (Exception error) {

            System.out.println(
                    "Transcription failed: "
                    + error.getMessage());

            return ResponseEntity.internalServerError().body("Transcription failed.");
        }
    }
}