let mediaRecorder;
let audioChunks = [];
let audioBlob;

const startButton = document.getElementById("startButton");
const stopButton = document.getElementById("stopButton");
const statusText = document.getElementById("status");
const transcriptionText = document.getElementById("transcription");

stopButton.disabled = true;


// AI Assistance: ChatGPT was used to help understand the browser microphone APIs (getUserMedia and MediaRecorder) and to structure the recording logic.
// The implementation was reviewed and tested for this project.
async function startRecording() {

    try {
       
        // Ask the user for microphone permission
        const stream = await navigator.mediaDevices.getUserMedia({
            audio: true
        });

        // Clear any audio from the previous recording
        audioChunks = [];

        // Create a recorder using the microphone stream
        mediaRecorder = new MediaRecorder(stream);

        // Save audio data while recording
        mediaRecorder.ondataavailable = function(event) {
            audioChunks.push(event.data);
        };

        // Run this code when recording stops
        mediaRecorder.onstop = function() {

            // Combine the recorded audio pieces into one audio file
            audioBlob = new Blob(audioChunks, {
                type: mediaRecorder.mimeType
            });

            console.log("Audio recorded");
            console.log("Audio size:", audioBlob.size);

            // Turn off the microphone
            stream.getTracks().forEach(function(track) {
                track.stop();
            });

            statusText.textContent = "Recording complete";
            transcriptionText.textContent =
                "Audio recorded successfully.";

            startButton.disabled = false;
            stopButton.disabled = true;
        };

        // Start recording
        mediaRecorder.start();

        statusText.textContent = "Recording...";
        transcriptionText.textContent = "";

        startButton.disabled = true;
        stopButton.disabled = false;

    } 
	catch (error) {

        console.log("Microphone error:", error);
        statusText.textContent =
            "Unable to access microphone.";

        startButton.disabled = false;
        stopButton.disabled = true;
    }
}


// Stop recording
function stopRecording() {

    if (mediaRecorder) {
        mediaRecorder.stop();
        statusText.textContent = "Processing recording...";
    }
}


// Connect buttons to functions
startButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);








