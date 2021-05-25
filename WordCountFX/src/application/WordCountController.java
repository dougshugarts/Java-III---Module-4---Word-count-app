package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class WordCountController {
	
	private ExecutorService exc;
	private WordCount wc;
	
	// list of existing files
	private List<File> validatedFiles = new ArrayList<>();
	// list of files to be processed
	private List<File> files = new ArrayList<>();
	
	private FileChooser fileChooser;
	private Alert alert = new Alert(AlertType.INFORMATION);
	
	private boolean isValid;
	
	// GUI elements
	@FXML
	private AnchorPane anchorPane;
	
	// TableView to display WordCount objects
    @FXML
    private TableView <WordCount> tableView;
    
    // TableView columns to display WordCount variable types
    @FXML
    private TableColumn <WordCount, String> column1;
    @FXML
    private TableColumn <WordCount, Integer> column2;
    
	@FXML
	private Button addFiles;
	@FXML
	private Button getCount;
	
	@FXML
	private void initialize() {
		
		//set welcome text; set column fields in GUI to match variables from WordCount object
		this.tableView.setPlaceholder(new Label("File names and word counts will appear here."));
		this.column1.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		this.column2.setCellValueFactory(new PropertyValueFactory<>("wordCount"));
	}
	// method to select files
	@FXML
	private void addFiles(ActionEvent event) throws NullPointerException {
				
		this.fileChooser = new FileChooser();
		
		Window window = this.anchorPane.getScene().getWindow();
		this.fileChooser.setTitle("Load Files");
		this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text file", "*.txt"));
		
		// in case user clicks 'Cancel' button ....
		try {
		// get list of selected files
		List <File> temp = this.fileChooser.showOpenMultipleDialog(window);
		
		// validate selected files
		for (File file : temp) {
		
			this.isValid = true;
			
			for (File existingFile: this.validatedFiles) {
			
					if (file.equals(existingFile)) {
							
							this.isValid = false;
							this.alert.setContentText(file.getName() + " has already been added.  Other selected files (if any) will be imported.");
							this.alert.show();
							}// if clause
					}
			if (this.isValid) {		
				this.files.add(file);
			}	
		}
		this.validatedFiles.addAll(this.files);
		this.isValid = true;
		}
			// catch exception that follows click on 'Cancel' button, where list of files is 'null'
			catch (NullPointerException e) {
				// do nothing
			}
	    }

	// method to create WordCount objects for processing
	// on concurrent threads via ExecutorService
	@FXML
	private void getCount(ActionEvent event) {
		
		if (this.files.size() ==0) {
			this.alert.setContentText("All files have been processed. Please add more files.");
			this.alert.show();
			}
		else {
		// initialize ExecutorService
		this.exc = Executors.newCachedThreadPool();
		
		for (File file : this.files) {
			
			this.fileHandler(file);
		}
		// clear files list and end ExecutorService
		this.files.clear();
		this.exc.shutdown();
	    }
	}
	// synchronized method to create WordCount objects
	// and populate the GUI
	private synchronized void fileHandler(File f) {
		
		this.wc = new WordCount(f, this.alert);
		this.exc.execute(wc);
		this.tableView.getItems().add(wc);
	}
}
