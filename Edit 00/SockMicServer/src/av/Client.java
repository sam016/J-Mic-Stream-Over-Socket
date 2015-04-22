package av;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by VarunKr on 22-04-2015.
 */
public class Client {

    private Socket _sock;
    private DataOutputStream _out ;

    public SocketAddress RemoteAddress;
    public int ErrorCount=0;

    public Client(Socket sock) throws IOException{
        this._sock=sock;
        _out= new DataOutputStream(sock.getOutputStream());
        RemoteAddress=sock.getRemoteSocketAddress();
    }

    public void Send(byte[] buffer, int offset, int count) throws IOException{
        _out.write(buffer, offset, count);
    }

}
