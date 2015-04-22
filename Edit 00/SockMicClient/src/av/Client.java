package av;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by VarunKr on 22-04-2015.
 */
public class Client {

    private IncomingSoundListener isl = new IncomingSoundListener();
    AudioFormat format = getAudioFormat();
    InputStream is;
    Socket client;
    String serverName = "127.0.0.1";
    int port=3000;
    boolean inVoice = true;

    public Client(String serverName) throws IOException {
        this.serverName = serverName;
        isl.runListener();
    }

    private AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }

    public void Start(){
        try{
            System.out.println("Connecting to server @" + serverName + ":" + port);
            client = new Socket(serverName, port);
            System.out.println("Connected to: " + client.getRemoteSocketAddress());
            System.out.println("Listening for incoming audio.");
            DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class,format);
            SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            speaker.open(format);
            byte[] data = new byte[8000];
            speaker.start();
            while(inVoice){
                is = client.getInputStream();
                if(is.available()<=0) continue;
                int dataLen = is.read(data,0,data.length);
                ByteArrayInputStream bais = new ByteArrayInputStream(data,0,dataLen);
                AudioInputStream ais = new AudioInputStream(bais,format,dataLen);
                int bytesRead = 0;
                if((bytesRead = ais.read(data)) != -1){
                    speaker.write(data,0,bytesRead);
                }
                ais.close();
                bais.close();
            }
            speaker.drain();
            speaker.close();
            System.out.println("Stopped listening to incoming audio.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
