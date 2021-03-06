package clientfx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientFx extends Application {

    private String user;
    private String port;
    private int portDigit;
    private Client client = null;
    private TextArea log;
    private Socket socket = null;

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Welcome");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");

        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Port:");
        grid.add(pw, 0, 2);

        final TextField pBox = new TextField();
        grid.add(pBox, 1, 2);

        Button btn = new Button("Connect");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        actiontarget.setId("actiontarget");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                port = pBox.getText();
                boolean keepGoing = false;
                try {
                    portDigit = Integer.parseInt(port);
                    try {
                        if ((!userTextField.getText().isEmpty()) && (!pBox.getText().isEmpty())) {
                            InetAddress adress = InetAddress.getLocalHost();
                            socket = new Socket(adress, portDigit);
                            keepGoing = true;
                        }
                    } catch (UnknownHostException uhe) {
                        if (grid.getChildren().contains(actiontarget)) {
                            grid.getChildren().remove(actiontarget);
                        }
                        actiontarget.setText("Server isnt at this port");
                        grid.add(actiontarget, 0, 4);
                        keepGoing = false;
                    } catch (IOException ioe) {
                        if (grid.getChildren().contains(actiontarget)) {
                            grid.getChildren().remove(actiontarget);
                        }
                        actiontarget.setText("Type correct port");
                        grid.add(actiontarget, 0, 4);
                        keepGoing = false;
                    }
                } catch (NumberFormatException x) {
                    if (grid.getChildren().contains(actiontarget)) {
                        grid.getChildren().remove(actiontarget);
                    }
                    actiontarget.setText("Port must be a number");
                    grid.add(actiontarget, 0, 4);
                }
                if ((!userTextField.getText().isEmpty()) && (!pBox.getText().isEmpty()) && (keepGoing)) {
                    user = userTextField.getText();

                    client = new Client(socket, user, ClientFx.this);

                    primaryStage.hide();
                    Stage secondaryStage = new Stage();
                    secondaryStage.setTitle("Chat");
                    GridPane sgrid = new GridPane();
                    sgrid.setAlignment(Pos.CENTER);
                    sgrid.setPadding(new Insets(75, 75, 75, 75));

                    log = new TextArea();
                    log.setMinSize(450, 680);
                    log.setFocusTraversable(false);
                    log.setEditable(false);
                    sgrid.add(log, 0, 0);

                    final TextField userMessage = new TextField();
                    userMessage.setMinWidth(400);
                    Button sendBtn = new Button("Send");
                    sendBtn.setId("sendingButton");
                    Button exitBtn = new Button("Exit");
                    exitBtn.setId("exitingButton");
                    HBox chatBtns = new HBox();
                    chatBtns.setSpacing(0);
                    chatBtns.getChildren().addAll(userMessage, sendBtn, exitBtn);
                    sgrid.add(chatBtns, 0, 1);

                    sendBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            if (!(userMessage.getText().isEmpty())) {
                                client.send(userMessage.getText());
                                userMessage.clear();
                            }
                        }
                    });
                    sgrid.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent t) {
                            if ((t.getCode() == KeyCode.ENTER) && !(userMessage.getText().isEmpty())) {
                                client.send(userMessage.getText());
                                userMessage.clear();
                            }
                        }
                    });

                    exitBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            client.stop();
                            Platform.exit();
                        }
                    });

                    Scene scene = new Scene(sgrid);
                    secondaryStage.setScene(scene);
                    scene.getStylesheets().add(ClientFx.class.getResource("Chat.css").toExternalForm());
                    secondaryStage.show();

                    secondaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent we) {
                            client.stop();
                            Platform.exit();
                        }
                    }));

                } else if (userTextField.getText().isEmpty()) {
                    if (grid.getChildren().contains(actiontarget)) {
                        grid.getChildren().remove(actiontarget);
                    }
                    actiontarget.setText("Write User Name");
                    grid.add(actiontarget, 0, 4);
                } else if (pBox.getText().isEmpty()) {
                    if (grid.getChildren().contains(actiontarget)) {
                        grid.getChildren().remove(actiontarget);
                    }
                    actiontarget.setText("Write port");
                    grid.add(actiontarget, 0, 4);
                }
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(ClientFx.class.getResource("Login.css").toExternalForm());
        primaryStage.show();
    }

    public void myTextAreaAppend(String text) {
        log.appendText(text + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}