package mastermind;

import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.DragSource;
import org.apache.pivot.wtk.DropAction;
import org.apache.pivot.wtk.DropTarget;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.LocalManifest;
import org.apache.pivot.wtk.Manifest;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.Visual;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.media.Image;

/**
 * Class that handles the puzzle window.
 * 
 * @author Jon
 *
 */
public class PuzzleWindow
{

    //---------------- Private variables -----------------------------------
	
    private Window puzzleWindow = null;
	private PushButton guessButton = null;
	private PushButton quitButton = null;
	private Label messageLabel = null;
	
	Map<String, Integer> pegBackgroundGray;
	Map<String, Integer> pegBackgroundBlue;
	Map<String, Integer> pegBackgroundRed;
	
    private List<Integer> puzzle = null;
    private List<List<PushButton>> guessButtons = null;
    private List<List<PushButton>> clueButtons = null;
    private int guessRowIndex;
    
    private int numColors;
    private int numHoles;
    private int numGuesses;
    private boolean dupsAllowed;
    private boolean blanksAllowed;
    
    private int numColorButtons;
    
    /*
     * Puzzle attributes.
     */
    private static enum PuzzleAttributes
    {
    	
    	/*
    	 * This attribute is set on the palette of color buttons that the user drags to the guess
    	 * rows. Since the push buttons themselves just have an image, we need a way to associate 
    	 * an actual value with the palette buttons, which is then used to compare against the
    	 * puzzle values.
    	 */
    	COLOR_VALUE("ColorValue");
    	
    	private String attributeString;
    	
    	/*
    	 * Constructor.
    	 */
    	private PuzzleAttributes (String value)
    	{
    		attributeString = value;
    	}
    	
    	/**
    	 * Gets the <code>enum</code> value as a string.
    	 * 
    	 * @return enum value as a string
    	 */
    	public String toString ()
    	{
    		return attributeString;
    	}
    }
	
	/*
	 * BXML variables.
	 */
	@BXML private Label actionLabel = null;
	@BXML private BoxPane buttonsBoxPane = null;
	@BXML private TablePane puzzleTablePane = null;
	
	/**
	 * Class constructor.
	 */
	public PuzzleWindow ()
	{
		
		/*
		 * Get the game options singleton object.
		 */
		Options options = Options.getInstance();
		
		/*
		 * Get the game options.
		 */
		numColors = options.getNumColors();
		numHoles = options.getNumHoles();
		numGuesses = options.getNumGuesses();
		dupsAllowed = options.getDupsAllowed();
		blanksAllowed = options.getBlanksAllowed();

        numColorButtons = numColors + ((blanksAllowed == true) ? 1 : 0);

		/*
		 * Initialize the rows of guess buttons.
		 */
		guessButtons = new ArrayList<List<PushButton>>(numGuesses);
		for (int i = 0; i < numGuesses; i++)
		{
			List<PushButton> guessRow = new ArrayList<PushButton>();
			guessButtons.add(guessRow);
		}

		/*
		 * Initialize the rows of clue buttons.
		 */
		clueButtons = new ArrayList<List<PushButton>>(numGuesses);
		for (int i = 0; i < numGuesses; i++)
		{
			List<PushButton> guessRow = new ArrayList<PushButton>();
			clueButtons.add(guessRow);
		}
		
		/*
		 * Initialize the guess row index. This gets incremented before the puzzle window is displayed,
		 * so it starts at -1.
		 */
		guessRowIndex = -1;
		
		/*
		 * Create styles for peg background colors.
		 */
		pegBackgroundGray = new HashMap<String, Integer>();
		pegBackgroundGray.put("backgroundColor", 10);
		pegBackgroundBlue = new HashMap<String, Integer>();
		pegBackgroundBlue.put("backgroundColor", 17);
		pegBackgroundRed = new HashMap<String, Integer>();
		pegBackgroundRed.put("backgroundColor", 22);
		
		/*
		 * Generate a new puzzle.
		 */
		generatePuzzle();
	}

    //---------------- Public methods --------------------------------------

