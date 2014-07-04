package TraceFileParser.wissim;

import java.awt.Component;
import java.io.IOException;

public interface IFileParser {
	/**
	 * 
	 * @param filePathNodes
	 * @param filePathEvent
	 * @throws IOException
	 */
	public void ConvertTraceFile(String filePathNodes, String filePathEvent)
			throws IOException;
	/**
	 * 
	 * @param mNodesTraceFile
	 */
	public void parseNodes(String mNodesTraceFile);
	/**
	 * 
	 * @param mFileTraceEvent
	 * @throws IOException
	 */
	public void parseEvents(String mFileTraceEvent) throws IOException;


}
