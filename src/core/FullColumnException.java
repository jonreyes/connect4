package core;

/**
 * Class
 * Custom Exception Class for handling when a Column is Full
 * and can no longer be filled with game pieces.
 * @author Jon Reyes
 */
public class FullColumnException extends Exception {
    /** Creates the custom FullColumnException object*/
    public FullColumnException(){
        super("Column is Full.");
    }
}
