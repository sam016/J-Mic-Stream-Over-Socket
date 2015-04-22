package av;

import java.io.IOException;

/**
 * Created by VarunKr on 22-04-2015.
 */
public class Receiver {

    public static void main(String [] args){
        if(args.length<=0){
            System.out.println("Pass IP Address of server as argument");
            return;
        }

        try{
            new av.Client(args[0]).Start();
        }
        catch(Exception ex){
            System.out.println("Error in client!!!");
        }
    }
}