import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.bson.Document;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {

    //create a global variable for seating capacity
    static final int SEATING_CAPACITY = 42;

    public static void main(String[] args) {
        launch();
    }

    @Override
    //throws Exception
    public void start(Stage primaryStage){
        manu();
    }

    public void manu() {

        //Create string array to store seats details
        List<String> seatNo = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> day = new ArrayList<>();

        //user commands
        System.out.println("System for booking seats on a Train.");
        System.out.println("Welcome to A/C Compartment in 'Denuwara Menike' train to Badulla");
        System.out.println();

        //create a variable for store user input
        String userInput;

        //when user enter 'Q', programme is run and do user requirements
        while (true) {
            //Display the Menu to user
            System.out.println("Menu: ");
            System.out.println("1. \t Press 'A' to add a customer to a seat. \n2. \t Press 'V' to view all seats.");
            System.out.println("3. \t Press 'E' to Display Empty seats. \n4. \t Press 'D' to Delete customer from seat.");
            System.out.println("5. \t Press 'F' to Find the seat for a given customer's name. \n6. \t Press 'S' to Store program data in to file.");
            System.out.println("7. \t Press 'L' to Load program data from file. \n8. \t Press 'O' to View seats Ordered alphabetically by name.");
            System.out.println("9. \t Press 'Q' to Exit this programme.");
            System.out.println();
            System.out.print("Please enter your choice: ");

            //create a scanner
            Scanner input = new Scanner(System.in);

            //convert user input to upperCase and store it in a variable called userInput
            userInput = input.next().toUpperCase();

            //check the character which is user input
            switch (userInput) {
                case "A":
                    displayStageToA(name, seatNo, day);
                    break;

                case "V":
                    displayStageToV(seatNo, day, SEATING_CAPACITY);
                    break;

                case "E":
                    displayStageToE(seatNo, day);
                    break;

                case "D":
                    delete(seatNo, name, day);
                    break;

                case "F":
                    find(seatNo, name, day);
                    break;

                case "S":
                    saveData(seatNo, name, day);
                    break;

                case "L":
                    //create a List which has a string list in inside. for get returen of load.
                    List<List<String>> seatDetails;
                    seatDetails = loadData();

                    //update my array list
                    seatNo = seatDetails.get(0);
                    name = seatDetails.get(1);
                    day = seatDetails.get(2);
                    break;

                case "O":
                    createNamesInOrder(name);
                    break;

                case "Q":
                    System.out.println();
                    System.out.print("Do you want to save your data? If say yes, You will quit in this programe. (y/n) ");
                    userInput = input.next().toUpperCase();
                    if(userInput.equals("N")){
                        System.out.println("Please enter 'S' to save your data.");
                        break;
                    }
                    else if (userInput.equals("Y")){
                        System.out.println("Thank You for using this programme. We will hope, it is very helpful for you.");
                        System.exit(0);
                    }

                    //if user enter another character, output this massage
                default:
                    System.out.println();
                    System.out.println("Sorry! You was input incorrect character. Please check and input a correct character.");
            }
            System.out.println();
        }
    }

    //This function implemented for Add customers UI
    public void displayStageToA(List<String> name, List<String> seatNo, List<String> day) {

        //create an AnchorPane
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: linear-gradient(#fdcd3b, #ffed4b)");

        //Create a stage
        Stage stage = new Stage();

        Label lblForNav = new Label("Denuwara Menike Express Train");
        lblForNav.setStyle("-fx-font-size: 30;");
        lblForNav.setTextFill(Color.WHITE);
        HBox hBoxForNav = new HBox(lblForNav);
        hBoxForNav.setStyle("-fx-background-color: #F57F17; "); //Orange
        hBoxForNav.setPadding(new Insets(5, 80, 5, 65));
        hBoxForNav.setMinHeight(15.0);

        //Create list of buttons for show to seats
        Button[] buttons = new Button[SEATING_CAPACITY];

        //Create hBoxes for put buttons
        HBox[] hBoxesList1 = new HBox[11];
        HBox[] hBoxesList2 = new HBox[11];
        HBox[] hBoxesList3 = new HBox[10];
        HBox[] hBoxesList4 = new HBox[10];

        //Create labels, textField and a datPicker to book seats
        //There are in under the seat UI
        Label lblDate = new Label("Date: ");
        DatePicker datePicker = new DatePicker();

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                LocalDate futureYear = LocalDate.MAX.withYear(today.getYear());
                LocalDate futureDate = LocalDate.MAX.withMonth(today.getMonthValue());
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(futureYear) > 0 ||
                        date.compareTo(futureDate) > 0);
            }
        });

        //ceate a labels and textfeild for bottum form
        Label lblShowNumber = new Label("Seat Number: ");
        Label lblPutSeatNumber = new Label("Click on your Seat No.");
        Label lblShowName = new Label("Name: ");
        TextField txtCusName = new TextField();

        //That is a hidden button. if user doesnt enter a name this label popup
        Label lblRequired = new Label("Please fill this fields to book your seat");
        lblRequired.setFont(Font.font("Please fill this fields to book your seat", FontWeight.BOLD, 13.5));

        //create 42 buttons automatically
        for (int i = 0; i < buttons.length; i++) {

            buttons[i] = new Button("Seat No. " + (i + 1));
            buttons[i].setMinWidth(75.0);
            String showSeatNumber = String.valueOf (i + 1);
            buttons[i].setOnAction(event -> lblPutSeatNumber.setText(showSeatNumber));
        }

        //There all hBoxes and vBoxes are use to align the UI and show properly.

        //put buttons in correct position
        for (int i = 0; i < 11; i++) {

            //last row has only two buttons
            if (i == 10) {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
            } else {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
                hBoxesList3[i] = new HBox(buttons[i * 4 + 2]);
                hBoxesList4[i] = new HBox(buttons[i * 4 + 3]);
            }
        }

        //Create vBoxes and put hBoxes to there
        VBox vBoxLeft = new VBox(15, hBoxesList1);
        VBox vBoxMiddleLeft = new VBox(15, hBoxesList2);
        VBox vBoxMiddleRight = new VBox(15, hBoxesList3);
        VBox vBoxRight = new VBox(15, hBoxesList4);

        HBox hBoxes5 = new HBox(20, vBoxLeft, vBoxMiddleLeft);
        HBox hBoxes6 = new HBox(20, vBoxMiddleRight, vBoxRight);

        //create middle line space
        HBox hBoxForSeats = new HBox(150, hBoxes5, hBoxes6);
        hBoxForSeats.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        VBox vBox = new VBox(20, lblDate, lblShowNumber, lblShowName);
        VBox vBox1 = new VBox(15, datePicker, lblPutSeatNumber, txtCusName);

        HBox hBox5 = new HBox(20, vBox, vBox1);

        Button btnSaveAndAddNew = new Button("Save and Add New");
        btnSaveAndAddNew.setOnAction(event -> {

            //get date from the datepicker
            LocalDate datePickerValue = datePicker.getValue();

            if(lblPutSeatNumber.getText().equals("Click on your Seat No.") || txtCusName.getText().equals("")
                    || Objects.equals(datePicker.getValue(), null)){
                lblRequired.setText("* This 3 fields are essentiall.");
                lblRequired.setTextFill(Color.RED);
            }

            else if(seatNo.size() == 0){
                //add value of seatNo, name and date
                seatNo.add(lblPutSeatNumber.getText());
                name.add(txtCusName.getText().toLowerCase());
                day.add(String.valueOf(datePickerValue));

                String seatNoForSaved = lblPutSeatNumber.getText();
                String nameForSaved = txtCusName.getText().toLowerCase();

                //create a background to add new
                lblPutSeatNumber.setText("");
                txtCusName.setText("");
                datePicker.setValue(null);

                JOptionPane.showMessageDialog(null, "Successfully Saved! \n\nSeat No   : "
                        + seatNoForSaved +  "\nName      : " + nameForSaved + "\nDate        : "
                        + day.get(0) + "\n\nThank You for booking seat. Wish You very happy and safe journey.");

                lblRequired.setText("Please fill this fields to book your seat");
                lblRequired.setFont(Font.font("Please fill this fields to book your seat", FontWeight.BOLD, 13.5));
                lblRequired.setTextFill(Color.BLACK);
            }

            else if(!(name.size() == 0)){
                int notBooked = 0;

                for (int i = 0; i < seatNo.size(); i++) {

                    // String.valueOf(datePickerValue.getMonthValue())
                    if(seatNo.get(i).equals(lblPutSeatNumber.getText())
                            && day.get(i).equals(String.valueOf(datePickerValue))){

                        //create a background to add new
                        lblPutSeatNumber.setText("");
                        txtCusName.setText("");
                        datePicker.setValue(null);

                        JOptionPane.showMessageDialog(null,
                                "Sorry. That particular seat booked on specific date. ");

                        notBooked = 1;
                    }
                }

                if(notBooked != 1){

                    //add value of seatNo, name and date
                    seatNo.add(lblPutSeatNumber.getText());
                    name.add(txtCusName.getText().toLowerCase());
                    day.add(String.valueOf(datePickerValue));

                    String seatNoForSaved = lblPutSeatNumber.getText();
                    String nameForSaved = txtCusName.getText().toLowerCase();
                    int dateValueForSaved = datePickerValue.getDayOfMonth();
                    int monthValueForSaved = datePickerValue.getMonthValue();
                    int yearValueForSaved = datePickerValue.getYear();

                    //create a background to add new
                    lblPutSeatNumber.setText("");
                    txtCusName.setText("");
                    datePicker.setValue(null);

                    JOptionPane.showMessageDialog(null, "Successfully Saved! \n\nSeat No   : "
                            + seatNoForSaved +  "\nName      : " + nameForSaved + "\nDate        : "
                            + dateValueForSaved + "/" + monthValueForSaved + "/" + yearValueForSaved
                            + "\n\nThank You for booking seat. Wish You very happy and safe journey.");

                    lblRequired.setText("Please fill this fields to book your seat");
                    lblRequired.setFont(Font.font("Please fill this fields to book your seat", FontWeight.BOLD, 13.5));
                    lblRequired.setTextFill(Color.BLACK);
                }
            }
        });

        Button btnSaveAndClose = new Button("Save and Close");
        btnSaveAndClose.setOnAction(event -> {
            //get date from the datepicker
            LocalDate datePickerValue = datePicker.getValue();

            if(lblPutSeatNumber.getText().equals("Click on your Seat No.") || txtCusName.getText().equals("")
                    || Objects.equals(datePicker.getValue(), null)){
                lblRequired.setText("* This 3 fields are essentiall.");
                lblRequired.setTextFill(Color.RED);
            }

            else if(seatNo.size() == 0){
                //add value of seatNo, name and date
                seatNo.add(lblPutSeatNumber.getText());
                name.add(txtCusName.getText().toLowerCase());
                day.add(String.valueOf(datePickerValue));

                String seatNoForSaved = lblPutSeatNumber.getText();
                String nameForSaved = txtCusName.getText().toLowerCase();

                //create a background to add new
                lblPutSeatNumber.setText("");
                txtCusName.setText("");
                datePicker.setValue(null);

                JOptionPane.showMessageDialog(null, "Successfully Saved! \n\nSeat No   : "
                        + seatNoForSaved +  "\nName      : " + nameForSaved + "\nDate        : " + day.get(0)
                        + "\n\nThank You for booking seat. Wish You very happy and safe journey.");

                stage.close();
            }

            else if(!(name.size() == 0)){
                int notBooked = 0;

                for (int i = 0; i < seatNo.size(); i++) {

                    if(seatNo.get(i).equals(lblPutSeatNumber.getText()) && day.get(i).equals(String.valueOf(datePickerValue))){

                        //create a background to add new
                        lblPutSeatNumber.setText("");
                        txtCusName.setText("");
                        datePicker.setValue(null);

                        JOptionPane.showMessageDialog(null,
                                "Sorry. That particular seat booked on specific date. ");

                        notBooked = 1;
                    }
                }

                if(notBooked != 1){

                    //add value of seatNo, name and date
                    seatNo.add(lblPutSeatNumber.getText());
                    name.add(txtCusName.getText().toLowerCase());
                    day.add(String.valueOf(datePickerValue));

                    String seatNoForSaved = lblPutSeatNumber.getText();
                    String nameForSaved = txtCusName.getText().toLowerCase();
                    int dateValueForSaved = datePickerValue.getDayOfMonth();
                    int monthValueForSaved = datePickerValue.getMonthValue();
                    int yearValueForSaved = datePickerValue.getYear();

                    JOptionPane.showMessageDialog(null, "Successfully Saved! \n\nSeat No   : "
                            + seatNoForSaved +  "\nName      : " + nameForSaved + "\nDate        : "
                            + dateValueForSaved + "/" + monthValueForSaved + "/" + yearValueForSaved
                            + "\n\nThank You for booking seat. Wish You very happy and safe journey.");

                    stage.close();
                }
            }
        });

        VBox vBox6 = new VBox(20, btnSaveAndAddNew, btnSaveAndClose);

        HBox hBox7 = new HBox(100, hBox5, vBox6);

        VBox vBox2 = new VBox(5, lblRequired, hBox7);
        vBox2.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        VBox vBox7 = new VBox(15, hBoxForSeats, vBox2);

        //AnchorPane.setTopAnchor(vBox7, 20.0);
        AnchorPane.setLeftAnchor(vBox7, 20.0);
        AnchorPane.setRightAnchor(vBox7, 20.0);
        AnchorPane.setBottomAnchor(vBox7, 20.0);

        //Add elements to another pane
        anchorPane.getChildren().addAll(vBox7, hBoxForNav);
        stage.setScene(new Scene(anchorPane, 560, 700));
        stage.setTitle("Add a customer to a seat");
        stage.showAndWait();

    }

    //This function implemented for view all seats UI.
    public void displayStageToV(List<String> seatNo, List<String> day, int seatingCapacity) {

        //create an AnchorPane for the root
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: linear-gradient(#fdcd3b, #ffed4b)");

        Label lblForNav = new Label("Denuwara Menike Express Train");
        lblForNav.setStyle("-fx-font-size: 30;");
        lblForNav.setTextFill(Color.WHITE);
        HBox hBoxForNav = new HBox(lblForNav);
        hBoxForNav.setStyle("-fx-background-color: #F57F17; "); //Orange
        hBoxForNav.setPadding(new Insets(5, 80, 5, 65));
        hBoxForNav.setMinHeight(15.0);

        //ceate a label, button and a date picker
        Label lblDescription = new Label("View seats to specific date");
        lblDescription.setStyle("-fx-font-size: 15;");

        Label lblDate = new Label("Date: ");
        DatePicker datePicker = new DatePicker();

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                LocalDate futureYear = LocalDate.MAX.withYear(today.getYear());
                LocalDate futureMonth = LocalDate.MAX.withMonth(today.getMonthValue() + 1);
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(futureMonth) > 0 || date.compareTo(futureYear) > 0);
            }
        });

        Label lblRequired = new Label();

        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color: #25D366; "); //Green
        btnSearch.setTextFill(Color.WHITE);

        HBox hBoxForDate = new HBox(30, lblDate, datePicker);
        HBox hBoxForAllignDateAndBtn = new HBox(40, hBoxForDate, btnSearch);
        VBox vBoxForDateAndBtn = new VBox(5, lblDescription, lblRequired, hBoxForAllignDateAndBtn);
        vBoxForDateAndBtn.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        //Create list of buttons for show to seats
        Button[] buttons = new Button[seatingCapacity];

        //Create hBoxes for put buttons
        HBox[] hBoxesList1 = new HBox[11];
        HBox[] hBoxesList2 = new HBox[11];
        HBox[] hBoxesList3 = new HBox[10];
        HBox[] hBoxesList4 = new HBox[10];

        //AtomicInteger dayOfMonth = new AtomicInteger();

        btnSearch.setOnAction(event -> {
            //get date from the datepicker
            LocalDate datePickerValue = datePicker.getValue();
            try{
                //dayOfMonth.set(datePickerValue);
                viewSeatSpacificDate(seatNo, day, datePickerValue);
                lblRequired.setText("");
            }
            catch (Exception e){
                lblRequired.setText("* Date is required.");
                lblRequired.setTextFill(Color.RED);
            }
        });

        //create 42 buttons automatically
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button("Seat No. " + (i + 1));
            buttons[i].setTextFill(Color.WHITE);
            buttons[i].setStyle("-fx-background-color: #BA68C8; "); //Purple
            buttons[i].setMinWidth(75.0);
        }

        //put buttons in correct position
        for (int i = 0; i < 11; i++) {
            //last row has only two buttons
            if (i == 10) {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
            } else {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
                hBoxesList3[i] = new HBox(buttons[i * 4 + 2]);
                hBoxesList4[i] = new HBox(buttons[i * 4 + 3]);
            }
        }

        //Create vBoxes and put hBoxes to there
        VBox vBoxLeft = new VBox(15, hBoxesList1);
        VBox vBoxMiddleLeft = new VBox(15, hBoxesList2);
        VBox vBoxMiddleRight = new VBox(15, hBoxesList3);
        VBox vBoxRight = new VBox(15, hBoxesList4);

        HBox hBoxes5 = new HBox(20, vBoxLeft, vBoxMiddleLeft);
        HBox hBoxes6 = new HBox(20, vBoxMiddleRight, vBoxRight);

        //create middle line space
        HBox hBoxForAllSeats = new HBox(150, hBoxes5, hBoxes6);
        hBoxForAllSeats.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        VBox vBox5 = new VBox(15, vBoxForDateAndBtn, hBoxForAllSeats);

        //AnchorPane.setTopAnchor(vBox5, 20.0);
        AnchorPane.setLeftAnchor(vBox5, 20.0);
        AnchorPane.setRightAnchor(vBox5, 20.0);
        AnchorPane.setBottomAnchor(vBox5, 20.0);

        //Add elements to another pane
        anchorPane.getChildren().addAll(vBox5, hBoxForNav);

        Stage stage = new Stage();
        stage.setScene(new Scene(anchorPane, 560, 650));
        stage.setTitle("View structure of all seats");
        stage.showAndWait();
    }

    //This function implemented for View Seats in spefic date UI.
    // This massage call in stageForV method where user enter a date and click search button.
    public void viewSeatSpacificDate(List<String> seatNo, List<String> day, LocalDate datePickerValue){
        //create an AnchorPane for the root
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: linear-gradient(#fdcd3b, #ffed4b)");

        Stage stage = new Stage();

        Label lblForNav = new Label("Denuwara Menike Express Train");
        lblForNav.setStyle("-fx-font-size: 30;");
        lblForNav.setTextFill(Color.WHITE);
        HBox hBoxForNav = new HBox(lblForNav);
        hBoxForNav.setStyle("-fx-background-color: #F57F17; "); //Orange
        hBoxForNav.setPadding(new Insets(5, 80, 5, 65));
        hBoxForNav.setMinHeight(15.0);

        Button btnRedForBooked = new Button("Booked seats");
        btnRedForBooked.setStyle("-fx-background-color: #E74C3C;");
        btnRedForBooked.setTextFill(Color.WHITE);

        Button btnBlueForAvailable = new Button("Available seats");
        btnBlueForAvailable.setStyle("-fx-background-color: #2E86C1;");
        btnBlueForAvailable.setTextFill(Color.WHITE);

        Label lblDate = new Label("Date: ");
        lblDate.setStyle("-fx-font-size: 15;");
        Label lblSpecificDate = new Label(String.valueOf(datePickerValue));
        lblSpecificDate.setStyle("-fx-font-size: 15;");

        //create button for leave in this stage
        Button btnExit = new Button("Exit");
        btnExit.setPadding(new Insets(5, 20, 5, 20));
        btnExit.setStyle("-fx-background-color: #25D366; "); //Green
        btnExit.setTextFill(Color.WHITE);

        btnExit.setOnAction(event -> stage.close());

        HBox hBoxForBottum = new HBox(btnExit);
        hBoxForBottum.setPadding(new Insets(0, 10, 10, 450));

        HBox hBoxForDate = new HBox(10, lblDate, lblSpecificDate);
        HBox hBoxForColorLabels = new HBox(10, btnRedForBooked, btnBlueForAvailable);

        HBox hBoxForUpperUI = new HBox(180, hBoxForDate, hBoxForColorLabels);

        //Create list of buttons for show to seats
        Button[] buttons = new Button[SEATING_CAPACITY];

        //Create hBoxes for put buttons
        HBox[] hBoxesList1 = new HBox[11];
        HBox[] hBoxesList2 = new HBox[11];
        HBox[] hBoxesList3 = new HBox[10];
        HBox[] hBoxesList4 = new HBox[10];

        //create 42 buttons automatically
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button("Seat No. " + (i + 1));
            buttons[i].setTextFill(Color.WHITE);
            buttons[i].setStyle("-fx-background-color: #2E86C1; "); //Blue
            buttons[i].setMinWidth(75.0);


            int listElement = 0;
            for (int j = 0; j < seatNo.size(); j++) {
                if (String.valueOf(i).equals(seatNo.get(j))) {
                    if (day.get(j).equals(datePickerValue.toString())) {
                        buttons[i - 1].setStyle("-fx-background-color: #E74C3C; "); //Red
                        buttons[i - 1].setMinWidth(75.0);
                    }
                }
                listElement += 1;
            }
        }

        for (int i = 0; i < 11; i++) {
            //last row has only two buttons
            if (i == 10) {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
            } else {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
                hBoxesList3[i] = new HBox(buttons[i * 4 + 2]);
                hBoxesList4[i] = new HBox(buttons[i * 4 + 3]);
            }
        }

        //Create vBoxes and put hBoxes to there
        VBox vBoxLeft = new VBox(15, hBoxesList1);
        VBox vBoxMiddleLeft = new VBox(15, hBoxesList2);
        VBox vBoxMiddleRight = new VBox(15, hBoxesList3);
        VBox vBoxRight = new VBox(15, hBoxesList4);

        HBox hBoxes5 = new HBox(20, vBoxLeft, vBoxMiddleLeft);
        HBox hBoxes6 = new HBox(20, vBoxMiddleRight, vBoxRight);

        //create middle line space
        HBox hBoxForAllSeats = new HBox(150, hBoxes5, hBoxes6);
        hBoxForAllSeats.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        VBox vBox5 = new VBox(20, hBoxForUpperUI, hBoxForAllSeats, hBoxForBottum);

        //AnchorPane.setTopAnchor(vBox5, 20.0);
        AnchorPane.setLeftAnchor(vBox5, 20.0);
        AnchorPane.setRightAnchor(vBox5, 20.0);
        AnchorPane.setBottomAnchor(vBox5, 20.0);

        //Add elements to another pane
        anchorPane.getChildren().addAll(vBox5, hBoxForNav);

        stage.setScene(new Scene(anchorPane, 560, 640));
        stage.setTitle("View all Seats a specification date");
        stage.showAndWait();
    }

    //This function implemented for empty sheets UI.
    public void displayStageToE(List<String> seatNo, List<String> day) {

        //create an AnchorPane for the root
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: linear-gradient(#fdcd3b, #ffed4b)");

        Label lblForNav = new Label("Denuwara Menike Express Train");
        lblForNav.setStyle("-fx-font-size: 30;");
        lblForNav.setTextFill(Color.WHITE);
        HBox hBoxForNav = new HBox(lblForNav);
        hBoxForNav.setStyle("-fx-background-color: #F57F17; "); //Orange
        hBoxForNav.setPadding(new Insets(5, 80, 5, 65));
        hBoxForNav.setMinHeight(15.0);

        //ceate a label, button and a date picker
        Label lblDescription = new Label("View empty seats to specific date");
        lblDescription.setStyle("-fx-font-size: 15;");

        Label lblDate = new Label("Date: ");
        DatePicker datePicker = new DatePicker();

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                LocalDate futureDate = LocalDate.MAX.withYear(LocalDate.now().getYear());
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(futureDate) > 0);
            }
        });

        Label lblRequired = new Label();

        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color: #25D366; "); //Green
        btnSearch.setTextFill(Color.WHITE);

        HBox hBoxForDate = new HBox(30, lblDate, datePicker);
        HBox hBoxForAllignDateAndBtn = new HBox(40, hBoxForDate, btnSearch);
        VBox vBoxForDateAndBtn = new VBox(5, lblDescription, lblRequired, hBoxForAllignDateAndBtn);
        vBoxForDateAndBtn.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        //Create list of buttons for show to seats
        Button[] buttons = new Button[SEATING_CAPACITY];

        //Create hBoxes for put buttons
        HBox[] hBoxesList1 = new HBox[11];
        HBox[] hBoxesList2 = new HBox[11];
        HBox[] hBoxesList3 = new HBox[10];
        HBox[] hBoxesList4 = new HBox[10];

        btnSearch.setOnAction(event -> {
            //get date from the datepicker
            LocalDate datePickerValue = datePicker.getValue();
            try{
                viewEmptySeatSpacificDate(seatNo, day, datePickerValue);
                lblRequired.setText("");
            }
            catch (Exception e){
                lblRequired.setText("* Date is required.");
                lblRequired.setTextFill(Color.RED);
            }
        });

        //create 42 buttons automatically
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button("Seat No. " + (i + 1));
            buttons[i].setStyle("-fx-background-color: #BA68C8; "); //Purple
            buttons[i].setTextFill(Color.WHITE);
            buttons[i].setMinWidth(75.0);
        }

        //put buttons in correct position
        for (int i = 0; i < 11; i++) {
            //last row has only two buttons
            if (i == 10) {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
            } else {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
                hBoxesList3[i] = new HBox(buttons[i * 4 + 2]);
                hBoxesList4[i] = new HBox(buttons[i * 4 + 3]);
            }
        }

        //Create vBoxes and put hBoxes to there
        VBox vBoxLeft = new VBox(15, hBoxesList1);
        VBox vBoxMiddleLeft = new VBox(15, hBoxesList2);
        VBox vBoxMiddleRight = new VBox(15, hBoxesList3);
        VBox vBoxRight = new VBox(15, hBoxesList4);

        HBox hBoxes5 = new HBox(20, vBoxLeft, vBoxMiddleLeft);
        HBox hBoxes6 = new HBox(20, vBoxMiddleRight, vBoxRight);

        //create middle line space
        HBox hBoxForAllSeats = new HBox(150, hBoxes5, hBoxes6);
        hBoxForAllSeats.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        VBox vBox5 = new VBox(15, vBoxForDateAndBtn, hBoxForAllSeats);

        //AnchorPane.setTopAnchor(vBox5, 20.0);
        AnchorPane.setLeftAnchor(vBox5, 20.0);
        AnchorPane.setRightAnchor(vBox5, 20.0);
        AnchorPane.setBottomAnchor(vBox5, 20.0);

        //Add elements to another pane
        anchorPane.getChildren().addAll(vBox5, hBoxForNav);

        Stage stage = new Stage();
        stage.setScene(new Scene(anchorPane, 560, 650));
        stage.setTitle("View structure of empty seats");
        stage.showAndWait();
    }

    //This function implemented for View empty seats in specific date UI.
    // This massage call in stageForE method where user enter a date and click search button.
    public void viewEmptySeatSpacificDate(List<String> seatNo, List<String> day, LocalDate datePickerValue){
        //create an AnchorPane for the root
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: linear-gradient(#fdcd3b, #ffed4b)");

        Stage stage = new Stage();

        Label lblDate = new Label("Date: ");
        lblDate.setStyle("-fx-font-size: 15;");
        Label lblSpecificDate = new Label(String.valueOf(datePickerValue));
        lblSpecificDate.setStyle("-fx-font-size: 15;");

        HBox hBoxForDate = new HBox(10, lblDate, lblSpecificDate);

        //create button for leave in this stage
        Button btnExit = new Button("Exit");
        btnExit.setPadding(new Insets(5, 20, 5, 20));
        btnExit.setStyle("-fx-background-color: #25D366; "); //Green
        btnExit.setTextFill(Color.WHITE);

        HBox hBoxForBottum = new HBox(btnExit);
        hBoxForBottum.setPadding(new Insets(0, 10, 10, 450));

        //Create list of buttons for show to seats
        Button[] buttons = new Button[SEATING_CAPACITY];

        //Create hBoxes for put buttons
        HBox[] hBoxesList1 = new HBox[11];
        HBox[] hBoxesList2 = new HBox[11];
        HBox[] hBoxesList3 = new HBox[10];
        HBox[] hBoxesList4 = new HBox[10];

        //create 42 buttons automatically
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button("Seat No. " + (i + 1));
            buttons[i].setMinWidth(75.0);
            buttons[i].setTextFill(Color.WHITE);
            buttons[i].setStyle("-fx-background-color: #2E86C1; "); //Blue

            int listElement = 0;
            for (int j = 0; j < seatNo.size(); j++) {
                if (String.valueOf(i).equals(seatNo.get(j))) {
                    if (day.get(listElement).equals(datePickerValue.toString())) {
                        buttons[i - 1].setVisible(false);
                    }
                }
                listElement ++;
            }
        }

        for (int i = 0; i < 11; i++) {
            //last row has only two buttons
            if (i == 10) {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
            } else {
                hBoxesList1[i] = new HBox(buttons[i * 4]);
                hBoxesList2[i] = new HBox(buttons[i * 4 + 1]);
                hBoxesList3[i] = new HBox(buttons[i * 4 + 2]);
                hBoxesList4[i] = new HBox(buttons[i * 4 + 3]);
            }
        }

        btnExit.setOnAction(event -> stage.close());

        //Create vBoxes and put hBoxes to there
        VBox vBoxLeft = new VBox(20, hBoxesList1);
        VBox vBoxMiddleLeft = new VBox(20, hBoxesList2);
        VBox vBoxMiddleRight = new VBox(20, hBoxesList3);
        VBox vBoxRight = new VBox(20, hBoxesList4);

        HBox hBoxes5 = new HBox(20, vBoxLeft, vBoxMiddleLeft);
        HBox hBoxes6 = new HBox(20, vBoxMiddleRight, vBoxRight);

        //create middle line space
        HBox hBoxForAllSeats = new HBox(150, hBoxes5, hBoxes6);
        hBoxForAllSeats.setStyle("-fx-border-color: black; -fx-border-style: dashed; -fx-padding: 10;");

        VBox vBox5 = new VBox(20, hBoxForDate, hBoxForAllSeats, hBoxForBottum);

        AnchorPane.setTopAnchor(vBox5, 20.0);
        AnchorPane.setLeftAnchor(vBox5, 20.0);
        AnchorPane.setRightAnchor(vBox5, 20.0);
        AnchorPane.setBottomAnchor(vBox5, 20.0);

        //Add elements to another pane
        anchorPane.getChildren().add(vBox5);

        stage.setScene(new Scene(anchorPane, 560, 590));
        stage.setTitle("View all Seats a specification date");
        stage.showAndWait();
    }

    //This function implemented for delete booked seat where user enter 'D'.
    public void delete(List<String> seatNo, List<String> name, List<String> day) {
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the Seat Number which you want to delete your booking: ");
            int inputSeatNumber = scanner.nextInt();
            if (inputSeatNumber > 0 && inputSeatNumber < 43){
                System.out.print("Enter the date which you book this seat: (yyyy-mm-dd)");
                LocalDate inputDate = LocalDate.parse(scanner.next());
                System.out.println(inputDate);
                if(inputDate.getDayOfMonth() < 31 && inputDate.getMonthValue() < 12){
                    int indexOfArrays = -1;

                    for (int i = 0; i < day.size(); i++) {
                        if(day.get(i).equals(String.valueOf(inputDate)) && seatNo.get(i).equals(String.valueOf(inputSeatNumber))){
                            indexOfArrays = i;
                        }
                    }
                    if(indexOfArrays != -1){
                        System.out.println("Do you really want to delete this record? (y/n)");
                        String userInput = scanner.next().toUpperCase();

                        if(userInput.equals("Y")){
                            String tempDay = day.get(indexOfArrays);
                            String tempName = name.get(indexOfArrays);
                            String tempSeat = seatNo.get(indexOfArrays);
                            day.remove(indexOfArrays);
                            name.remove(indexOfArrays);
                            seatNo.remove(indexOfArrays);
                            System.out.println("Successfully deleted.");
                            System.out.println("You deleted day of " + tempDay + ", name \"" + tempName + "\" and seat number " + tempSeat);
                        }
                        else if(userInput.equals("N")){
                            System.out.println("You are never delete this record.");
                        }
                        else{
                            System.out.println("Sorry, You can enter only y and n. Please check your anser and try again.");
                        }
                    }
                    else{
                        System.out.println("Sorry. You aren't Booked the particular seat on specific date. ");
                    }
                }
                else {
                    System.out.println("Please enter valid date of month");
                }
            }
            else {
                System.out.println("Please enter valid seat number and try again..");
            }
        }

        catch (Exception e){
            System.out.println("Something went wrong. Please check your seat number and date.");
        }
    }

    //This function implemented for find a seat where user enter 'F'.
    public void find(List<String> seatNo, List<String> name, List<String> day) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name which is used to book a seat: ");
        String inputName = scanner.next().toLowerCase();
        int notFindName = 0;
        for (int i = 0; i < name.size(); i++) {

            if (name.get(i).equals(inputName)) {
                System.out.println("Your seat number is: " + seatNo.get(i));
                System.out.println("Date which you book that seats is: " + day.get(i));
                notFindName = 1;
            }
        }
        if(notFindName == 0){
            System.out.println("Sorry! Your name can't find. Please verify your entered name and try again..");
        }
    }

    //This function implemented for save data to database where user enter 'S'.
    public void saveData(List<String> seatNo, List<String> name, List<String> day){

        //Stop show response of the connect db
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        //connect mongodb via uri
        MongoClientURI uri = new MongoClientURI("mongodb+srv://ssdeshanayaka:99sanu99@cluster0-ebirv.mongodb.net/test");

        //create a mangoClient via my uri
        MongoClient mongoClient = new MongoClient(uri);

        //create a database called BookingTrainSeats
        MongoDatabase database = mongoClient.getDatabase("BookingTrainSeats");

        //create a collection called seats
        MongoCollection<Document> coll = database.getCollection("seats");

        Document docSeats = new Document("seatNo", (seatNo))
                .append("name", (name))
                .append("day", (day));

        if(coll.find() != null){
            coll.insertOne(docSeats);
        }
        else{
            System.out.println("Sorry, You have a collection already in database.");
        }

        System.out.println("Your data recorded successfully.");
    }

    //This function implemented for load data from database where user enter 'L'.
    public List<List<String>> loadData(){

        List<List<String>> seatDetails = new ArrayList<>();

        //Stop show response of the connect db
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        //connect mongodb via uri
        MongoClientURI uri = new MongoClientURI("mongodb+srv://ssdeshanayaka:99sanu99@cluster0-ebirv.mongodb.net/test");

        //create a mangoClient via my uri
        MongoClient mongoClient = new MongoClient(uri);

        //create a database called BookingTrainSeats
        MongoDatabase database = mongoClient.getDatabase("BookingTrainSeats");

        //create a collection called seats
        MongoCollection<Document> coll = database.getCollection("seats");

        MongoCursor<Document> mongoCursor = coll.find().iterator();
        while (mongoCursor.hasNext()){
            Document docSeatDetails = mongoCursor.next();

            List<String> seatNo = (List<String>) docSeatDetails.get("seatNo");
            List<String> name = (List<String>) docSeatDetails.get("name");
            List<String> day = (List<String>) docSeatDetails.get("day");

            seatDetails.add(seatNo);
            seatDetails.add(name);
            seatDetails.add(day);
        }
        System.out.println("Your data loaded successfully.");

        return seatDetails;
    }

    //This function implemented for order customers name where user enter 'O'.
    public void createNamesInOrder(List<String> name) {

        String[] sortName = new String[name.size()];
        for (int i = 0; i < name.size(); i++) {
             sortName[i] = name.get(i);
        }

        //make names to alphabetically order
        String temp;
        for (int i = 0; i < name.size(); i++) {

            for (int j = 0; j < name.size() - 1 - i; j++) {

                char firstElement = sortName[j].charAt(0);
                char nextElement = sortName[j + 1].charAt(0);

                if (firstElement > nextElement) {
                    temp = sortName[j];
                    sortName[j] = sortName[j + 1];
                    sortName[j + 1] = temp;
                }
            }
        }

        //print sorting names
        for (String s : sortName) {
            System.out.println(s);
        }
    }
}
