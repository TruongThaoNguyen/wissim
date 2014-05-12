package wissim.object;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractNode;
import org.graphstream.graph.implementations.MultiGraph;

import org.graphstream.graph.implementations.SingleGraph;

public class WiGraph extends SingleGraph {
    wiSpriteManager spriteMan;

    public wiSpriteManager getSpriteMan() {
        return spriteMan;
    }
    public WiGraph(String id) {
        super(id);
        spriteMan = new wiSpriteManager(this);
        AddAttribute();
    }

    @Deprecated
    @Override
    public <T extends Node> T addNode(String id) {
        return null;
    }

    public void AddAttribute() {
        addAttribute("ui.antialias");
        addAttribute("ui.quality");
        //
        addAttribute("ui.stylesheet", getCssStyle());
    }

    public wiNode addwiNode(String id) {
        Node n = super.addNode(id);
        if (n != null) {
            n.addAttribute("data", new NodeData());
            n.addAttribute("ui.class", "init, wake, sleep, send, broadcast, receive");
            return new wiNode(this, n);
        } else {
            return null;
        }
    }

    public wiNode getwiNode(String id) {
        Node n = super.getNode(id);
        if (n == null) {
            return null;
        } else {
            
            return new wiNode(this, n);
        }
    }

    public wiEdge addwiEdge(String id, String node1, String node2) {
        Edge e = super.addEdge(id, node1, node2);
        if(e!= null)
        	return new wiEdge(this,e);
        else
        	return null;
        /*
        if (e != null) {
            e.addAttribute("data", new EdgeData());
            return new wiEdge(this, e);
        } else {
            return null;
        }
        */
        
    }

    public wiEdge getwiEdge(String id) {
        Edge e = super.getEdge(id);
        if (e == null) {
            return null;
        } else {
            return new wiEdge(this, e);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }
    public void reset(){
        
    }
    
   
    public static String getCssStyle() {
    	String parent = new File("..").getAbsolutePath();
    	parent = parent.substring(0, parent.length() - 2);
    	String fileCssPath = parent + "res" + File.separator + "graph.css";
    	System.out.println("crt: " + fileCssPath);

    	return "url('file:///" + fileCssPath + "')";
        }
}