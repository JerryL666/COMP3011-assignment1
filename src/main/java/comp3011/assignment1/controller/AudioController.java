package comp3011.assignment1.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AudioController {

    @PostMapping("/api/transcribe")
    public String receiveAudio(@RequestParam("audio") MultipartFile audio) {

        if (audio.isEmpty()) {
            return "No audio received";
        }

        System.out.println("Audio received");
        System.out.println("Audio size: " + audio.getSize());

        return "Audio received successfully";
    }
}