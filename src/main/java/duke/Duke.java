package duke;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;







public class Duke extends Application{

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/DaDuke.jpg"));
    private String input;

    LoadFile loadFile;
    TaskList taskList;

    int first = 1;


    private void handleUserInput() {
        String userInputText = userInput.getText();
        //String dukeResponse = getResponse();
        DialogBox userDialog = DialogBox.getUserDialog("b", user);
        DialogBox dukeDialog = DialogBox.getDukeDialog("a", duke);
        dialogContainer.getChildren().addAll(userDialog, dukeDialog);
        userInput.clear();
    }
    public Duke(String input) {
        this.input = input;
    }

    public Duke() throws Exception {

        try {
            File f = new File("./data/zenith.txt");
            String filePath = "./data/zenith.txt";
            if (!f.exists()) {
                FileOutputStream s = FileUtils.openOutputStream(new File("./data/zenith.txt"));
            }
            loadFile = new LoadFile(filePath);
            TaskList lst = new TaskList();
            loadFile.load();
        } catch (Exception e) {
            System.out.println(e);
        }
    }



    public void initialization(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);
        userInput = new TextField();
        sendButton = new Button("Send");
        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);
        scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
        mainLayout.setPrefSize(400.0, 600.0);
        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        userInput.setPrefWidth(325.0);
        sendButton.setPrefWidth(55.0);
        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });
        userInput.setOnAction((event) -> {
            handleUserInput();
        });
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    @Override
    public void start(Stage stage) throws Exception{
        try {
            initialization(stage);
            String filePath = "./data/zenith.txt";
            loadFile = new LoadFile(filePath);
            TaskList lst = new TaskList();
            loadFile.load();

        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public String getResponse() throws Exception{
        try {
            taskList = new TaskList();
            String output = taskList.Answer(input);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";

    }

}
