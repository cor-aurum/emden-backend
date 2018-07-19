package de.recondita.emden.data.input;

import de.recondita.emden.data.Settings;

/**
 * Providerclass for configured Sources
 * 
 * @author felix
 *
 */
public final class SourceSetup {

	private static String sources = "";
	public final static String APPENDIX = "temp";

	/**
	 * You cannot instantiate me
	 */
	private SourceSetup() {
	}

	/**
	 * adds a Single source
	 * 
	 * @param source
	 *            a Single Sourcedescription
	 */
	public synchronized static void setSource(String source) {
		if (!sources.isEmpty())
			sources += ",";
		sources += Settings.getInstance().getProperty("index.basename")+source.toLowerCase();
	}

	/**
	 * Toggle the Searchindex to the Backupped one
	 * 
	 * @param source
	 *            index
	 */
	public static void doBackup(String source) {
		sources=sources.replaceAll(source, source + APPENDIX);
		System.out.println("Sources: "+sources);
	}

	/**
	 * Toggle the Backupped Searchindex to the primary one
	 * 
	 * @param source
	 *            index
	 */
	public static void undoBackup(String source) {
		sources=sources.replaceAll(source+APPENDIX, source);
		System.out.println("Sources: "+sources);
	}

	/**
	 * Returns all Sources, separated by comma
	 * 
	 * @return sources
	 */
	public static String getSourceCSV() {
		return sources;
	}
}
