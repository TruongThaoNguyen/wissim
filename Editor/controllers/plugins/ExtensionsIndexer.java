package controllers.plugins;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
/**
 * 
 * @author khaclinh
 *
 */
public class ExtensionsIndexer extends AbstractProcessor {

	public static final String EXTENSIONS_RESOURCE = "META-INF/extensions.idx";
	
	private List<TypeElement> extensions = new ArrayList<TypeElement>();
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotationTypes = new HashSet<String>();
        annotationTypes.add(Extension.class.getName());
        
        return annotationTypes;
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
            return false;
        }
		
		for (Element element : roundEnv.getElementsAnnotatedWith(Extension.class)) {
			if (!(element instanceof TypeElement)) {
				continue;
			}

			TypeElement typeElement = (TypeElement) element;
			String message = "Extension found in " + processingEnv.getElementUtils().getBinaryName(typeElement).toString();
			processingEnv.getMessager().printMessage(Kind.NOTE, message);
			extensions.add(typeElement);
        }
		
		/*
		if (!roundEnv.processingOver()) {
			return false;
		}
		*/

		write();
		
		return false;
//		return true; // no further processing of this annotation type
	}
	
	private void write() {
		Set<String> entries = new HashSet<String>();
		for (TypeElement typeElement : extensions) {
			entries.add(processingEnv.getElementUtils().getBinaryName(typeElement).toString());
		}
		
		read(entries); // read old entries
		write(entries); // write entries
	}

	private void write(Set<String> entries) {
		try {
			FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", EXTENSIONS_RESOURCE);
			Writer writer = file.openWriter();
			for (String entry : entries) {
				writer.write(entry);
				writer.write("\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// it's the first time, create the file
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Kind.ERROR, e.toString());
		}
	}
	
	private void read(Set<String> entries) {
		try {
			FileObject file = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", EXTENSIONS_RESOURCE);
			readIndex(file.openReader(true), entries);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			// thrown by Eclipse JDT when not found
		} catch (UnsupportedOperationException e) {
			// java6 does not support reading old index files
		}
	}
	
	public static void readIndex(Reader reader, Set<String> entries) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			entries.add(line);
		}
		
		reader.close();
	}
	
}
 