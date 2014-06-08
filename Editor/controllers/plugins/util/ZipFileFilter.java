package controllers.plugins.util;

/**
 * 
 * @author khaclinh
 *
 */
public class ZipFileFilter extends ExtensionFileFilter {

    /**
     * The extension that this filter will search for.
     */
    private static final String ZIP_EXTENSION = ".ZIP";

    public ZipFileFilter() {
        super(ZIP_EXTENSION);
    }

}