	/**
	 * Displays the puzzle in a new window.
	 * 
	 * @param display display object for managing windows
	 * @throws IOException If an error occurs trying to read the BXML file.
	 * @throws SerializationException If an error occurs trying to 
	 * deserialize the BXML file.
	 */
    public void displayPuzzle (Display display) 
    		throws IOException, SerializationException
    {
    	
    	/*
    	 * Get the BXML information for the query playlists window.
    	 */
		initializeBxmlVariables();
		
		/*
		 * Create the appropriate number of table columns to represent the number of holes in use.
		 * The column for the clues is predefined in the bxml.
		 */
		TablePane.ColumnSequence columns = puzzleTablePane.getColumns();
		for (int i = 0; i < numHoles; i++)
		{
			columns.insert(new TablePane.Column(), 0);
		}

		/*
		 * Define a drag source for the palette of color buttons that the user drags to the guess rows.
		 */
        DragSource imageDragSource = new DragSource()
        {
            private Image image = null;
            private Point offset = null;
            private LocalManifest content = null;
            private Integer colorAttribute = null;

            /*
             * Called when the user starts to drag a color button. We grab information about the button
             * being dragged, which is later used when it's dropped.
             */
            @Override
            public boolean beginDrag(Component comp, int x, int y)
            {
            	PushButton colorButton = (PushButton)comp;
                this.image = (Image) colorButton.getButtonData();
                this.colorAttribute = (Integer) colorButton.getAttribute(PuzzleAttributes.COLOR_VALUE);

                if (this.image != null)
                {
                    this.content = new LocalManifest();
                    this.content.putImage(this.image);
                    this.content.putValue(PuzzleAttributes.COLOR_VALUE.toString(), this.colorAttribute);
                    this.offset = new Point(
                    		x - (colorButton.getWidth()  - this.image.getWidth())  / 2,
                    		y - (colorButton.getHeight() - this.image.getHeight()) / 2);
                }

                return (this.image != null);
            }

            @Override
            public void endDrag(Component comp, DropAction dropAction)
            {
                this.image = null;
                this.offset = null;
                this.content = null;
                this.colorAttribute = null;
            }

            @Override
            public boolean isNative()
            {
                return false;
            }

            @Override
            public LocalManifest getContent()
            {
                return this.content;
            }

            @Override
            public Visual getRepresentation()
            {
                return this.image;
            }

            @Override
            public Point getOffset()
            {
                return this.offset;
            }

            /*
             * We only support a copy operation for the drag and drop.
             */
            @Override
            public int getSupportedDropActions()
            {
                return DropAction.COPY.getMask();
            }
        };
		
		/*
		 * Create the palette of color buttons.
		 */
		for (int i = 1; i <= numColorButtons; i++)
		{
    		
    		/* 
    		 * Get the image for the color value.
    		 */
            Image image = getPegImageForValue(i);
            
            /*
             * Create the button.
             */
			PushButton button = new PushButton();
            button.setButtonData(image);
            button.setDragSource(imageDragSource);
            button.setAttribute(PuzzleAttributes.COLOR_VALUE, i);
            
            /*
             * Mouse click listener for the drag and drop buttons. This is an alternative means
             * to drag and drop, in order to set the colors being guessed.
             */
            button.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener.Adapter()
            {
                @Override
                public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count)
                {
                	
                	/*
                	 * Get the image and color value attribute for the clicked button.
                	 */
                	PushButton colorButton = (PushButton) component;
                	Image image = (Image) colorButton.getButtonData();
                	Integer colorAttribute = 
                			(Integer) colorButton.getAttribute(PuzzleAttributes.COLOR_VALUE);
                	
                	/*
                	 * Find the first guess button that does not have a color value attribute.
                	 * That button is the first one in the guess row that has not been set.
                	 * Set its data from the color button that was clicked.
                	 */
                    List<PushButton> guessRow = guessButtons.get(guessRowIndex);
            		for (int i = 0; i < numHoles; i++)
            		{
            			PushButton guessButton = guessRow.get(i);
            			
            			if (guessButton.getAttribute(PuzzleAttributes.COLOR_VALUE) != null)
            			{
            				continue;
            			}
            			
            			guessButton.setButtonData(image);
            			guessButton.setAttribute(PuzzleAttributes.COLOR_VALUE, colorAttribute);
            			break;
                	}
                	
                	return false;
                }            	
            });
            
