package subedi.address;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import subedi.address.model.Person;
import subedi.address.util.PersonListWrapper;
import subedi.address.view.BirthdayStatisticsController;
import subedi.address.view.PersonEditDialogController;
import subedi.address.view.PersonOverviewController;
import subedi.address.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private File file;
    
    //The data as an observable list of Persons
    private ObservableList<Person> personData = FXCollections.observableArrayList();
    
    /**
     * Constructor that initializes the file name on where the data would be storing
     * 
     * @throws IOException 
     */
    public MainApp() throws IOException {
    	//gets the users home folder
    	String DefaultFolder =new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    	//System.out.println(DefaultFolder);
    	Path path = Paths.get(DefaultFolder + File.separator+"addressApp"+File.separator+"addressApp.xml");
    	Files.createDirectories(path.getParent());
    	try {
            Files.createFile(path);
            file = path.toFile();
        } catch (FileAlreadyExistsException e) {
            //System.out.println("already exists. Opening the old file " + e.getMessage());
            file = path.toFile();
        }
    	System.out.println(path.toString());

    }
    
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        
        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/addressIcon.png"));

        initRootLayout();

        showPersonOverview();
	}
	
    
    /**
     * Returns the data as an observable list of Persons. 
     * @return list of person
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }
	
	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        /*try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    	
    	 try {
    	        // Load root layout from fxml file.
    	        FXMLLoader loader = new FXMLLoader();
    	        loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
    	        rootLayout = (BorderPane) loader.load();

    	        // Show the scene containing the root layout.
    	        Scene scene = new Scene(rootLayout);
    	        primaryStage.setScene(scene);

    	        // Give the controller access to the main app.
    	        RootLayoutController controller = loader.getController();
    	        controller.setMainApp(this);

    	        primaryStage.show();
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }

    	    // Try to load last opened person file if exists.
    	    File file = getPersonFilePath();
    	    if (file != null) {
    	        loadPersonDataFromFile(file);
    	    }
    }
    
    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
            
            // Give the controller access to the main app.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     * 
     * @param person - the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Person's Information");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Returns the main stage.
     * @return the stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Method to get the file to load for the first time on every run
	 * If it is in the specified directory otherwise it will be created
	 * 
	 * @return file that contains the person data
	 */
	public File getPersonFilePath() {
	    /*Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }*/
		return this.file;
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setPersonFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Update the stage title.
	        //primaryStage.setTitle("AddressApp - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp");
	    }
	}
	
	
	/**
	 * Loads person data from the specified file. The current person data will
	 * be replaced.
	 * 
	 * @param file
	 */
	public void loadPersonDataFromFile(File file) {
		if (file.exists()) {
			loadPersonFile(file);
		} else {
			loadPersonFile(this.file);
		}
	}

	//common method to load file with user choice or self generated
	private void loadPersonFile(File personFile) {
		//System.out.println(personFile.getPath());
		try {
	        JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
	        Unmarshaller um = context.createUnmarshaller();

	        // Reading XML from the file and unmarshalling.
	        PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(personFile);

	        personData.clear();
	        personData.addAll(wrapper.getPersons());

	        // Save the file path to the registry.
	        setPersonFilePath(personFile);

	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Information");
	        alert.setHeaderText("Creating Data file!");
	        alert.setContentText("Your data file wll be created on:\n" + personFile.getPath() 
	        		+"\n\nIf you want to load your own data file, please open using file menu");
	        alert.showAndWait();
	    }
		
	}

	/**
	 * Saves the current person data to the specified file i.e. created
	 * in the constructor.
	 * 
	 * @param file
	 */
	public void savePersonDataToFile(File file) {
	    try {
	        JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // Wrapping our person data.
	        PersonListWrapper wrapper = new PersonListWrapper();
	        wrapper.setPersons(personData);

	        // Marshalling and saving XML to the file.
	        m.marshal(wrapper, file);

	        // Save the file path to the registry.
	        setPersonFilePath(file);
	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}
	
	/**
	 * Opens a dialog to show birthday statistics.
	 */
	public void showBirthdayStatistics() {
	    try {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Birthday Statistics");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the persons into the controller.
	        BirthdayStatisticsController controller = loader.getController();
	        controller.setPersonData(personData);

	        dialogStage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
