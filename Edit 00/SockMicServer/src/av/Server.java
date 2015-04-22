package av;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by VarunKr on 22-04-2015.
 */
public class Server {

    public ArrayList<Client> Clients;

    private ServerSocket _serverSocket;
    private Thread _thread;

    private boolean _running=false;

    public Server()  throws IOException {
        System.out.println("Creating Socket...");
        _serverSocket = new ServerSocket(3000);
        Clients= new ArrayList<Client>();
    }

    public void Start(){
        Stop();
        _running=true;
        _thread = new Thread(_runnable);
        _thread.start();
    }

    public void Stop(){
        if(_thread !=null){
            try{
                _running=false;
                _thread.interrupt();
            }
            catch(Exception ex){}
            _thread =null;
        }
    }


    private Runnable _runnable= new Runnable() {
        @Override
        public void run() {
            while(_running){
                System.out.println("Waiting for client...");
                try{
                    Socket sock = _serverSocket.accept();
                    System.out.println("\tav.Client accepted > " + sock.getLocalPort());

                    Client cl= new Client(sock);
                    Clients.add(cl);
                }
                catch(Exception ex){
                    System.out.println("\tError in accepting the client!!!");
                }
            }
        }
    };
}
