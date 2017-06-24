package by.stankevich.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Lenovo on 06/16/2017.
 */
public class NetClient extends JFrame implements Runnable {
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private JTextArea outTextArea;
    private JTextField inTextField;
    boolean isOn;

    public NetClient(String title, Socket socket, DataInputStream dis, DataOutputStream dos) {
        super(title);
        this.socket = socket;
        inStream = dis;
        outStream = dos;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, outTextArea = new JTextArea());
        outTextArea.setEditable(false);
        cp.add(BorderLayout.SOUTH, inTextField = new JTextField());
        inTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outStream.writeUTF(inTextField.getText());
                    outStream.flush();
                }catch (IOException ex){
                    ex.printStackTrace();
                    isOn = false;
                }
                inTextField.setText("");
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                isOn = false;
                try{
                    outStream.close();
                } catch (IOException ex){
                    ex.printStackTrace();
                }
                try{
                    socket.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,500);
        setVisible(true);
        inTextField.requestFocus();
        (new Thread(this)).start();

    }

    @Override
    public void run(){
        //System.out.println("1");
        isOn = true;
        try{
            while (isOn){
                //System.out.println("2");
                String line = inStream.readUTF();
                outTextArea.append(line + "\n");

                System.out.println(line);
                //System.out.println("3");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            inTextField.setVisible(false);
            validate();
        }
    }

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        String host = "8030";

        Socket socket = new Socket(address, Integer.parseInt(host));
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            new NetClient("Client",socket, dis, dos);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if(dos != null) {
                    dos.close();
                }
            }catch (IOException ex1) {
                ex1.printStackTrace();
            }
            try{
                socket.close();
            }catch (IOException ex2){
                ex2.printStackTrace();
            }
        }
    }
}
