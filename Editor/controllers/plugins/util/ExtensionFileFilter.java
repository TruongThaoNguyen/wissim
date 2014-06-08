package controllers.plugins.util;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * @author khaclinh
 *
 */
public class ExtensionFileFilter implements FileFilter {

    private String extension;

    public ExtensionFileFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File file) {
        // perform a case insensitive check.
        return file.getName().toUpperCase().endsWith(extension.toUpperCase());
    }

}
