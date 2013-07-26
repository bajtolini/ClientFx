package clientfx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientFx extends Application {

    private String user;
    private String port;
    private int portDigit;

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");

        grid.add(scenetitle, 0, 0, 2, 1);

        final Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
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
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                user = userName.getText();

                port = pBox.getText();
                Pattern p = Pattern.compile("\\d*");
                Matcher m = p.matcher(port);
                if (m.find()) {
                    portDigit = Integer.parseInt(m.group(0));
                }

                actiontarget.setId("actiontarget");
                actiontarget.setText("Connecting...");
                Client client = new Client(portDigit, user);

                primaryStage.hide();
                Stage secondaryStage = new Stage();
                secondaryStage.setTitle("Chat");
                GridPane sgrid = new GridPane();
                sgrid.setAlignment(Pos.CENTER);
                sgrid.setPadding(new Insets(75, 75, 75, 75));

                Label log = new Label();
                log.setMinSize(450, 680);
                sgrid.add(log, 0, 0);

                TextField userMessage = new TextField();
                sgrid.add(userMessage, 0, 1);

                Button sendBtn = new Button("Send");
                HBox sendHBtn = new HBox(10);
                sendHBtn.setAlignment(Pos.BOTTOM_RIGHT);
                sendHBtn.getChildren().add(sendBtn);
                sgrid.add(sendHBtn, 1, 1);
                sendBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        //code for sending message
                    }
                });
                
                Button exitBtn = new Button("Exit");
                HBox exitHBtn = new HBox(10);
                exitHBtn.setAlignment(Pos.BOTTOM_RIGHT);
                exitHBtn.getChildren().add(exitBtn);
                sgrid.add(exitHBtn, 2, 1);
                exitBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        //code for exiting + need to erase exit with command .bye
                    }
                });

                sgrid.setGridLinesVisible(true);
                Scene scene = new Scene(sgrid);
                secondaryStage.setScene(scene);
                scene.getStylesheets().add(ClientFx.class.getResource("Chat.css").toExternalForm());
                secondaryStage.show();
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(ClientFx.class.getResource("Login.css").toExternalForm());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}