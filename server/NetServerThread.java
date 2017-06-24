package by.stankevich.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lenovo on 06/16/2017.
 */
public class NetServerThread {

    public NetServerThread(int port) throws IOException{
        ServerSocket server = new ServerSocket(port);
        System.out.println("Initialized");
        try {
            while (true) {
                Socket socket = server.accept();
                System.out.println(socket.getInetAddress().getHostName() + " connected");
                ServerThread thread = new ServerThread(socket);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            server.close();
        }
    }
    public static void main(String[] args) {
        try{
            String port = "8030";
            new NetServerThread(Integer.parseInt(port));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
