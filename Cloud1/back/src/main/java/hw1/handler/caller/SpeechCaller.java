package hw1.handler.caller;

import java.io.*;
import java.util.UUID;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;
import org.pmw.tinylog.Logger;

public class SpeechCaller implements ApiCaller {

    private static AmazonPolly client = null;

    private static void initClient() {
//        TODO: CREDENTIALSSSSS
        if (client == null) {
            client = AmazonPollyClientBuilder.standard().build();
        }
    }

    @Override
    public String getData(String... params) throws IOException {
        initClient();

        String id = getRandomId();
        String outputFileName = "../front/res/" + id + ".mp3";

//        System.out.println("synth");
        Logger.info("Initiating Synthesize Request...");
        long startTime = System.currentTimeMillis();

        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
                .withOutputFormat(OutputFormat.Mp3)
                .withVoiceId(VoiceId.Brian)
                .withText(params[0]);

        try (FileOutputStream outputStream = new FileOutputStream(new File(outputFileName))) {
            SynthesizeSpeechResult synthesizeSpeechResult = client.synthesizeSpeech(synthesizeSpeechRequest);
            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            try (InputStream in = synthesizeSpeechResult.getAudioStream()) {
                while ((readBytes = in.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readBytes);
                }
            }

            long latency = System.currentTimeMillis() - startTime;
            Logger.info("Request to AWSPolly terminated successfully: " + latency);
        } catch (Exception e) {
            Logger.error(e, "Request to AWSPolly failed: 500");
            throw e;
        }
        return id;
    }

    private String getRandomId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
