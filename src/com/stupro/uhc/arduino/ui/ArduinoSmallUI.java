package com.stupro.uhc.arduino.ui;

import org.controlsfx.control.PopOver;
import com.stupro.uhc.Floor;
import com.stupro.uhc.GUI;
import com.stupro.uhc.arduino.Arduino;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ArduinoSmallUI extends StackPane {

	double width = 50;
	double height = 50;
	protected double orgSceneX;
	protected double orgSceneY;
	protected double orgTranslateX;
	protected double orgTranslateY;
	protected ArduinoSmallUI reference;
	protected boolean mouseOutside;
	protected Label newLabel;
	boolean isNew = true;
	protected PopOver pop;
	Arduino myArduino;

	public ArduinoSmallUI(Arduino arduino) {
		myArduino = arduino;
		reference = this;

		Image i = new Image("/images/arduino_background.png");
		ImageView background = new ImageView(i);
		Text t = new Text();
		t.setText("LED");
		t.setTextAlignment(TextAlignment.CENTER);

		newLabel = new Label("NEW");
		newLabel.setTextFill(Color.WHITE);
		newLabel.setStyle("-fx-background-image: url('images/marker_background.png');");
		t.minHeight(height);
		t.minWidth(width);
		setMinSize(width, height);
		setMaxSize(width, height);
		background.fitWidthProperty().bind(this.widthProperty());
		background.fitHeightProperty().bind(this.heightProperty());
		getChildren().addAll(background, t, newLabel);
		StackPane.setAlignment(newLabel, Pos.TOP_RIGHT);
		setOnMousePressed(onMousePressedEventHandler);
		setOnMouseDragged(onMouseDraggedEventHandler);
		setOnMouseDragOver(onMouseDraggedOverEventHandler);

		setOnMouseClicked(onDoubleClickEventHandler);
		setOnMouseExited(onMouseExitEventHandler);
		setOnMouseEntered(onMouseEnterEventHandler);

	}

	EventHandler<MouseEvent> onDoubleClickEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
				if (isNew) {
					newLabel.setVisible(false);
				}
				if (mouseEvent.getClickCount() == 2) {
					GUI.Instance.changeCenterToArduino(myArduino);
				}
			}
		}

	};

	EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			if (isNew) {
				newLabel.setVisible(false);
			}
			reference.setTranslateZ(10);
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgTranslateX = reference.getTranslateX();
			orgTranslateY = reference.getTranslateY();
		}
	};
	EventHandler<MouseEvent> onMouseDraggedOverEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			if (isNew) {
				isNew = false;
				// Util.changeParent(reference,
				// GUI.Instance.GetHouseCenterPane());
			}
		}
	};
	EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			t.consume();
			Point2D point = (reference.getParent().sceneToLocal(new Point2D(t.getSceneX(), t.getSceneY())));
			if (Floor.outSideBounds(point)) {
				return;
			}
			reference.setTranslateX(point.getX() - width / 2);
			reference.setTranslateY(point.getY() - height / 2);
		}
	};
	EventHandler<MouseEvent> onMouseExitEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			if (isNew) {
				return;
			}
			// mouseOutside = true;
		}
	};
	EventHandler<MouseEvent> onMouseEnterEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			mouseOutside = false;
		}
	};

}
