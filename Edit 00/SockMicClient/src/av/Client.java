package av;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by VarunKr on 22-04-2015.
 */
public class Client {

    public static int PORT = 3000;

    SourceDataLine _speaker;
    InputStream _streamIn;
    Socket _server;
    String _serverName = "127.0.0.1";
    boolean _running = true;

    public Client(String serverName) throws IOException,LineUnavailableException {
        this._serverName = serverName;
        init();
    }

    private void init() throws LineUnavailableException{
        //  specifying the audio format
        AudioFormat _format = new AudioFormat(8000.F,// Sample Rate
                16,     // Size of SampleBits
                1,      // Number of Channels
                true,   // Is Signed?
                false   // Is Big Endian?
        );

        //  creating the DataLine Info for the speaker format
        DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, _format);

        //  getting the mixer for the speaker
        _speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
        _speaker.open(_format);
    }

    public void Start() {
        try {
            System.out.println("Connecting to server @" + _serverName + ":" + PORT);

            //  creating the socket and connect to the server
            _server = new Socket(_serverName, PORT);
            System.out.println("Connected to: " + _server.getRemoteSocketAddress());

            //  gettting the server stream
            _streamIn = _server.getInputStream();

            _speaker.start();

            byte[] data = new byte[8000];
            System.out.println("Waiting for data...");
            while (_running) {

                //  checking if the data is available to speak
                if (_streamIn.available() <= 0)
                    continue;   //  data not available so continue back to start of loop

                //  count of the data bytes read
                int readCount= _streamIn.read(data, 0, data.length);

                if(readCount>0){
                    _speaker.write(data, 0, readCount);
                }
            }
            //honestly.... the control never reaches here.
            _speaker.drain();
            _speaker.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
