package com.stupro.uhc.ui;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
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
	protected HBox CreateHBox(Node n1, Node n2){
		HBox hbox = new HBox();
		hbox.getChildren().addAll(n1,n2);
		hbox.setSpacing(10);
		return hbox;
	}

	
	
}
