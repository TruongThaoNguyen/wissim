package controllers.plugins.util;

/**
 * 
 * @author khaclinh
 *
 */
public class JarFileFilter extends ExtensionFileFilter {

    /**
     * The extension that this filter will search for.
     */
    private static final String JAR_EXTENSION = ".JAR";

    public JarFileFilter() {
        super(JAR_EXTENSION);
    }

}
