package av;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;

public class Radio {


    TargetDataLine _mic;
    Server _server;
    boolean _running = true;
    private int count;

    public Radio() throws IOException, LineUnavailableException {
        try {
            // initializing the server
            _server = new Server();
            //  starting the server
            _server.Start();

            //  initializing Mic
            initMic();

            //  running the streaming of mic data
            Run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException, LineUnavailableException {
        new Radio();


    private void initMic() throws LineUnavailableException {
        //  specifying the audio format
        AudioFormat _format = new AudioFormat(8000.F,// Sample Rate
                16,     // Size of SampleBits
                1,      // Number of Channels
                true,   // Is Signed?
                false   // Is Big Endian?
        );

        //  getting the source line i.e, mic
        _mic = AudioSystem.getTargetDataLine(_format);

        //  causes the line to acquire any required system resources and become operational
        _mic.open(_format);
    }

    public void Run() {
        try {
            System.out.println("Mic open.");
            byte _buffer[] = new byte[(int) (_mic.getFormat().getSampleRate() * 0.4)];
            _mic.start();
            while (_running) {
                // returns the length of data copied in buffer
                int count = _mic.read(_buffer, 0, _buffer.length);

                //if data is available
                if (count > 0) {
                    _server.SendToAll(_buffer, 0, count);
                }
            }
            //  honestly.... program never reaches here
            //  drain() causes the mixer's remaining data to get delivered to the target data line's buffer
            _mic.drain();
            _mic.close();
        } catch (Exception e) {
            System.out.println("Error!!!");
            e.printStackTrace();
        }
    }   }

}