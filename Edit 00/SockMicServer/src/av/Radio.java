package av;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class Radio {

    public static final int ERROR_LIMIT = 10;

    boolean outVoice = true;
    AudioFormat format = getAudioFormat();
    Server server;


    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }

    public Radio() throws IOException{
        try{
            server=new Server();
            server.Start();
            Run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void Run(){
        try{
            TargetDataLine mic = AudioSystem.getTargetDataLine(format);
            mic.open(format);
            System.out.println("Mic open.");
            byte tmpBuff[] = new byte[mic.getBufferSize()/5];
            mic.start();
            while(outVoice) {
                //System.out.println("Reading from mic.");
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
            mic.drain();
            mic.close();
            System.out.println("Stopped listening from mic.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void main (String args[]) throws IOException{
        new Radio();

    }


}