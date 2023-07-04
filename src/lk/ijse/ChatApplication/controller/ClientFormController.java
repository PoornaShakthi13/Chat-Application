/*
 * @Created by: Abhishek Ashinsa
 * @Date: 06/08/2022
 * @Project: Chat-Room
 * */

package lk.ijse.ChatApplication.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.videoio.VideoCapture;

public class ClientFormController implements Initializable {
    public TextArea txtArea;
    public Label lblClientName;
    public TextField txtField;
    public JFXButton btnSend;
    public FontAwesomeIconView imgCamera;
    public ImageView imgEmoji;

    final int PORT = 2000;
    public ScrollPane emojiPane;
    public AnchorPane mediaPane;
    public Pane imageViewPane;
    public ImageView imgView;
    public JFXButton btnSendImage;
    public Label lblTime;
    public JFXButton btnCancelSendImg;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public static String message = "";

    public int clickCount = 0;


    public void sendBtnOnAction(ActionEvent actionEvent) throws IOException {
        sendMessage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblClientName.setText(LoginFormController.userName);
        mediaPane.setDisable(true);
        mediaPane.setVisible(false);

        emojiPane.setDisable(true);
        emojiPane.setVisible(false);

        imageViewPane.setDisable(true);
        imageViewPane.setVisible(false);

        new Thread(() -> {
            try {
                socket = new Socket("localhost",PORT);

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (socket.isConnected()) {
                    message = dataInputStream.readUTF();
                    txtArea.appendText(message+"\n");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void enterKeyReleased(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER){
            sendMessage();
        }
    }

    private void sendMessage() throws IOException {
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        dataOutputStream.writeUTF(txtField.getText().trim());
        dataOutputStream.flush();
        txtField.clear();
    }

    public void emojiBtnClicked(MouseEvent event) throws IOException {
        clickCount++;

        if (clickCount%2==1) {
            emojiPane.setDisable(false);
            emojiPane.setVisible(true);
        }else {
            emojiPane.setDisable(true);
            emojiPane.setVisible(false);
        }

    }

    public void cameraBtnClecked(MouseEvent event) {
        clickCount++;

        if (clickCount%2==1) {
            mediaPane.setDisable(false);
            mediaPane.setVisible(true);
        }else {
            mediaPane.setDisable(true);
            mediaPane.setVisible(false);
        }
    }

    public void photosMouseClicked(MouseEvent event) throws IOException {
        System.out.println("Open Photos");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images","*jpeg","*jpg","*png","*svg","*docs","*gif","*psd","*ico"));
        File file = fileChooser.showOpenDialog(new Stage());
        String path = file.getPath();

        if (file != null) {
            imageViewPane.setDisable(false);
            imageViewPane.setVisible(true);

            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);

            URL url = file.toURI().toURL();
            imgView.setImage(new Image(url.toExternalForm()));
        }

    }

    public void cameraMouseClicked(MouseEvent event) {
        System.out.println("Open Camera");
    }

    public void documentMouseClicked(MouseEvent event) {
        System.out.println("open Docs");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Document Files","*pdf","*txt","*docx","*xlsx","*docs","*html"));
        File file = fileChooser.showOpenDialog(new Stage());
        String path = file.getPath();
    }

    public void sendImageBtnOnAction(ActionEvent actionEvent) {
        //txtArea.appendText(String.valueOf(imgView));
    }


    public void cancelSendImageBtnOnAction(ActionEvent actionEvent) {
        imageViewPane.setDisable(true);
        imageViewPane.setVisible(false);

        mediaPane.setDisable(true);
        mediaPane.setVisible(false);
    }
}
