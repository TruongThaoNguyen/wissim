package wissim.object.table;

import java.util.ArrayList;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.Packet;
import TraceFileParser.wissim.FullParser;

public class PacketData {

	public static enum PacketType {
		HELLO, CBR, GPRS
	}

	public Integer id;
	public String type;
	public Integer source;
	public Integer destination;
	public String hopCount;
	public Float timeSent;
	public Float timeReceived;
	public Integer delay;
	public String path;
	public Integer ttl;
	public Boolean drop;
	public Integer view;

	public static ArrayList<PacketData> getPacketData(AbstractParser mParser) {
		ArrayList<PacketData> pdList = new ArrayList<>();
		if (mParser != null) {
			for (Packet p : mParser.getListPacket()) {
				PacketData pd = new PacketData();
				pd.id = Integer.valueOf(p.id);
				pd.type = p.type;
				pd.source = Integer.valueOf(p.sourceID);
				pd.destination = Integer.valueOf(p.destID);
				if (p.isSuccess) {
					pd.hopCount = String.valueOf(p.listNode.size());
				} else
					pd.hopCount = "DROP";
				pd.timeSent = Float.parseFloat(p.startTime);
				pd.timeReceived = Float.valueOf(p.endTime);
				pd.drop = p.isSuccess;
				pd.view = Integer.valueOf(p.id);
				if (p.listNode.isEmpty()) {
					pd.path = "";
				} else {
					StringBuilder path = new StringBuilder();
					for (NodeTrace n : p.listNode) {
						path.append(n.id);
						path.append(",");
					}
					pd.path = path.toString();
				}

				pdList.add(pd);

			}
		}

		return pdList;
	}
}
