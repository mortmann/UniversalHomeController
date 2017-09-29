package com.stupro.uhc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.controlsfx.glyphfont.FontAwesome;

import com.stupro.uhc.GUI;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class NewFloor extends ButtonOver {
	FileChooser fileChooser;
	TextField nameField;
	ImageView preview;
	StackPane sp;
	
	String[] fileExtensions = {"*.png", "*.jpg","*.jpeg"};
	private String fileLocation;
	private Label errorLabel;
	
	public NewFloor(){
		super("NewFloor",FontAwesome.Glyph.PLUS_SQUARE);
	}
	protected void createPopOverContent(){
		popOver.setTitle("New Floor"); 
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Images","*.png", "*.jpg","*.jpeg"));
		
		fileChooser.setTitle("Choose Image File");
		Button showFileChooser = new Button("Select Image!");
		
		showFileChooser.setOnAction(x->{
			File f = fileChooser.showOpenDialog(GUI.Instance.getStage());
			addImage(f.getAbsolutePath());
		});
		sp = new StackPane();
		sp.setStyle("-fx-background-color:grey;");
		preview = new ImageView();

		sp.setMinSize(300, 300);
		sp.setMaxSize(310, 310);

		sp.getChildren().add(preview);
		
		sp.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                mouseDragOver(event);
            }
        });
 
		sp.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                mouseDragDropped(event);
            }
        });
 
		sp.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
            	sp.setStyle("-fx-border-color: #C6C6C6;");
            }
        });
		
		Label lb = new Label("Drag&Drop Picture here:");
		sp.getChildren().add(lb);
		nameField = new TextField();
		vbox.getChildren().add(CreateHBox(new Label("Name: "),nameField));
		vbox.getChildren().add(sp);
		vbox.getChildren().add(CreateHBox(new Label(""),showFileChooser));
		errorLabel = new Label("");
		vbox.getChildren().add(CreateHBox(new Label(""),errorLabel));
		Button create = new Button("Create");
		create.setOnAction(x->{
			try {
				CreateFloor();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		vbox.getChildren().add(CreateHBox(new Label(""),create));

		popOver.setContentNode(vbox);
	}
	
    private void CreateFloor() throws FileNotFoundException {
    	Image i = new Image(new FileInputStream(fileLocation));
    	if(i.getHeight()<500 || i.getWidth()<500){
    		errorLabel.setText("Error! Image must be at least 500x500!");
    		errorLabel.setTextFill(Color.RED);
    		return;
    	}
    	String name = nameField.getText();
    	if(name.trim().isEmpty() || name.length()<3){
    		errorLabel.setText("Error! Name must be at least 3 characters long!");
    		errorLabel.setTextFill(Color.RED);
    		return;
    	}
    	errorLabel.setText("");
    	popOver.hide();
    	GUI.Instance.getMyHouse().createNewFloor(fileLocation,name);
	}
	void addImage(String string){
        Image img = null;
		try {
			img = new Image(new FileInputStream(string));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
		fileLocation = string;
        preview.setImage(img);
        if(img.getWidth()>img.getHeight()){
            double scale = img.getHeight()/img.getWidth();
            preview.fitHeightProperty().unbind();
            preview.setFitHeight(scale*sp.getWidth());
            preview.fitWidthProperty().bind(sp.widthProperty());
        } else {
            double scale = img.getWidth()/img.getHeight();
            preview.fitWidthProperty().unbind();
            preview.setFitWidth(scale*sp.getWidth());
            preview.fitHeightProperty().bind(sp.heightProperty());
        }
    }
    
    private void mouseDragDropped(final DragEvent e) {
        final Dragboard db = e.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            final File file = db.getFiles().get(0);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                   addImage(file.getAbsolutePath());
                }
            });
        }
        e.setDropCompleted(success);
        e.consume();
    }
 
    private  void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();
 
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");
 
        if (db.hasFiles()) {
            if (isAccepted) {
                sp.setStyle("-fx-border-color: red;"
              + "-fx-border-width: 5;"
              + "-fx-background-color: #C6C6C6;"
              + "-fx-border-style: solid;");
                e.acceptTransferModes(TransferMode.COPY);
            }
        } else {
            e.consume();
        }
    }
	@Override
	protected void onHiding() {
		
	}
	@Override
	protected void onClose() {
		
	}
	@Override
	protected void onShowing() {
		// TODO Auto-generated method stub
		
	}
}
