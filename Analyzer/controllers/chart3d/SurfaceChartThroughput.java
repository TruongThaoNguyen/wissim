package controllers.chart3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import controllers.parser.ParserManager;
import models.Node;
import models.Packet;

public class SurfaceChartThroughput {
	
	public static void createData() throws IOException{
		HashMap<Node, Integer> totalDrop = new HashMap<Node, Integer>();
		
		for (Node node : ParserManager.getParser().getNodes().values()) 
		{
			totalDrop.put(node, 0);
		}		
        	
        for (Packet packet : ParserManager.getParser().getPackets().values()) 
        {		
        	if (!packet.isSuccess() && packet.getType().equals("cbr"))
        	{
        		totalDrop.put(packet.getSourceNode(), totalDrop.get(packet.getSourceNode()) + 1);
        	}
        }
        
		FileOutputStream fos= new FileOutputStream("DataEfficiency",false);
        PrintWriter pw = new PrintWriter(fos);
                
        for (Node node : totalDrop.keySet()) {
			pw.println(node.getX() + " " + node.getY() + " " + totalDrop.get(node));
		}
        
        pw.close();
	}
	public static void drawChart3D(){
		try {
			createData();
	        Runtime rt = Runtime.getRuntime();
	        String[] s = new String[]{"gnuplot"};
	        Process proc = rt.exec(s);
//	        while(true){
		        java.io.OutputStream opStream = proc.getOutputStream();
		        PrintWriter gp = new PrintWriter(new BufferedWriter(new OutputStreamWriter(opStream)));
		  
		        BufferedReader br = new BufferedReader(new FileReader("GnuplotThroughput"));
		        String line = null;
		        while ((line = br.readLine()) != null)
		        	gp.println(line+"");
		        gp.println("pause mouse close;\n");
		        br.close();
		        gp.close();
//		        int exitVal = proc.waitFor();
//		        System.out.println("Exited with error code "+exitVal);
//		        	if(exitVal == 0)
//		        		break;
//		        	else 
//		        	proc.destroy();
//		        proc = rt.exec("exe/pgnuplot.exe");
//	        }
	    } catch(Exception e) {
	        System.out.println(e.toString());
	        e.printStackTrace();
	    }
	}
}
