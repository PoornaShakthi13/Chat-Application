package lk.ijse.ChatApplication.util;

import lk.ijse.ChatApplication.controller.LoginFormController;
import lk.ijse.ChatApplication.controller.LoginFormController;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.clientName = LoginFormController.userName;
            System.out.println("client handler");
            clientHandlers.add(this);
            broadcastMessage(" is connected to the chat...! \n");
        } catch (IOException e) {
            closeAll(socket, dataInputStream, dataOutputStream);
        }
    }

    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = dataInputStream.readUTF();
                broadcastMessage(messageFromClient);
                System.out.println(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, dataInputStream, dataOutputStream);
                break;
            }
        }

    }

    public void broadcastMessage(String message){
        for (ClientHandler clientHandler : clientHandlers) {
            try {

                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                String time = currentTime.format(dateTimeFormatter);

                clientHandler.dataOutputStream.writeUTF(clientName+" : " + message+"               "+time);
                clientHandler.dataOutputStream.flush();
                System.out.println(message);


            } catch (IOException e) {
                closeAll(socket, dataInputStream, dataOutputStream);
            }
        }
    }

    public void removeClientHandler (){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+clientName+" has left from the chat...! \n");
    }

    private void closeAll(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        removeClientHandler();

        try {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
