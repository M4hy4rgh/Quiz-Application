/* Developed By:
    Mahyar Ghasemi Khah
*/
package com.example.demo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HelloApplication extends Application {
    // array to save random questions
    public static Question[] questions = new Question[5];
    //value of radio button used in for loop
    public static String[] alphabet = {"A", "B", "C", "D"};
    //array to save the selected answers
    public static int[] selectedState = {-1, -1, -1, -1, -1};

    @Override
    public void start(Stage stage) throws Exception {
        //---------components and Fields---------
        Label headerLabel = new Label("Please Answer the questions below. " +
                "Select a single answer for each question.");
        //name field
        Label starLabel = new Label("*");
        starLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        starLabel.setTextFill(Color.FIREBRICK);
        Label nameLabel = new Label("Full Name: ");
        TextField nameTextField = new TextField();
        nameTextField.setPromptText("Enter your name here"); // placeholder in the text field
        nameTextField.setFocusTraversable(false); // no focus on the text field
        nameTextField.setMaxWidth(200);
        Validation(nameTextField);
        //questions label
        Label questionsLabel = new Label("Questions");
        questionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 14));

        //button bar
        ButtonBar btnBar = new ButtonBar();

        Button startOver = new Button("Start New");
        startOver.setFocusTraversable(false);
        ButtonBar.setButtonData(startOver, ButtonBar.ButtonData.LEFT);
        Button showAVG = new Button("Show AVG");
        showAVG.setFocusTraversable(false);
        ButtonBar.setButtonData(showAVG, ButtonBar.ButtonData.FINISH);
        Button calcScore = new Button("Calculate");
        calcScore.setFocusTraversable(false);
        ButtonBar.setButtonData(calcScore, ButtonBar.ButtonData.OK_DONE);

        Label resultL = new Label();
        resultL.setPadding(new Insets(3.25, 0, 3.25, 0));
        resultL.setAlignment(Pos.CENTER);
        Stop[] stops = {new Stop(0, Color.web("#ddd")),
                new Stop(1, Color.web("#f0f0f0"))};
        resultL.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 1, 0, 0,
                true, CycleMethod.NO_CYCLE, stops), new CornerRadii(2.5), Insets.EMPTY)));
        resultL.setBorder(new Border(new BorderStroke(Color.rgb(192, 192, 192),
                BorderStrokeStyle.SOLID, new CornerRadii(2.5), new BorderWidths(.85))));
        ButtonBar.setButtonData(resultL, ButtonBar.ButtonData.OTHER);

        //-----------Layout----------------
        Initialized();
        //grandparent layout
        BorderPane root = new BorderPane();

        //parent layout(grid)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 10, 20, 10));

        //toggle group used by calculate button to access questions.
        ToggleGroup[] toggleGroups = QuestionsCreator(grid);

        //child layout (for scrolling the page)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.setCenter(scrollPane);

        //child layout (for the buttons)
        btnBar.getButtons().addAll(startOver, showAVG, calcScore, resultL);
        btnBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
        btnBar.setPadding(new Insets(10, 40, 10, 10));
        root.setBottom(btnBar);

        //child layout (for the name and text field)
        HBox headerBox = new HBox();
        headerBox.setSpacing(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        //child layout (for the headerLabel and headerBox)
        VBox headerBox2 = new VBox();
        headerBox2.setSpacing(10);
        headerBox2.setPadding(new Insets(10, 10, 8, 10));

        //---------location of components in layout---------

        //name
        headerBox.getChildren().addAll(starLabel, nameLabel, nameTextField);
        headerBox2.getChildren().addAll(headerLabel, headerBox);
        root.setTop(headerBox2);

        //QLabel(Questions)
        grid.add(questionsLabel, 0, 0);

        //------------Scene----------------
        Scene scene = new Scene(root);
        stage.setHeight(600);
        stage.setWidth(600);
        stage.setMinWidth(350);
        stage.setMinHeight(350);
        // set the preferred size of the scroll pane
        scrollPane.setPrefSize(scene.getWidth(), stage.getHeight());

        stage.setTitle("Multiple Choice Exam");
        stage.setScene(scene);
        stage.show();


        //----------event handling----------
        // submit button event that shows the AVG and total marks and count of question answered for each class.
        showAVG.setOnAction(e -> {
            CalculateAVG();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exam Results");
            alert.setHeaderText("Exam Results");
            alert.setContentText("The average score is: " + String.format("%,.2f", CalculateAVG()) + "\nTotal Marks: " +
                    getTotalMark() + "\nTotal Questions: " + getCount());
            alert.showAndWait();
        });
        // calculate button event that calculate the marks for each question and shows the result, the correct answer.
        calcScore.setOnAction(e -> {
            //get the name
            String name = nameTextField.getText();
            //if the name field is empty then display an error message
            if (name.equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please enter your name.");
                alert.showAndWait();
            } else {
                String[] scores = new String[5];
                for (int i = 0; i < 5; i++) {
                    RadioButton selectedRadioButton = (RadioButton) toggleGroups[i].getSelectedToggle();
                    String toggleGroupValue = "";
                    if (selectedRadioButton != null) {
                        toggleGroupValue = selectedRadioButton.getText();
                    }
                    scores[i] = toggleGroupValue;
                    Toggle t = toggleGroups[i].getToggles().get(Character.getNumericValue(questions[i]
                            .getCorrectAnswer().charAt(0)) - 10);
                    RadioButton correctAnswer = (RadioButton) t;

                    correctAnswer.setStyle("-fx-text-fill: green");
                    HBox h = (HBox) ((VBox) scene.lookup("#qAnswerBox" + i)).getChildren()
                            .get(Character.getNumericValue(questions[i].getCorrectAnswer().charAt(0)) - 10);

                    if (toggleGroupValue.equals("")) {
                        HBox temp;
                        for (int j = 0; j < toggleGroups[i].getToggles().size(); j++) {
                            temp = (HBox) ((VBox) scene.lookup("#qAnswerBox" + i)).getChildren().get(j);
                            temp.getChildren().get(1).setStyle("-fx-text-fill: red");
                            temp.getChildren().get(0).setStyle("-fx-text-fill: red");
                        }
                    } else {
                        HBox h1 = (HBox) ((VBox) scene.lookup("#qAnswerBox" + i)).getChildren()
                                .get(Character.getNumericValue(selectedRadioButton.getText().charAt(0)) - 10);
                        h1.getChildren().get(1).setStyle("-fx-text-fill: red");
                        h1.getChildren().get(0).setStyle("-fx-text-fill: red");

                    }
                    h.getChildren().get(1).setStyle("-fx-text-fill: green");
                    h.getChildren().get(0).setStyle("-fx-text-fill: green");
                }

                calculateScore(scores);
                appendResult(name, scores);
                resultL.setText((calculateScore(scores)) + "/100");

                //reset the radio buttons and the text field
                resetAfterSubmit(toggleGroups, nameTextField);
                showAVG.setDisable(false);

            }
        });
        //resetButton to reset the radio buttons and the text field and read the questions from the file
        startOver.setOnAction(e -> {
            grid.getChildren().clear();
            resultL.setText(null);
            try {
                Initialized();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            ToggleGroup[] toggleGroups1 = QuestionsCreator(grid);
            for (int i = 0; i < 5; i++) {
                toggleGroups[i] = toggleGroups1[i];
            }
            grid.add(questionsLabel, 0, 0);

        });
    }

    //method to check if the answer is correct, returns true if correct, false if incorrect
    public static boolean checkAnswer(String answer, String correctAnswer) {
        boolean correct = false;
        if (answer.equals(correctAnswer)) {
            correct = true;
        }
        return correct;
    }

    //method to calculate the score
    public static int calculateScore(String[] scores) {
        int score = 0;
        for (int i = 0; i < scores.length; i++) {
            if (checkAnswer(scores[i], questions[i].getCorrectAnswer())) {
                score += 20;
            } else if (scores[i].equals("")) {
                score += 0;
            } else {
                score -= 5;
            }
        }
        if (score < 0) {
            score = 0;
        }
        return score;
    }

    //method to append the result to the file
    public static void appendResult(String name, String[] answers) {
        try {
            //create a file
            File file = new File("..//result.txt");

            //create a print writer
            PrintWriter output = new PrintWriter(new FileOutputStream(file, true));
            String result = name + " ";
            for (int i = 0; i < answers.length; i++) {
                if (answers[i].equals("")) {
                    result += "X,";
                } else {
                    result += answers[i] + ",";
                }
            }
            result.substring(0, result.length() - 1);

            //append the result to the file
            output.println(result + " " + calculateScore(answers));
            output.close();
        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found");
            alert.setContentText("The file was not found");
            alert.showAndWait();

        }
    }

    //method to calculate the average
    public static double CalculateAVG() {
        double total = 0;
        double count = 0;
        try {
            File file = new File("..//result.txt");
            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                String line = input.nextLine();
                if (line.equals("")) {
                    continue;
                }
                String[] tokens = line.split(" ");
                total += Integer.parseInt(tokens[tokens.length - 1]);
                count++;
            }
            input.close();
        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found");
            alert.setContentText("The file was not found");
            alert.showAndWait();
        }
        return total / (count * 5);
    }

    //method to change the radio button selection status.
    public static void changeRDBtnSelection(RadioButton rdBtn, ToggleGroup tg) {
        rdBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (selectedState[Integer.parseInt(rdBtn.getId())] == Character.getNumericValue(((RadioButton)
                        tg.getSelectedToggle()).getText().charAt(0)) - 10) {
                    rdBtn.setSelected(false);
                    selectedState[Integer.parseInt(rdBtn.getId())] = -1;
                } else {
                    selectedState[Integer.parseInt(rdBtn.getId())] = Character.getNumericValue(((RadioButton)
                            tg.getSelectedToggle()).getText().charAt(0)) - 10;
                }
            }
        });
    }

    //method to get total marks.
    public static int getTotalMark() {
        int total = 0;
        try {
            File file = new File("..//result.txt");
            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                String line = input.nextLine();
                if (line.equals("")) {
                    continue;
                }
                String[] tokens = line.split(" ");
                total += Integer.parseInt(tokens[tokens.length - 1]);
            }
            input.close();
        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found");
            alert.setContentText("The file was not found");
            alert.showAndWait();
        }
        return total;
    }

    //method to get the number of question used.
    public static int getCount() {
        int count = 0;
        try {
            File file = new File("..//result.txt");
            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                String line = input.nextLine();
                if (line.equals("")) {
                    continue;
                }
                count++;
            }
            input.close();
        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found");
            alert.setContentText("The file was not found");
            alert.showAndWait();
        }
        return count * 5;
    }

    //method to reset the radio buttons and the text field after calculating the result.
    public static void resetAfterSubmit(ToggleGroup[] groups, TextField nameField) {
        for (int i = 0; i < groups.length; i++) {
            groups[i].selectToggle(null);
        }
        nameField.setText("");
    }

    //method to shuffle an array.
    public static String[] shuffle(String[] arr) {
        String[] shuffled = new String[arr.length];
        int[] newIndexes = getRandom(arr.length, arr.length);
        for (int i = 0; i < arr.length; i++)
            shuffled[i] = arr[newIndexes[i]];
        return shuffled;
    }

    //get random questions from the question bank.
    public static int[] getRandom(int n, int range) {
        int rand;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            rand = (int) (Math.random() * range);
            for (int j = 0; j < i; j++) {
                if (arr[j] == rand) {
                    rand = (int) (Math.random() * range);
                    j = -1;
                }
            }
            arr[i] = rand;
        }
        return arr;
    }

    //method to get the questions from the question bank and put it into the Questions array.
    public static void Initialized() throws Exception {
        File file = new File("..//exam.txt");
        Scanner sc = new Scanner(file);

        List<String> original = new ArrayList<String>();
        while (sc.hasNextLine()) {
            original.add(sc.nextLine());
            if (original.get(original.size() - 1).equals("")) {
                original.remove(original.size() - 1);
            }
        }
        sc.close();

        int[] randomQs = getRandom(5, original.size());
        String[] Qs = new String[5];
        for (int i = 0; i < randomQs.length; i++)
            Qs[i] = original.get(randomQs[i]);// get the random questions from the question bank.

        for (int i = 0; i < Qs.length; i++) {
            String[] split = Qs[i].split(",");
            String[] shuffledOptions = shuffle(Arrays.copyOfRange(split, 1, split.length));// shuffle the options
            int correctIndex = 0;
            for (int j = 0; j < shuffledOptions.length; j++) {
                if (shuffledOptions[j].equals(split[1])) {
                    correctIndex = j;
                    break;
                }
            }
            questions[i] = new Question(split[0], shuffledOptions, alphabet[correctIndex]);
        }
    }

    //method to get values from the Questions array and put it into the grid layout.
    public static ToggleGroup[] QuestionsCreator(GridPane grid) {
        ToggleGroup[] toggleGroups = new ToggleGroup[5];
        for (int i = 0; i < questions.length; i++) {
            Label questionLabel = new Label((i + 1) + ") " + questions[i].getQuestion());
            grid.add(questionLabel, 0, ((2 * i) + 1), 2, 1);
            ToggleGroup group = new ToggleGroup();
            HBox qA;
            VBox qAnswerBox = new VBox();
            qAnswerBox.setId("qAnswerBox" + i);
            qAnswerBox.setSpacing(10);
            qAnswerBox.setAlignment(Pos.CENTER_LEFT);
            qAnswerBox.setPadding(new Insets(0, 10, 0, 20));

            for (int j = 0; j < questions[i].getOptions().length; j++) {
                RadioButton radioButton = new RadioButton(alphabet[j]);
                radioButton.setId("" + i);
                Label QL = new Label(") " + questions[i].getOptions()[j]);
                radioButton.setToggleGroup(group);
                radioButton.setFocusTraversable(false);
                qA = new HBox();
                qA.setAlignment(Pos.CENTER_LEFT);
                qA.getChildren().addAll(radioButton, QL);
                qAnswerBox.getChildren().addAll(qA);
                changeRDBtnSelection(radioButton, group);
            }
            grid.add(qAnswerBox, 0, ((2 * i) + 2));
            toggleGroups[i] = group;
        }
        return toggleGroups;
    }

    // validating the name TextField
    public static void Validation(TextField nameField) {
        final Tooltip tooltip = new Tooltip("Please enter a valid name");
        tooltip.setAutoHide(false);
        nameField.setTooltip(tooltip);

        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue || nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                tooltip.hide();
                tooltip.setOpacity(0);
            }
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            tooltip.hide();
            tooltip.setOpacity(0);
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                nameField.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
                tooltip.show(nameField, nameField.localToScreen(155, -2).getX(),
                        nameField.localToScreen(155, -2).getY());
                tooltip.setOpacity(1);
            }
        });
    }


    public static void main(String args) throws Exception {
        launch();
    }
}