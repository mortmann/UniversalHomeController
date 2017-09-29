package com.stupro.uhc.util;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField {
	int maxLength = 10;
	int maxNumber = -1;
	boolean isFloat = false;
	
	public NumberTextField(int maxLength) {
		super();
		this.maxLength = maxLength; 
	}
	public NumberTextField(int maxLength,boolean isFloat) {
		super();
		this.maxLength = maxLength; 
		this.isFloat = isFloat;
	}
	public NumberTextField(boolean isFloat) {
		super();
		this.isFloat = isFloat;
	}
	public NumberTextField(int maxLength, int maxNumber) {
		super();
		this.maxLength = maxLength; 
		this.maxNumber = maxNumber;
	}
	public NumberTextField(String s,int maxLength, int maxNumber) {
		super(s);
		this.maxLength = maxLength; 
		this.maxNumber = maxNumber;
	}
	public NumberTextField() {
		super();
	}
	public NumberTextField(String s) {
		super(s);
	}
	public NumberTextField(String s,int maxLength) {
		super(s);
		this.maxLength = maxLength; 
	}
	@Override
	public void replaceText(int start, int end, String text) {
		if (validate(text)) {
		 if (this.getMaxLength() <= 0 || getMaxLength() ==-1) {
	            // Default behavior, in case of no max length

	            super.replaceText(start, end, text);
	        }
	        else {
	            // Get the text in the textfield, before the user enters something
	            String currentText = this.getText() == null ? "" : this.getText();

	            // Compute the text that should normally be in the textfield now
	            String finalText = currentText.substring(0, start) + text + currentText.substring(end);
	            
	            // If the max length is not excedeed
	            int numberOfexceedingCharacters = finalText.length() - this.getMaxLength();
	            if (numberOfexceedingCharacters <= 0) {
	                // Normal behavior
	                super.replaceText(start, end, text);
	            }
	            else {
	                // Otherwise, cut the the text that was going to be inserted
	                String cutInsertedText = text.substring(
	                        0, 
	                        text.length() - numberOfexceedingCharacters
	                );
	                // And replace this text
	                super.replaceText(start, end, cutInsertedText);
	            }
	            // Limit the Textfield Number-Value when maximum is given
	            if(maxNumber!=-1 && GetFloatValue()>maxNumber){
	            	setText(maxNumber+"");
	            }
	        }

		}

	}

	@Override
	public void replaceSelection(String text) {
		if (validate(text)) {
			super.replaceSelection(text);
		}
		
		
	}
	
	public int GetIntValue(){
		if(this.getText()==null||this.getText()==""||this.getText().isEmpty()){
			return 0;
		}
		return Integer.parseInt(this.getText());
	}
	
	public float GetFloatValue(){
		if(this.getText()==null||this.getText()==""||this.getText().isEmpty()){
			return 0;
		}
		return Float.parseFloat(this.getText());
	}
	private int getMaxLength() {
		return maxLength;
	}
	private boolean validate(String text) {
		if(isFloat){
			if(text.matches("[.]")){
				if(this.getText().contains(".")){
					return false;
				}
			}
			return text.matches("[0-9]*[.]?[0-9]*");
		}
		return text.matches("[0-9]*");
	}
}