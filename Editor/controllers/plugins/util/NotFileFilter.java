package controllers.plugins.util;

import java.io.File;
import java.io.FileFilter;
/**
 * 
 * @author khaclinh
 *
 */
public class NotFileFilter implements FileFilter {

    private FileFilter filter;

    public NotFileFilter(FileFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(File file) {
        return !filter.accept(file);
    }

}
