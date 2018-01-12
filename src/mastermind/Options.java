package mastermind;

/**
 * Class that represents game options. This is a singleton class.
 * 
 * @author Jon
 *
 */
public final class Options
{

    //---------------- Singleton implementation ----------------------------
	
	/*
	 * Singleton class instance variable.
	 */
	private static Options instance = null;
	
	/**
	 * Gets the singleton instance.
	 * 
	 * @return singleton class instance
	 */
	public static Options getInstance ()
	{
		if (instance == null)
		{
			instance = new Options();
		}
		
		return instance;
	}
	
    //---------------- Public variables ------------------------------------
	
	/**
	 * Java preferences keys representing the game options.
	 */
	public static final String JAVA_PREFS_KEY_NUM_COLORS = "NUM_COLORS";
	public static final String JAVA_PREFS_KEY_NUM_HOLES = "NUM_HOLES";
	public static final String JAVA_PREFS_KEY_NUM_GUESSES = "NUM_GUESSES";
	public static final String JAVA_PREFS_KEY_DUPS_ALLOWED = "DUPS_ALLOWED";
	public static final String JAVA_PREFS_KEY_BLANKS_ALLOWED = "BLANKS_ALLOWED";

    //---------------- Class variables -------------------------------------
	
	/*
	 * Variables for the actual options we want to serialize.
	 * 
	 * - number of game colors
	 * - number of holes in the puzzle
	 * - number of guesses allowed
	 * - if duplicate colors are allowed
	 * - if blanks (no peg) are allowed
	 */
	private int numColors;
	private int numHoles;
	private int numGuesses;
	private boolean dupsAllowed;
	private boolean blanksAllowed;
	
	/*
	 * Defaults.
	 */
	private static final int DEFAULT_NUM_COLORS = NumColors.EIGHT.getNumColorsValue();
	private static final int DEFAULT_NUM_HOLES = NumHoles.FIVE.getNumHolesValue();
	private static final int DEFAULT_NUM_GUESSES = NumGuesses.TWELVE.getNumGuessesValue();
	private static final boolean DEFAULT_DUPS_ALLOWED = true;
	private static final boolean DEFAULT_BLANKS_ALLOWED = false;
	
	/**
	 * The number of colors used.
	 */
	public enum NumColors
	{
		
		/**
		 * Six colors used.
		 */
		SIX(6),
		
		/**
		 * Eight colors used.
		 */
		EIGHT(8);
		
		private int numColorsValue;
		
		/*
		 * Constructor.
		 */
		private NumColors (int value)
		{
			numColorsValue = value;
		}
		
		/**
		 * Gets the number of colors value.
		 * 
		 * @return number of colors value
		 */
		public int getNumColorsValue ()
		{
			return numColorsValue;
		}
		
		/**
		 * Gets the number of colors display value.
		 * 
		 * @return number of colors display value
		 */
		public String getNumColorsDisplayValue ()
		{
			return Integer.toString(numColorsValue);
		}
	}
	
	/**
	 * The number of holes used.
	 */
	public enum NumHoles
	{
		
		/**
		 * Six holes used.
		 */
		FOUR(4),
		
		/**
		 * Five holes used.
		 */
		FIVE(5);
		
		private int numHolesValue;
		
		/*
		 * Constructor.
		 */
		private NumHoles (int value)
		{
			numHolesValue = value;
		}
		
		/**
		 * Gets the number of holes value.
		 * 
		 * @return number of holes value
		 */
		public int getNumHolesValue ()
		{
			return numHolesValue;
		}
		
		/**
		 * Gets the number of holes display value.
		 * 
		 * @return number of holes display value
		 */
		public String getNumHolesDisplayValue ()
		{
			return Integer.toString(numHolesValue);
		}
	}
	
	/**
	 * The number of guesses allowed.
	 */
	public enum NumGuesses
	{
		
		/**
		 * Eight guesses allowed.
		 */
		EIGHT(8),
		
		/**
		 * Ten guesses allowed.
		 */
		TEN(10),
		
		/**
		 * Twelve guesses allowed.
		 */
		TWELVE(12);
		
		private int numGuessesValue;
		
