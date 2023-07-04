package lk.ijse.ChatApplication.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lk.ijse.ChatApplication.util.ClientHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


    public class LoginFormController implements Initializable {
        public JFXTextField txtUserName;
        public JFXButton btnStart;


        public static ArrayList<ClientHandler> socketArrayList = new ArrayList<>();
        ServerSocket serverSocket;
        Socket socket;
        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;

        public static String message = "";

        public static String userName;


        public void startBtnOnAction(ActionEvent actionEvent) throws Exception {
            startClient();
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {



            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(2000);
                    System.out.println("Server Started..");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }


        public void enterKeyReleased(KeyEvent keyEvent) throws IOException {
            if (keyEvent.getCode() == KeyCode.ENTER){
                startClient();
            }
        }

        public void startClient() throws IOException {
            userName = txtUserName.getText();

            Parent parent = FXMLLoader.load(getClass().getResource("../view/ClientForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.show();

            new Thread(() -> {
                try {
                    socket = serverSocket.accept();
                    System.out.println("Client is connected...!");

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    ClientHandler clientHandler = new ClientHandler(socket);

                    Thread thread = new Thread(clientHandler);
                    thread.start();

                    while (!socket.isConnected()) {
                        message = dataInputStream.readUTF();
                        System.out.println("Client : " + message);
                        dataOutputStream.writeUTF(message.trim());
                        dataOutputStream.flush();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();


    }
}
