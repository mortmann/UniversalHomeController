package com.stupro.uhc.ui;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow.AnchorLocation;

public abstract class ButtonOver extends StackPane {

	protected ToggleButton toggle;
	protected PopOver popOver;
	protected VBox vbox;
	public ButtonOver(String buttonText, org.controlsfx.glyphfont.FontAwesome.Glyph glyph){
		reference=this;
		Glyph fontG = new FontAwesome().create(glyph);
		fontG.setFontSize(20);
		toggle = new ToggleButton(buttonText,fontG);
		getChildren().addAll(toggle);
		toggle.setOnAction(x->show());
		toggle.setPrefWidth(150);
		createPopOver();
	}
	protected ButtonOver(){}
	protected abstract void createPopOverContent();
	protected abstract void onHiding();
	protected abstract void onClose();
	protected ButtonOver reference;
	
	protected void show() {
		if(popOver==null){
			return;
		}
		if(popOver.isShowing()){
			popOver.hide();
			return;
		}
		onShowing();
		popOver.show(toggle);
	}
	
	protected void createPopOver(){
		popOver = new PopOver();
		popOver.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		popOver.setArrowLocation(ArrowLocation.TOP_CENTER);
		popOver.setOnHiding(x->{
			toggle.setSelected(false);
			onHiding();
		});
		popOver.setOnCloseRequest(x->{
			toggle.setSelected(false);
			onClose();
		});
		
		
		popOver.setAutoHide(false);
		
		vbox = new VBox();
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(5);
		
		createPopOverContent();
	}
	protected abstract void onShowing();
	
	protected Node CreateHBox(Node n1, Node n2){
//		HBox hbox = new HBox();
//		hbox.getChildren().addAll(n1,n2);
//		hbox.setSpacing(10);
//		hbox.
		GridPane g = new GridPane();
		g.add(n1, 0, 0);
		g.add(n2, 1, 0);
		GridPane.setHalignment(n2, HPos.CENTER);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(40);
		g.getColumnConstraints().add(column1); 

		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(60);
		g.getColumnConstraints().add(column2); 
		return g;
	}

	
	
}