		/*
		 * Constructor.
		 */
		private NumGuesses (int value)
		{
			numGuessesValue = value;
		}
		
		/**
		 * Gets the number of guesses value.
		 * 
		 * @return number of guesses
		 */
		public int getNumGuessesValue ()
		{
			return numGuessesValue;
		}
		
		/**
		 * Gets the number of guesses display value.
		 * 
		 * @return number of guesses display value
		 */
		public String getNumGuessesDisplayValue ()
		{
			return Integer.toString(numGuessesValue);
		}
	}
	
	/**
	 * The available color choices. These are defined as integer values that
	 * map to *.png files for the different colored pegs.
	 */
	public enum Colors
	{
		
		/**
		 * White game peg.
		 */
		WHITE(1),
		
		/**
		 * Black game peg.
		 */
		BLACK(2),
		
		/**
		 * Red game peg.
		 */
		RED(3),
		
		/**
		 * Green game peg.
		 */
		GREEN(4),
		
		/**
		 * Blue game peg.
		 */
		BLUE(5),
		
		/**
		 * Yellow game peg.
		 */
		YELLOW(6),
		
		/**
		 * Tan game peg.
		 */
		TAN(7),
		
		/**
		 * Pink game peg.
		 */
		PINK(8),
		
		/**
		 * Blank game peg.
		 */
		BLANK(9);
		
		private int colorValue;
		
		/*
		 * Constructor.
		 */
		private Colors (int value)
		{
			colorValue = value;
		}
		
		/**
		 * Gets the color value.
		 * 
		 * @return color value
		 */
		public int getColorValue ()
		{
			return colorValue;
		}
	}
	
	/*
	 * Constructor. Making it private prevents instantiation by any other class.
	 */
	private Options ()
	{
	}
	
    //---------------- Getters and setters ---------------------------------

	/**
	 * Gets the number of colors.
	 * 
	 * @return number of colors
	 */
	public int getNumColors()
	{
		return numColors;
	}

	/**
	 * Sets the number of colors.
	 * 
	 * @param numColors number of colors
	 */
	public void setNumColors(int numColors)
	{
		this.numColors = numColors;
	}

	/**
	 * Gets the number of holes (pegs).
	 * 
	 * @return number of holes
	 */
	public int getNumHoles()
	{
		return numHoles;
	}

	/**
	 * Sets the number of holes (pegs).
	 * 
	 * @param numHoles number of holes
	 */
	public void setNumHoles(int numHoles)
	{
		this.numHoles = numHoles;
	}

	/**
	 * Gets the number of user guesses.
	 * 
	 * @return number of guesses
	 */
	public int getNumGuesses()
	{
		return numGuesses;
	}

	/**
	 * Sets the number of user guesses.
	 * 
	 * @param numGuesses number of guesses
	 */
	public void setNumGuesses(int numGuesses)
	{
		this.numGuesses = numGuesses;
	}

	/**
	 * Gets the duplicates allowed flag.
	 * 
	 * @return duplicates allowed flag
	 */
	public boolean getDupsAllowed()
	{
		return dupsAllowed;
	}

	/**
	 * Sets the duplicates allowed flag.
	 * 
	 * @param dupsAllowed duplicates allowed flag
	 */
	public void setDupsAllowed(boolean dupsAllowed)
	{
		this.dupsAllowed = dupsAllowed;
	}

	/**
	 * Gets the blanks allowed flag.
	 * 
	 * @return blanks allowed flag
	 */
	public boolean getBlanksAllowed()
	{
		return blanksAllowed;
	}

	/**
	 * Sets the blanks allowed flag.
	 * 
	 * @param blanksAllowed blanks allowed flag
	 */
	public void setBlanksAllowed(boolean blanksAllowed)
	{
		this.blanksAllowed = blanksAllowed;
	}
	
	public void readJavaPreferences ()
	{
		numColors = accessJavaPreferenceInt(JAVA_PREFS_KEY_NUM_COLORS);
		numHoles = accessJavaPreferenceInt(JAVA_PREFS_KEY_NUM_HOLES);
		numGuesses = accessJavaPreferenceInt(JAVA_PREFS_KEY_NUM_GUESSES);
		dupsAllowed = accessJavaPreferenceBoolean(JAVA_PREFS_KEY_DUPS_ALLOWED);
		blanksAllowed = accessJavaPreferenceBoolean(JAVA_PREFS_KEY_BLANKS_ALLOWED);
	}
	
