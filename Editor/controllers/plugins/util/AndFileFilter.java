package controllers.plugins.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * @author khaclinh
 *
 */
public class AndFileFilter implements FileFilter {

    /** The list of file filters. */
    private List<FileFilter> fileFilters;

    public AndFileFilter() {
        this.fileFilters = new ArrayList<FileFilter>();
    }

    public AndFileFilter(List<FileFilter> fileFilters) {
        this.fileFilters = new ArrayList<FileFilter>(fileFilters);
    }

    public void addFileFilter(FileFilter fileFilter) {
        fileFilters.add(fileFilter);
    }

    public List<FileFilter> getFileFilters() {
        return Collections.unmodifiableList(fileFilters);
    }

    public boolean removeFileFilter(FileFilter fileFilter) {
        return fileFilters.remove(fileFilter);
    }

    public void setFileFilters(List<FileFilter> fileFilters) {
        this.fileFilters = new ArrayList<FileFilter>(fileFilters);
    }

    @Override
    public boolean accept(File file) {
        if (this.fileFilters.size() == 0) {
            return false;
        }

        for (Iterator<FileFilter> iter = this.fileFilters.iterator(); iter.hasNext();) {
            FileFilter fileFilter = (FileFilter) iter.next();
            if (!fileFilter.accept(file)) {
                return false;
            }
        }

        return true;
    }

}
