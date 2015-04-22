package av;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

// for more details, refer to
// https://docs.oracle.com/javase/tutorial/sound/capturing.html

public class Radio {

    public static final int ERROR_LIMIT = 10;

    AudioFormat format;
    Server server;

    public Radio() throws IOException{
        try{
            // initializing the server
            server=new Server();
            //  starting the server
            server.Start();
            
            //  setting the mic audio format
            setAudioFormat();
            
            //  running the streaming of mic data
            Run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void setAudioFormat() {
        format = new AudioFormat(8000.F   ,// Sample Rate 
                                 16       ,// Size of SampleBits
                                 1        ,// Number of Channels
                                 true     ,// Is Signed?
                                 false     // Is Big Endian?
                                );
    }

    public void Run(){
        boolean running=true;
        try{
            TargetDataLine mic = AudioSystem.getTargetDataLine(format);
            mic.open(format);
            System.out.println("Mic open.");
            byte tmpBuff[] = new byte[(int)(format.getSampleRate() * 0.4)];
            mic.start();
            while(running) {
                int count = mic.read(tmpBuff,0,tmpBuff.length);
                if (count > 0){
                    for(int i=0;i<server.Clients.size();i++){
                        Client cl=server.Clients.get(i);
                        try{
                            cl.Send(tmpBuff,0,count);
                            cl.ErrorCount=0;
                        }
                        catch(Exception ex){
                            cl.ErrorCount++;
                            System.out.println(cl.RemoteAddress.toString()+" @ Send Error#"+cl.ErrorCount);
                            if(cl.ErrorCount>ERROR_LIMIT){
                                System.out.println("\tError limit exceeded.");
                                System.out.println("\tRemoving client "+cl.RemoteAddress.toString());
                                server.Clients.remove(i);
                                i-=1;
                            }
                        }
                    }
                }
            }
            //  drain() causes the mixer's remaining data to get delivered to the target data line's buffer
            mic.drain();
            mic.close();
        }catch(Exception e){
            System.out.println("Error!!!");
            e.printStackTrace();
        }
    }


    public static void main (String args[]) throws IOException{
        new Radio();
    }
    
}
