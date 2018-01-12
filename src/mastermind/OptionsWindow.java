package mastermind;

import java.io.IOException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.SpinnerSelectionListener;
import org.apache.pivot.wtk.Window;

/**
 * Class that handles the puzzle options window.
 * 
 * @author Jon
 *
 */
public class OptionsWindow
{

    //---------------- Private variables -----------------------------------
	
    private Window optionsWindow = null;
    private Options options = null;
    
    private boolean numColorsUpdated;
    private boolean numHolesUpdated;
    private boolean numGuessesUpdated;
    private boolean dupsAllowedUpdated;
    private boolean blanksAllowedUpdated;
	
	/*
	 * BXML variables.
	 */
	@BXML private Label numColorsLabel = null;
	@BXML private Spinner numColorsSpinner = null;
	@BXML private Label numHolesLabel = null;
	@BXML private Spinner numHolesSpinner = null;
	@BXML private Label numGuessesLabel = null;
	@BXML private Spinner numGuessesSpinner = null;
	@BXML private Label dupsAllowedLabel = null;
	@BXML private Checkbox dupsAllowedCheckbox = null;
	@BXML private Label blanksAllowedLabel = null;
	@BXML private Checkbox blanksAllowedCheckbox = null;
	@BXML private PushButton optionsDoneButton = null;
	
	/**
	 * Class constructor.
	 */
	public OptionsWindow ()
	{

		/*
		 * Get the game options singleton object.
		 */
		options = Options.getInstance();
	}

    //---------------- Public methods --------------------------------------

	/**
	 * Displays the puzzle options in a new window.
	 * 
	 * @param display display object for managing windows
	 * @throws IOException If an error occurs trying to read the BXML file.
	 * @throws SerializationException If an error occurs trying to 
	 * deserialize the BXML file.
	 */
    public void displayOptions (Display display) 
    		throws IOException, SerializationException
    {
    	
    	/*
    	 * Get the BXML information for the puzzle options window.
    	 */
		initializeBxmlVariables();
		
		/*
		 * Set up the labels and done button.
		 */
		numColorsLabel.setText("Select the number of colors used.");
		numHolesLabel.setText("Select the number of holes used in the puzzle.");
		numGuessesLabel.setText("Select the number of guesses allowed.");
		dupsAllowedLabel.setText("Indicate if duplicate colors are allowed.");
		blanksAllowedLabel.setText("Indicate if blank spaces are allowed.");
		optionsDoneButton.setButtonData("Done");
		
		/*
		 * Set up the number of colors spinner.
		 */
        List<String> numColorsArray = new ArrayList<String>();
        numColorsArray.add(Options.NumColors.SIX.getNumColorsDisplayValue());
        numColorsArray.add(Options.NumColors.EIGHT.getNumColorsDisplayValue());
        numColorsSpinner.setSpinnerData(numColorsArray);
        numColorsSpinner.setCircular(true);
        
        int index = numColorsArray.indexOf(Integer.toString(options.getNumColors()));
        numColorsSpinner.setSelectedIndex(index);
        
        /*
         * Listener to handle the number of colors spinner.
         */
        numColorsSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener()
        {
        	@Override
        	public void selectedIndexChanged(Spinner spinner, int previousSelectedIndex)
        	{
        		numColorsUpdated = true;
        	}

			@Override
			public void selectedItemChanged(Spinner spinner, Object previousSelectedItem)
			{
			}
        });
		
		/*
		 * Set up the number of holes spinner.
		 */
        List<String> numHolesArray = new ArrayList<String>();
        numHolesArray.add(Options.NumHoles.FOUR.getNumHolesDisplayValue());
        numHolesArray.add(Options.NumHoles.FIVE.getNumHolesDisplayValue());
        numHolesSpinner.setSpinnerData(numHolesArray);
        numHolesSpinner.setCircular(true);
        
        index = numHolesArray.indexOf(Integer.toString(options.getNumHoles()));
        numHolesSpinner.setSelectedIndex(index);
        
