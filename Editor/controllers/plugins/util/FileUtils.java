package controllers.plugins.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 * 
 * @author khaclinh
 *
 */
public class FileUtils {

	public static List<String> readLines(File file, boolean ignoreComments) throws IOException {
		if (!file.exists() || !file.isFile()) {
			return Collections.emptyList();
		}

		List<String> lines = new ArrayList<String>();

		BufferedReader reader = null;
		try {
	        reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (ignoreComments && !line.startsWith("#") && !lines.contains(line)) {
					lines.add(line);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return lines;
	}

    public static void writeLines(Collection<String> lines, File file) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                writer.write(line);
                writer.write('\n');
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

	/**
	 * Delete a file or recursively delete a folder.
	 *
	 * @param fileOrFolder
	 * @return true, if successful
	 */
	public static boolean delete(File fileOrFolder) {
		boolean success = false;
		if (fileOrFolder.isDirectory()) {
			File [] files = fileOrFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						success |= delete(file);
					} else {
						success |= file.delete();
					}
				}
			}
		}
		success |= fileOrFolder.delete();

		return success;
	}
}
