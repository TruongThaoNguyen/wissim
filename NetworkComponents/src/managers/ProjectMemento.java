package managers;
import model.Project;
import nu.xom.Document;

public class ProjectMemento {
	// description telling the situation it is saved
	private String description;
	
	// saves states of project in a Document
	private Document doc;
	
	// storing the path of project out of doc file
	private String path;
	
	/**
	 * Initialize an ProjectMemento object with a specific description
	 * @param desc
	 */
	public ProjectMemento(String desc) {
		description = desc;
	}
	
	/**
	 * Save the states of project
	 * @param p
	 */
	public void save(Project p) {
		this.path = p.getPath();
		doc = Parser.saveProjectToDocument(p);
	}
	
	/**
	 * Restore project
	 * @return
	 */
	public Project restore() {
		return Parser.loadProjectFromDocument(doc, path);
	}
	
	/**
	 * Gets description
	 * @return
	 */
	public String getDescription() {
		return description;
	}
}
