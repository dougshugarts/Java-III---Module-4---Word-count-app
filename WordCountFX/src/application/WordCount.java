package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class WordCount implements Runnable{
	
	private String fileName;
	private String convertedFile;
	private Integer wordCount = 0;
	private File wordFile;
	private Alert alert = new Alert(AlertType.INFORMATION);
	
	// constructor takes file for processing
	public WordCount (File file, Alert alert) {
		
		this.setFile(file);
		this.setFileName(file.getName());
		this.alert = alert;
	}
	
	// sets and gets
	private void setFileName(String fileName) {
		
		this.fileName = fileName;
	}
	
	public String getFileName() {
		
		return this.fileName;
	}
	
	private void setWordCount(Integer count) {
		
		this.wordCount = count;
	}
	
	public synchronized Integer getWordCount() {
		
		return this.wordCount;
	}
	
	private void setFile(File file) {
		
		this.wordFile = file;
	}
	
	private synchronized void processString() {
				
		try {
			Path path = Path.of(this.wordFile.getPath());
			this.convertedFile =  Files.readString(path);
			String wordCount[] = this.splitString(this.convertedFile);
			this.setWordCount(wordCount.length);
			
		} catch (IOException e) {
			this.alert.setContentText("Error processing text file.");
			this.alert.show();
		}
	}
	
	private synchronized String[] splitString (String s) {
		
		return s.split("\\s+");
	}
	
	//runnable to get word count
	@Override
	public void run() {
	
		this.processString();
	}
}