	/**
	 * Accesses a Java integer preference for a given key. This method also 
	 * sets the preference in case it was not currently set.
	 * 
	 * @param key key that represents the preference
	 * @return value for the specified key
	 */
	public int accessJavaPreferenceInt (String key)
	{
		int result;
		
		/*
		 * Get the Java preferences node.
		 */
		java.util.prefs.Preferences javaPrefs;
		javaPrefs = java.util.prefs.Preferences.userRoot().node(Options.class.getName());
		
		/*
		 * Get the default value in case the Java preference doesn't exist.
		 * 
		 * NOTE: We don't expect an unknown key so we don't bother checking it.
		 */
		int defaultValue = 0;
		switch (key)
		{
		case JAVA_PREFS_KEY_NUM_COLORS:
			defaultValue = DEFAULT_NUM_COLORS;
			break;

		case JAVA_PREFS_KEY_NUM_HOLES:
			defaultValue = DEFAULT_NUM_HOLES;
			break;

		case JAVA_PREFS_KEY_NUM_GUESSES:
			defaultValue = DEFAULT_NUM_GUESSES;
			break;
			
		default:
		}
		
		/*
		 * Get the Java preference value, or use the default.
		 */
		result = javaPrefs.getInt(key, defaultValue);
		
		/*
		 * We have no way of knowing if the Java preference was set before, so unconditionally set it.
		 */
		javaPrefs.putInt(key, result);
		
        return result;
	}
	
	/**
	 * Accesses a Java boolean preference for a given key. This method also 
	 * sets the preference in case it was not currently set.
	 * 
	 * @param key key that represents the preference
	 * @return value for the specified key
	 */
	public boolean accessJavaPreferenceBoolean (String key)
	{
		boolean result;
		
		/*
		 * Get the Java preferences node.
		 */
		java.util.prefs.Preferences javaPrefs;
		javaPrefs = java.util.prefs.Preferences.userRoot().node(Options.class.getName());
		
		/*
		 * Get the default value in case the Java preference doesn't exist.
		 * 
		 * NOTE: We don't expect an unknown key so we don't bother checking it.
		 */
		boolean defaultValue = false;
		switch (key)
		{
		case JAVA_PREFS_KEY_DUPS_ALLOWED:
			defaultValue = DEFAULT_DUPS_ALLOWED;
			break;

		case JAVA_PREFS_KEY_BLANKS_ALLOWED:
			defaultValue = DEFAULT_BLANKS_ALLOWED;
			break;
			
		default:
		}
		
		/*
		 * Get the Java preference value, or use the default.
		 */
		result = javaPrefs.getBoolean(key, defaultValue);
		
		/*
		 * We have no way of knowing if the Java preference was set before, so unconditionally set it.
		 */
		javaPrefs.putBoolean(key, result);
		
        return result;
	}
	
	/**
	 * Saves a Java integer preference for a given key.
	 * 
	 * @param key key that represents the preference
	 * @param value value for the specified key
	 */
	public void saveJavaPreferenceInt (String key, int value)
	{
		
		/*
		 * Get the Java preferences node.
		 */
		java.util.prefs.Preferences javaPrefs;
		javaPrefs = java.util.prefs.Preferences.userRoot().node(Options.class.getName());

		/*
		 * Save the java preference value.
		 */
		javaPrefs.putInt(key, value);
	}
	
	/**
	 * Saves a Java boolean preference for a given key.
	 * 
	 * @param key key that represents the preference
	 * @param value value for the specified key
	 */
	public void saveJavaPreferenceBoolean (String key, boolean value)
	{
		
		/*
		 * Get the Java preferences node.
		 */
		java.util.prefs.Preferences javaPrefs;
		javaPrefs = java.util.prefs.Preferences.userRoot().node(Options.class.getName());

		/*
		 * Save the java preference value.
		 */
		javaPrefs.putBoolean(key, value);
	}
}