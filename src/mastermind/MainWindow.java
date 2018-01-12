package mastermind;

import java.io.IOException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Window;

/**
 * Class that represents the Apache Pivot application for the mastermind
 * game.
 * <p>
 * This is the main class for the application. The <code>startup</code> method
 * is called when the application starts. Its primary job is to manage the 
 * Pivot UI.
 * 
 * @author Jon
 * @see <a href="http://pivot.apache.org/">Apache Pivot</a>
 *
 */
public class MainWindow implements Application
{

    //---------------- Private variables -----------------------------------
	
    private Window mainWindow = null;
    private static boolean diagMode;
    
    private static final int MAIN_BUTTON_HEIGHT = 90;
    private static final String DIAG_PROPERTY_KEY = "diag";
	
	/*
	 * BXML variables.
	 */
    @BXML private Label titleLabel = null;
    @BXML private PushButton gameOptionsButton = null;
    @BXML private PushButton newGameButton = null;
    
    /**
     * Class constructor.
     */
    public MainWindow ()
    {
    	diagMode = false;
    }
	
    //---------------- Getters and setters ---------------------------------
    
    /**
     * Gets the diagnostic mode flag.
     * 
     * @return diagnostic mode flag
     */
    public static boolean getDiagMode ()
    {
    	return diagMode;
    }

    //---------------- Public methods --------------------------------------

    /**
     * Starts up the application when it's launched.
     * 
     * @param display display object for managing windows
     * @param properties properties passed to the application
	 * @throws IOException If an error occurs trying to read the BXML file;
	 * or an error occurs trying to read or write the game options.
	 * @throws SerializationException If an error occurs trying to 
	 * deserialize the BXML file.
     */
    @Override
    public void startup (Display display, Map<String, String> properties)
    		throws IOException, SerializationException
    {
    	
    	/*
    	 * Set diag mode if the property is set. This lets us cheat and see the puzzle, for diagnosing
    	 * any analysis or clue problems.
    	 */
    	String diagProperty = properties.get(DIAG_PROPERTY_KEY);
    	if (diagProperty != null && diagProperty.equals("true"))
    	{
    		diagMode = true;
    	}
    	
    	/*
    	 * Get the BXML information for the main window.
    	 */
		initializeBxmlVariables();
        
		/*
		 * Listener to handle the game options button press.
		 */
		gameOptionsButton.getButtonPressListeners().add(new ButtonPressListener() 
        {
            @Override
            public void buttonPressed(Button button) 
            {            	
            	try
				{
            		OptionsWindow optionsWindowHandler = new OptionsWindow();
            		optionsWindowHandler.displayOptions(display);
				} 
            	catch (IOException | SerializationException e)
				{
					throw new RuntimeException(e);
				}
            }
        });
		
        /*
         * Listener to handle the new game button press.
         */
        newGameButton.getButtonPressListeners().add(new ButtonPressListener() 
        {
            @Override
            public void buttonPressed(Button button) 
            {            	
            	try
				{
            		PuzzleWindow puzzleWindowHandler = new PuzzleWindow();
            		puzzleWindowHandler.displayPuzzle(display);
				} 
            	catch (IOException | SerializationException e)
				{
					throw new RuntimeException(e);
				}
            }
        });
        
        /*
         * Flesh out the widgets.
         */
        titleLabel.setText("Mastermind Game");
        
        gameOptionsButton.setButtonData("Game Options");
        gameOptionsButton.setPreferredHeight(MAIN_BUTTON_HEIGHT);
        
        newGameButton.setButtonData("New Game");
        newGameButton.setPreferredHeight(MAIN_BUTTON_HEIGHT);
        
        //---------------- Start of Initialization -----------------------------
		
		/*
		 * Get the game options singleton object.
		 */
		Options options = Options.getInstance();
		
		/*
		 * Read the game options if they exist.
		 */
		options.readJavaPreferences();
		
		/*
		 * Set the window title.
		 */
		mainWindow.setTitle("Mastermind");
        
        /*
         * Open the main window.
         */
        mainWindow.open(display);
    }

    /**
     * Shuts down the application.
     * 
     * @param optional indicates if the shutdown is optional
     * @return <code>true</code> if further shutdown is optional, otherwise
     * <code>false</code>
     */
    @Override
    public boolean shutdown (boolean optional) 
    {
        if (mainWindow != null) 
        {
            mainWindow.close();
        }

        return false;
    }

    /**
     * Suspends the application (this method is not used).
     */
    @Override
    public void suspend () 
    {
    }

    /**
     * Resumes the application (this method is not used).
     */
    @Override
    public void resume () 
    {
    }

    /**
     * Specifies the main application entry point.
     * 
     * @param args program arguments
     */
    public static void main (String[] args) 
    {
    	
    	/*
    	 * This method instantiates our class, then calls our startup() method.
    	 */
        DesktopApplicationContext.main(MainWindow.class, args);
    }

    //---------------- Private methods -------------------------------------
    
    /*
     * Initialize BXML variables.
     */
    private void initializeBxmlVariables () 
    		throws IOException, SerializationException
    {
    	
        BXMLSerializer windowSerializer = new BXMLSerializer();
        mainWindow = 
        		(Window)windowSerializer.readObject(getClass().getResource("mainWindow.bxml"));

        titleLabel = 
        		(Label)windowSerializer.getNamespace().get("titleLabel");
        gameOptionsButton = 
        		(PushButton)windowSerializer.getNamespace().get("gameOptionsButton");
		newGameButton = 
        		(PushButton)windowSerializer.getNamespace().get("newGameButton");
    }
}