package controllers.converter;

import java.util.List;

import models.converter.InsProc;
import models.networkcomponents.WirelessNode;

public class SNode extends WirelessNode implements TclObject {

	public SNode(SNetwork network) {
		super(network);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String parse(List<String> command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInsProc(InsProc p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InsProc getInsProc(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInsVar(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setInsVar(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void setLabel(String label) {
		// TODO Auto-generated method stub
		
	}
}
