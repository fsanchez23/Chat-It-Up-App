package fsanchez.a8;

import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import jforsythe.Message;

/**
 * ServerListener class to get details from server and it is used by Main class
 * @author Frankie Sanchez
 * @version 1.0
 */
public class ServerListener extends Thread{

    /**
     * @param inputStream used for user input
     * @param socket used to connect to server
     * @param output used to receive text output
     * @patam running boolean used for state true/false
     *
     */

    private Socket socket;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private EditText output;
    public boolean running = true;

    /**
     *
     * @param socket used for server
     * @param output used for user output
     */

    public ServerListener(Socket socket, EditText output){
        this.socket = socket;
        this.output = output;
        try{
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * run function
     * Used to run and ensure our application is running as intended
     */
    @Override
    public void run() {
        try{
            while(running){
                Message tmp = (Message)objectInputStream.readObject();
                output.append(String.format("%s: %s%n", tmp.getName(), tmp.getMessage()));

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            {
                try{
                    objectInputStream.close();;
                    inputStream.close();;
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
