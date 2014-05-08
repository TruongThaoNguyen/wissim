package TraceFileParser.wissim;

import java.awt.Component;
import java.io.IOException;

public interface IFileParser {
	public void ConvertTraceFile(String filePathNodes, String filePathEvent)
			throws IOException;

	public void parseNodes(String mNodesTraceFile);

	public void parseEvents(String mFileTraceEvent) throws IOException;


}