        /*
         * Listener to handle the number of holes spinner.
         */
        numHolesSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener()
        {
        	@Override
        	public void selectedIndexChanged(Spinner spinner, int previousSelectedIndex)
        	{
        		numHolesUpdated = true;
        	}

			@Override
			public void selectedItemChanged(Spinner spinner, Object previousSelectedItem)
			{
			}
        });
		
		/*
		 * Set up the number of guesses spinner.
		 */
        List<String> numGuessesArray = new ArrayList<String>();
        numGuessesArray.add(Options.NumGuesses.EIGHT.getNumGuessesDisplayValue());
        numGuessesArray.add(Options.NumGuesses.TEN.getNumGuessesDisplayValue());
        numGuessesArray.add(Options.NumGuesses.TWELVE.getNumGuessesDisplayValue());
        numGuessesSpinner.setSpinnerData(numGuessesArray);
        numGuessesSpinner.setCircular(true);
        
        index = numGuessesArray.indexOf(Integer.toString(options.getNumGuesses()));
        numGuessesSpinner.setSelectedIndex(index);
        
        /*
         * Listener to handle the number of guesses spinner.
         */
        numGuessesSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener()
        {
        	@Override
        	public void selectedIndexChanged(Spinner spinner, int previousSelectedIndex)
        	{
        		numGuessesUpdated = true;
        	}

			@Override
			public void selectedItemChanged(Spinner spinner, Object previousSelectedItem)
			{
			}
        });
        
        /*
         * Set up the duplicates allowed checkbox.
         */
        dupsAllowedCheckbox.setSelected(options.getDupsAllowed());
        
        /*
         * Listener to handle the duplicates allowed checkbox.
         */
        dupsAllowedCheckbox.getButtonPressListeners().add(new ButtonPressListener()
        {
            @Override
            public void buttonPressed(Button button)
            {
            	dupsAllowedUpdated = true;
            }
        });
        
        /*
         * Set up the blanks allowed checkbox.
         */
        blanksAllowedCheckbox.setSelected(options.getBlanksAllowed());
        
        /*
         * Listener to handle the blanks allowed checkbox.
         */
        blanksAllowedCheckbox.getButtonPressListeners().add(new ButtonPressListener()
        {
            @Override
            public void buttonPressed(Button button)
            {
            	blanksAllowedUpdated = true;
            }
        });
        
        /*
         * Listener to handle the done button press.
         */
        optionsDoneButton.getButtonPressListeners().add(new ButtonPressListener() 
        {
            @Override
            public void buttonPressed(Button button)
            {
            	
            	/*
            	 * Handle a change to the number of colors option.
            	 */
            	if (numColorsUpdated == true)
            	{
            		String numColorsOption = (String) numColorsSpinner.getSelectedItem();
            		int numColorsValue = Integer.valueOf(numColorsOption);
            		
            		options.setNumColors(numColorsValue);
            		options.saveJavaPreferenceInt(Options.JAVA_PREFS_KEY_NUM_COLORS, numColorsValue);
            	}
            	
            	/*
            	 * Handle a change to the number of holes option.
            	 */
            	if (numHolesUpdated == true)
            	{
            		String numHolesOption = (String) numHolesSpinner.getSelectedItem();
            		int numHolesValue = Integer.valueOf(numHolesOption);
            		
            		options.setNumHoles(numHolesValue);
            		options.saveJavaPreferenceInt(Options.JAVA_PREFS_KEY_NUM_HOLES, numHolesValue);
            	}
            	
            	/*
            	 * Handle a change to the number of guesses option.
            	 */
            	if (numGuessesUpdated == true)
            	{
            		String numGuessesOption = (String) numGuessesSpinner.getSelectedItem();
            		int numGuessesValue = Integer.valueOf(numGuessesOption);
            		
            		options.setNumGuesses(numGuessesValue);
            		options.saveJavaPreferenceInt(Options.JAVA_PREFS_KEY_NUM_GUESSES, numGuessesValue);
            	}
            	
            	/*
            	 * Handle a change to the duplicates allowed option.
            	 */
            	if (dupsAllowedUpdated == true)
            	{
            		boolean dupsAllowedOption = dupsAllowedCheckbox.isSelected();
            		
            		options.setDupsAllowed(dupsAllowedOption);
            		options.saveJavaPreferenceBoolean(Options.JAVA_PREFS_KEY_DUPS_ALLOWED, dupsAllowedOption);
            	}
            	
            	/*
            	 * Handle a change to the blanks allowed option.
            	 */
            	if (blanksAllowedUpdated == true)
            	{
            		boolean blanksAllowedOption = blanksAllowedCheckbox.isSelected();
            		
            		options.setBlanksAllowed(blanksAllowedOption);
            		options.saveJavaPreferenceBoolean(Options.JAVA_PREFS_KEY_BLANKS_ALLOWED, blanksAllowedOption);
            	}
            	
            	/*
            	 * Close the options window.
            	 */
            	optionsWindow.close();
            }
        });
		
		/*
		 * Set the window title.
		 */
		optionsWindow.setTitle("Puzzle Options");

		/*
		 * Open the puzzle window.
		 */
    	optionsWindow.open(display);
    	
    }

    //---------------- Private methods -------------------------------------
    
    /*
     * Initialize BXML variables.
     */
    private void initializeBxmlVariables () 
    		throws IOException, SerializationException
    {
		
        BXMLSerializer windowSerializer = new BXMLSerializer();
        optionsWindow = (Window)windowSerializer.
        		readObject(getClass().getResource("optionsWindow.bxml"));

        numColorsLabel = 
        		(Label)windowSerializer.getNamespace().get("numColorsLabel");
        numColorsSpinner = 
        		(Spinner)windowSerializer.getNamespace().get("numColorsSpinner");
        numHolesLabel = 
        		(Label)windowSerializer.getNamespace().get("numHolesLabel");
        numHolesSpinner = 
        		(Spinner)windowSerializer.getNamespace().get("numHolesSpinner");
        numGuessesLabel = 
        		(Label)windowSerializer.getNamespace().get("numGuessesLabel");
        numGuessesSpinner = 
        		(Spinner)windowSerializer.getNamespace().get("numGuessesSpinner");
        dupsAllowedLabel = 
        		(Label)windowSerializer.getNamespace().get("dupsAllowedLabel");
        dupsAllowedCheckbox = 
        		(Checkbox)windowSerializer.getNamespace().get("dupsAllowedCheckbox");
        blanksAllowedLabel = 
        		(Label)windowSerializer.getNamespace().get("blanksAllowedLabel");
        blanksAllowedCheckbox = 
        		(Checkbox)windowSerializer.getNamespace().get("blanksAllowedCheckbox");
        optionsDoneButton = 
        		(PushButton)windowSerializer.getNamespace().get("optionsDoneButton");
    }
}