            buttonsBoxPane.add(button);
		}
    	
    	/*
    	 * Create a styles object for the button colors.
    	 */
    	Map<String, Object> styles = new HashMap<String, Object>();
    	styles.put("color", 4);
    	styles.put("backgroundColor", 17);
		
		/*
		 * Create the game control buttons.
		 */
        guessButton = new PushButton();
        guessButton.setStyles(styles);
        buttonsBoxPane.add(guessButton);
        
        quitButton = new PushButton();
        quitButton.setStyles(styles);
        buttonsBoxPane.add(quitButton);
		
		/*
		 * Listener to handle the guess button (pressed when the user wants to evaluate a guess).
		 */
        guessButton.getButtonPressListeners().add(new ButtonPressListener()
        {
            @Override
            public void buttonPressed(Button button)
            {
            	
            	/*
            	 * Initialize the puzzle guess array.
            	 */
            	List<Integer> guess = new ArrayList<Integer>(numHoles);
            	
            	/*
            	 * Get the current guess row.
            	 */
            	List<PushButton> guessRow = guessButtons.get(guessRowIndex);
            	
            	/*
            	 * Gather the guess integer values.
            	 */
        		Iterator<PushButton> guessRowIter = guessRow.iterator();
        		while (guessRowIter.hasNext())
        		{
                	PushButton guessButton = guessRowIter.next();
                	guess.add((Integer)guessButton.getAttribute(PuzzleAttributes.COLOR_VALUE));
            	}
        		
        		/*
        		 * Evaluate the guess. We get back 2 integers:
        		 * 
        		 * - the number of black clues
        		 * - the number of white clues
        		 */
            	List<Integer> result = evaluateGuess(guess);
            	Integer numBlack = result.get(0);
            	Integer numWhite = result.get(1);

        		/*
        		 * Create an array of indices to match all the clue pegs.
        		 */
        		List<Integer> indices = new ArrayList<Integer>(numHoles);
        		for (int i = 0; i < numHoles; i++)
        		{
        			indices.add(i);
        		}
            	
            	/*
            	 * All black means the user wins.
            	 */
            	if (numBlack == numHoles)
            	{
            		placeClues(numBlack, "black-clue.png", indices);
            		uncoverPuzzle();
            		messageLabel.setText("     You win!");
            	}
            	else
            	{

            		/*
            		 * Randomly place all the black clue pegs.
            		 */
            		indices = placeClues(numBlack, "black-clue.png", indices);

            		/*
            		 * Randomly place all the white clue pegs.
            		 */
            		placeClues(numWhite, "white-clue.png", indices);
            		
            		/*
            		 * Increment the guess row.
            		 */
            		incrementGuessRow();
            		
            		/*
            		 * If we ran out of rows, the user loses.
            		 */
            		if (guessRowIndex >= numGuesses)
            		{
                		uncoverPuzzle();
                		messageLabel.setText("     You lose!");
            		}
            		
            		/*
            		 * Otherwise, set the drop target on the new row of guess buttons.
            		 */
            		else
            		{
            			setDropTarget();
            		}
            		
            		/*
            		 * Repaint the window.
            		 */
            		puzzleWindow.repaint();
            	}
            }
        });

        /*
         * Listener to handle the quit button.
         */
        quitButton.getButtonPressListeners().add(new ButtonPressListener()
        {
            @Override
            public void buttonPressed(Button button)
            {
            	puzzleWindow.close();
            }
        	
        });
		
		/*
		 * Flesh out the widgets.
		 */
		guessButton.setButtonData("Guess");
		quitButton.setButtonData("Quit");
		actionLabel.setText("Click color buttons, or drag them onto the current row.");
		
		/*
		 * Set the window title.
		 */
		puzzleWindow.setTitle("Puzzle Me This");

		/*
		 * Create the puzzle row.
		 */
		TablePane.Row newRow = createPuzzleRow();
		puzzleTablePane.getRows().add(newRow);
		
		/*
		 * Create the rows for user guesses.
		 */
		for (int i = 0; i < numGuesses; i++)
		{
			newRow = createGuessRow(i);
			puzzleTablePane.getRows().add(newRow);
		}
		
		/*
		 * Initialize the guess row to the first row. This gets updated as the user plays the game.
		 */
		incrementGuessRow();
		
		/*
		 * Set the drop target on the current row of guess buttons.
		 */
		setDropTarget();

		/*
		 * Open the puzzle window.
		 */
    	puzzleWindow.open(display);
    }

    //---------------- Private methods -------------------------------------
    
    /*
     * Generate a new puzzle to be solved. The puzzle consists of an array of integer values that
     * are mapped to colored pegs for display purposes.
     */
    private void generatePuzzle ()
    {
		
    	/*
    	 * If duplicates are not allowed, we need to create a puzzle with each peg representing a 
    	 * unique color. So create an array of allowed colors and pass it to the puzzle values
    	 * generator.
    	 */
		if (dupsAllowed == false)
		{
			List<Integer> uniqueColors = new ArrayList<Integer>(numColorButtons);
			for (Options.Colors color : Options.Colors.values())
			{
				int colorValue = color.getColorValue();
				uniqueColors.add(colorValue);
			}
			puzzle = generatePuzzleValues(uniqueColors);
		}
		
		/*
		 * Duplicates are allowed, so we don't need the allowed colors array.
		 */
		else
		{
			puzzle = generatePuzzleValues(null);
		}
    }
    
    /*
     * Generate the array of puzzle integer values.
     */
    private List<Integer> generatePuzzleValues (List<Integer> uniqueColors)
    {
		
		/*
		 * Initialize the pseudo random number generator.
		 */
		Random rand = new Random();
		
		/*
		 * Create the integer array.
		 */
    	List<Integer> puzzleValues = new ArrayList<Integer>(numHoles);
		
    	/*
    	 * Fill each value of the integer array.
    	 */
		for (int i = 0; i < numHoles; i++)
		{
			
			/*
			 * We need to use unique colors for each puzzle value.
			 */
			if (uniqueColors != null)
			{
				
				/*
				 * Loop until we find a good value to add.
				 */
				boolean valueAdded = false;
				while (valueAdded == false)
				{

					/*
					 * Try to use a random color value.
					 */
					int color = rand.nextInt(numColorButtons) + 1;
					
					/*
					 * If the randomly selected color has already been used, try again.
					 */
					if (uniqueColors.indexOf(color) == -1)
					{
						continue;
					}
					
					/*
					 * Add the randomly selected color, and remove it from the available choices
					 * for the next round.
					 */
					uniqueColors.remove(color);
					puzzleValues.add(color);
					valueAdded = true;
				}
			}
			
			/*
			 * Duplicate colors are allowed, so just add a randomly selected color.
			 */
			else
			{
				int color = rand.nextInt(numColorButtons) + 1;
				puzzleValues.add(color);
			}
		}
    	
    	return puzzleValues;
    }
	
    /*
     * Create the puzzle row for display.
     */
	private TablePane.Row createPuzzleRow () 
	{

		/*
		 * Create a new puzzle table row, consisting of an array of pushbuttons that use colored 
		 * peg icons.
		 */
    	TablePane.Row newRow = new TablePane.Row();
    	
    	/*
    	 * Walk through all the puzzle values.
    	 */
    	Iterator<Integer> puzzleIter = puzzle.iterator();
    	while (puzzleIter.hasNext())
    	{
    		
    		/* 
    		 * Get the image for the selected puzzle value.
    		 */
    		Integer puzzleValue = puzzleIter.next();

    		/*
    		 * If diag mode is active, then display the real puzzle. Otherwise cover it up.
    		 */
            Image image;
    		if (MainWindow.getDiagMode() == true)
    		{
                image = getPegImageForValue(puzzleValue);
    		}
    		else
    		{
                image = getIconImage("x-button.png");
    		}

            /*
             * Add the button to represent this peg.
             */
        	PushButton button = new PushButton();
        	button.setStyles(pegBackgroundRed);
        	
        	button.setButtonData(image);
        	newRow.add(button);
    	}
    	
    	/*
    	 * Create a label to display the win/lose message. First, create a font.
    	 */
    	Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    	
    	/*
    	 * Create a styles object for the label.
    	 */
    	Map<String, Object> styles = new HashMap<String, Object>();
    	styles.put("font", font);
    	styles.put("color", 22);
    	
    	/*
    	 * Create the label and set the styles. The text is set when the game is over.
    	 */
    	messageLabel = new Label();
    	messageLabel.setStyles(styles);
    	
    	newRow.add(messageLabel);
    	
    	return newRow;
	}
	
	/*
	 * Create a user guess row for display.
	 */
	private TablePane.Row createGuessRow (int rowIndex)
	{

		/*
		 * Create a new guess table row, consisting of an array of pushbuttons that will use colored 
		 * peg icons to represent the user's choices, but are initially placeholders (gray pegs).
		 */
    	TablePane.Row newRow = new TablePane.Row();
		
		/* 
		 * Get the image URL for the gray peg icon.
		 */
        Image image = getIconImage("gray-button.png");
		
        /*
         * Create the row of guess buttons that the user will fill in.
         */
        List<PushButton> guessRow = guessButtons.get(rowIndex);
		for (int i = 0; i < numHoles; i++)
		{
			PushButton button = new PushButton();
			guessRow.add(button);
			
        	button.setButtonData(image);
        	
        	newRow.add(button);
    	}
    	
    	/*
    	 * Create a styles object for padding the clue box.
    	 */
    	Map<String, Object> styles = new HashMap<String, Object>();
    	styles.put("padding", 5);
    	
    	/*
    	 * Create the clue area.
    	 */
    	BoxPane clueBox = new BoxPane();
    	clueBox.setStyles(styles);
		
		/* 
		 * Get the image URL for the gray "x" clue icon.
		 */
        image = getIconImage("x-clue.png");
		
        /*
         * Create the row of clue buttons.
         */
        List<PushButton> clueRow = clueButtons.get(rowIndex);
    	for (int i = 0; i < numHoles; i++)
    	{
    		PushButton button = new PushButton();
    		clueRow.add(button);
    		
    		button.setButtonData(image);
    		clueBox.add(button);
    	}
    	
    	newRow.add(clueBox);
    	
    	return newRow;
	}
	
	/*
	 * Uncover the puzzle, either because the user won or the game is over.
	 */
	private void uncoverPuzzle ()
	{
		
		/*
		 * Access the puzzle row.
		 */
		TablePane.RowSequence rows = puzzleTablePane.getRows();
		TablePane.Row puzzleRow = rows.get(0);
    	
    	/*
    	 * Walk through all the puzzle values.
    	 */
    	for (int i = 0; i < numHoles; i++)
    	{
    		Integer puzzleValue = puzzle.get(i);
    		
    		/* 
    		 * Get the image for the selected puzzle value.
    		 */
            Image image = getPegImageForValue(puzzleValue);

            /*
             * Update the button with the appropriate color and background.
             */
        	PushButton button = (PushButton) puzzleRow.get(i);
        	button.setButtonData(image);
        	button.setStyles(pegBackgroundGray);
    	}
	}
	
	/*
	 * Set the drop target for the current row of guess buttons.
	 */
	private void setDropTarget ()
	{

        /*
         * Define a drop target.
         */
        DropTarget imageDropTarget = new DropTarget()
        {
            @Override
            public DropAction dragEnter(Component comp, Manifest dragContent,
                int supportedDropActions, DropAction userDropAction)
            {
                DropAction dropAction = null;

                if (dragContent.containsImage()
                    && DropAction.COPY.isSelected(supportedDropActions))
                {
                    dropAction = DropAction.COPY;
                }

                return dropAction;
            }

            @Override
            public void dragExit(Component comp)
            {
            }

            @Override
            public DropAction dragMove(Component comp, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction)
            {
                return (dragContent.containsImage() ? DropAction.COPY : null);
            }

            @Override
            public DropAction userDropActionChange(Component comp, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction)
            {
                return (dragContent.containsImage() ? DropAction.COPY : null);
            }

            /*
             * Called when the user releases the mouse button over a drop target. It sets the
             * color and color attribute value from the dragged color button.
             */
            @Override
            public DropAction drop(Component comp, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction)
            {
                DropAction dropAction = null;

            	PushButton colorButton = (PushButton)comp;
                if (dragContent.containsImage())
                {
                    try
                    {
                    	colorButton.setButtonData(dragContent.getImage());
                    	colorButton.setAttribute(PuzzleAttributes.COLOR_VALUE, 
                    			dragContent.getValue(PuzzleAttributes.COLOR_VALUE.toString()));
                        dropAction = DropAction.COPY;
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }

                return dropAction;
            }
        };
        
        /*
         * Set the above drop target for all buttons on the current guess row.
         */
        List<PushButton> guessRow = guessButtons.get(guessRowIndex);
		for (int i = 0; i < numHoles; i++)
		{
			PushButton button = guessRow.get(i);
        	button.setDropTarget(imageDropTarget);
    	}
	}
	
	/*
	 * Increment the guess row. This involves setting the background color of the current and new
	 * rows, and incrementing the guess row index.
	 */
	private void incrementGuessRow ()
	{
		
		/*
		 * Set the current guess row to gray background. Also remove the drop target, since we don't 
		 * want to inadvertently change a previous clue. This is bypassed for the first row.
		 */
		List<PushButton> guessRow;
		if (guessRowIndex >= 0)
		{
			guessRow = guessButtons.get(guessRowIndex);
			for (int i = 0; i < numHoles; i++)
			{
				PushButton button = guessRow.get(i);
				button.setStyles(pegBackgroundGray);

	        	button.setDropTarget(null);
			}
		}
		
		/*
		 * Bump the guess row index to the next row.
		 */
		guessRowIndex++;
		
		/*
		 * Set the new guess row to blue background. This is bypassed for the last row.
		 */
		if (guessRowIndex < numGuesses)
		{
			guessRow = guessButtons.get(guessRowIndex);
			for (int i = 0; i < numHoles; i++)
			{
				PushButton button = guessRow.get(i);
				button.setStyles(pegBackgroundBlue);
			}
		}
	}
	
	/*
	 * Get the colored peg image for a given color value.
	 */
	private Image getPegImageForValue (int value)
	{
		String icon = null;
		switch (value)
		{
		case 1:
			icon = "white-button.png";
			break;

		case 2:
			icon = "black-button.png";
			break;

		case 3:
			icon = "red-button.png";
			break;

		case 4:
			icon = "green-button.png";
			break;

		case 5:
			icon = "blue-button.png";
			break;

		case 6:
			icon = "yellow-button.png";
			break;

		/*
		 * Special case. If the number of colors is 6, a value of 7 means that blanks are allowed, so
		 * we need a blank button. All other cases of number of colors and blanks allowed work out 
		 * just fine.
		 */
		case 7:
			if (numColors == Options.NumColors.SIX.getNumColorsValue())
			{
				icon = "blank-button.png";
			}
			else
			{
				icon = "tan-button.png";
			}
			break;

		case 8:
			icon = "pink-button.png";
			break;

		case 9:
			icon = "blank-button.png";
			break;
			
		default:
			throw new RuntimeException(
					"Unexpected color value '" + value + "', [" + numColors + ", " + blanksAllowed + "]");
		}
		
		/* 
		 * Get the image for the selected colored peg icon.
		 */
        return getIconImage(icon);
	}
	
	/*
	 * Get the colored peg image for a given image name.
	 */
	private Image getIconImage (String icon)
	{
		
		/* 
		 * Get the image URL for the input colored peg icon.
		 */
        URL imageURL;
		try
		{
			imageURL = Paths.get("src/mastermind/" + icon).toUri().toURL();
		}
		catch (MalformedURLException e)
		{
            throw new RuntimeException(e);
		}

        /*
         *  If the image has not been added to the resource cache yet, add it.
         */
        Image image = (Image)ApplicationContext.getResourceCache().get(imageURL);

        if (image == null)
        {
            try
            {
                image = Image.load(imageURL);
            }
            catch (TaskExecutionException e)
            {
                throw new RuntimeException(e);
            }

            ApplicationContext.getResourceCache().put(imageURL, image);
        }
		
        return image;
	}
	
	/*
	 * Evaluate the uesr's guess. We return a list of two integers:
	 * 
	 * - the number of black clue pegs needed
	 * - the number of white clue pegs needed
	 */
	private List<Integer> evaluateGuess (List<Integer> guess)
	{
		List<Integer> result = new ArrayList<Integer>(2);
		int numBlack = 0;
		int numWhite = 0;
		
		/*
		 * This is annoying, but Pivot's ArrayList does not support copy or clone, so we have to
		 * copy the input guess and the puzzle arrays manually.
		 * 
		 * We need copies because we remove items from the arrays as we go along.
		 */
		List<Integer> workingPuzzle = new ArrayList<Integer>(numHoles);
		Iterator<Integer> puzzleIter = puzzle.iterator();
		while (puzzleIter.hasNext())
		{
			workingPuzzle.add(puzzleIter.next());
		}

		List<Integer> workingGuess = new ArrayList<Integer>(numHoles);
		Iterator<Integer> guessIter = guess.iterator();
		while (guessIter.hasNext())
		{
			workingGuess.add(guessIter.next());
		}
		
		/*
		 * Determine the number of black clue pegs needed by a simple comparison of each puzzle
		 * position with the corresponding guess position. Remove any matches found from both
		 * working arrays, because we don't want to also count such matches when we're looking for 
		 * white clue pegs.
		 */
		for (int i = 0; i < numHoles; i++)
		{
			if (puzzle.get(i) == guess.get(i))
			{
				
				/*
				 * Tricky stuff alert: since we're removing items from the working arrays, we can't
				 * just use i as an index, since it won't be correct after the first removal. So
				 * use i minus the number of black matches previously found instead.
				 */
				workingGuess.remove(i - numBlack, 1);
				workingPuzzle.remove(i - numBlack, 1);
				numBlack++;
			}
		}

		/*
		 * Determine the number of white clue pegs needed by checking each remaining guess position
		 * against all remaining puzzle positions. Remove each match found from the puzzle, so we
		 * don't count anything more than once.
		 */
		Iterator<Integer> workingGuessIter = workingGuess.iterator();
		while (workingGuessIter.hasNext())
		{
			int index;
			if ((index = workingPuzzle.indexOf(workingGuessIter.next())) >= 0)
			{
				numWhite++;
				workingPuzzle.remove(index, 1);
			}
		}
		
		/*
		 * Build the result list.
		 */
		result.add(numBlack);
		result.add(numWhite);
		return result;
	}
	
	/*
	 * Place black or white clue pegs randomly, based on the user's guess.
	 */
	private List<Integer> placeClues (Integer numClues, String imageName, List<Integer> indices)
	{
		
		/*
		 * Initialize the pseudo random number generator.
		 */
		Random rand = new Random();
		
		/*
		 * Get the appropriate clue button image.
		 */
        Image clueImage = getIconImage(imageName);
		
		/*
		 * Randomly place the appropriate clues.
		 */
        List<PushButton> clueRow = clueButtons.get(guessRowIndex);
		for (int i = 0; i < numClues; i++)
		{
			
			/*
			 * The input indices variable initially contains the values 0 through numHoles - 1.
			 * Get a random index into these indices.
			 */
			int indicesIndex = rand.nextInt(indices.getLength());
			
			/*
			 * Get the actual indices value that we selected. This is the index into the list of
			 * clue buttons.
			 */
			int clueIndex = indices.get(indicesIndex);
			
			/*
			 * Now remove the indices index from the indices for the next round.
			 */
			indices.remove(indicesIndex, 1);
			
			/*
			 * Update the selected clue button with the black or white clue peg.
			 */
			PushButton clueButton = clueRow.get(clueIndex);
			clueButton.setButtonData(clueImage);
			clueRow.update(clueIndex, clueButton);
		}
		
		return indices;
	}
    
    /*
     * Initialize BXML variables.
     */
    private void initializeBxmlVariables () 
    		throws IOException, SerializationException
    {
		
        BXMLSerializer windowSerializer = new BXMLSerializer();
        puzzleWindow = (Window)windowSerializer.
        		readObject(getClass().getResource("puzzleWindow.bxml"));

        actionLabel = 
        		(Label)windowSerializer.getNamespace().get("actionLabel");
        buttonsBoxPane = 
        		(BoxPane)windowSerializer.getNamespace().get("buttonsBoxPane");
		puzzleTablePane = 
        		(TablePane)windowSerializer.getNamespace().get("puzzleTablePane");
    }
}
