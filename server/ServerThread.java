package by.stankevich.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Lenovo on 06/16/2017.
 */
public class ServerThread extends Thread {
    private DataOutputStream out; //send
    private DataInputStream in; //receive
    private Socket socket; //client socket
    private boolean isOn;

    private static List<ServerThread> handlers = Collections.synchronizedList(new ArrayList<ServerThread>());



    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));


    }
    public void run() {
       // System.out.println("1");
        isOn = true;
        try {
            synchronized (handlers){
                handlers.add(this);
            }
            System.out.println(handlers);
            while (isOn) {
                String msg = in.readUTF();
                //System.out.println("3");
                broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //System.out.println("4");
            synchronized (handlers){
                handlers.remove(this);
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void broadcast(String message) {
        System.out.println(message);
        synchronized (handlers) {
            Iterator<ServerThread> iterator = handlers.iterator();
            while(iterator.hasNext()){
                ServerThread st = iterator.next();
                try{
                    synchronized (st.out){
                        st.out.writeUTF(message);
                    }
                    st.out.flush();
                }catch (IOException e){
                    e.printStackTrace();
                    st.isOn = false;
                }
            }
        }
    }
}
