package algorithms;

import networkcomponents.Network;
import networkcomponents.Node;
import networkcomponents.WirelessNetwork;
import networkcomponents.WirelessNode;

public class PlanarGraphAlgorithm extends Algorithm {
	public static final int RNG = 0, GG = 1;

	private int type;
	
	public PlanarGraphAlgorithm(Object input) {
		super(input);
	}

	@Override
	public Object doAlgorithm() {
		switch (type) {
		case RNG:
			return doRNG();
		case GG:
			return doGG();
		}
		
		return null;
	}
	
	public Graph doRNG() {
		// create new original graph 
		Graph rngGraph = new Graph((WirelessNetwork) input);
		
		// reduce graph
		int sizeTemp = ((WirelessNetwork) input).getNodeList().size();
		for (Node u : ((WirelessNetwork) input).getNodeList()) 
		{
			for (Node v : rngGraph.getAdjacentNodes((WirelessNode)u)) 
			{
				for (Node w : rngGraph.getAdjacentNodes((WirelessNode)u)) 
				{ 
					if (w == v) continue;
					
					// combine d(U, V) with d(U, W); d(V, W) 
					double dUV = d(u, v);
					if (dUV > d(u, w) && dUV > d(v, w))
					{
						//eliminate edge(u;v)
						if(u.getId() <= sizeTemp && v.getId() <= sizeTemp)
						rngGraph.reduce(u, v);
						break;
					}
				}
			}
		}
		
		return rngGraph;
	}
	
	public Graph doGG() {
		// create new original graph
		Graph ggGraph = new Graph((WirelessNetwork) input);
		int sizeTemp = ((WirelessNetwork) input).getNodeList().size();
		
		// reducre graph
		for (Node u : ((Network) input).getNodeList())
		{
			for (Node v : ggGraph.getAdjacentNodes((WirelessNode) u))
			{
				for (Node w : ggGraph.getAdjacentNodes((WirelessNode) u))
				{
					if (w == v) continue;
					
					// combine d(U, V) with d(U, W) + d(V, W)
					if (d(u, v) > d(u, w) + d(v, w))
					{
						//eliminate edge(u;v)
						if(u.getId() <= sizeTemp && v.getId() <= sizeTemp)
						ggGraph.reduce(u, v);
						break;
					}
				}
			}
		}
		
		return ggGraph;		
	} 
	
	public void setType(int t) {
		type = t;
	}

	private double d(Node n, Node m) {
		int x1 = ((WirelessNode) n).getX();
		int x2 = ((WirelessNode) m).getX();
		int y1 = ((WirelessNode) n).getY();
		int y2 = ((WirelessNode) m).getY();
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);	
	}
}
