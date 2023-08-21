package fsanchez.a8;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import jforsythe.Message;
import jforsythe.MessageType;

/**
 * Main Drover Class for our A8
 * @author Frankie Sanchez
 * @Version 1.0.0
 */

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    /**
     * @param txtInput Holds text input or text
     * @param txtOutput Holds text output
     * @param name Hold name of user
     * @param socket Used to connect to our server
     * @param outputStream Used for output purposes
     * @param objectOutputStream used for output purposes
     * @param serverListener used to gather data from our server
     */
    private  EditText txtInput;
    private EditText txtOutput;
    private String name;
    private Socket socket;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
    private ServerListener serverListener;

    /**
     * onCreate function used for android application
     * @param savedInstanceState used to implement a text view
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        this.txtInput = findViewById(R.id.txtInput);
        this.txtInput.setOnEditorActionListener(this);
        this.txtOutput = findViewById(R.id.txtOutput);


        getUserName();

    }

    /**
     * onDestroy function
     * Used to close and flush our processes ones the application is closed
     */

    @Override
    protected void onDestroy(){
        super.onDestroy();
        serverListener.running=false;
        try{
            objectOutputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * connect function
     * Used to connect to the Odin server to run our chat client on the Odin server
     */
    private void connect() {
        try {
            socket = new Socket("odin.cs.csubak.edu",3390);
            outputStream = socket.getOutputStream();
            outputStream.flush();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.flush();

            serverListener= new ServerListener(socket, txtOutput);
            serverListener.start();

            Message connect = new Message(MessageType.CONNECT, name, "Hi from Android");
            objectOutputStream.writeObject(connect);
            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * getUserName function
     * Used to get a user's name and display on application
     * onClick function to verify if user name is correct and ensure a user name is entered
     */
    private void getUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Name");
        EditText userNameInput = new EditText(this);
        userNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(userNameInput);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = userNameInput.getText().toString();
                Log.d("USER NAME", name);
                if(name.equals(""))getUserName();
                else connect();

            }

        });
        builder.show();
    }


    /**
     * onEditorAction functoin
     * @param v used for our TextView
     * @param actionId used for Id
     * @param event used for event on action of user
     * @return true if it works
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(event == null || event.getAction() == KeyEvent.ACTION_UP) {
            Message tmp = new Message(MessageType.MESSAGE, name, txtInput.getText().toString());
            try {
                objectOutputStream.writeObject(tmp);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            txtInput.setText("");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return true;
    }
}