package chart3D;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import parser.NodeTrace;
import parser.Packet;
import views.Analyzer;

public class SurfaceChartEfficiency {
	public static void createData() throws IOException{
		//TraceFile.ConvertTraceFile();
		double totalDrop[]=new double[Analyzer.mParser.getListNodes().size()];
        for(int i=0;i<Analyzer.mParser.getListPacket().size();i++){
        	Packet packet = Analyzer.mParser.getListPacket().get(i);
        	if(!packet.isSuccess && packet.getType().equals("cbr"))
        		totalDrop[Integer.parseInt(packet.sourceID)]++;
        }
        
		FileOutputStream fos= new FileOutputStream("DataEfficiency",false);
        PrintWriter pw= new PrintWriter(fos);
        for(int i=0;i<totalDrop.length;i++){
        	NodeTrace node = Analyzer.mParser.getListNodes().get(i);
        	if (totalDrop[i] !=0)
        		pw.println(node.x+" "+node.y+" "+totalDrop[i]);
        }
        pw.close();
	}
	public static void drawChart3D(){
		try {
			createData();
	        Runtime rt = Runtime.getRuntime();
	        Process proc = rt.exec("exe/pgnuplot.exe");
	        while(true){
		        java.io.OutputStream opStream = proc.getOutputStream();
		        PrintWriter gp = new PrintWriter(new BufferedWriter(new OutputStreamWriter(opStream)));
		  
		        BufferedReader br = new BufferedReader(new FileReader("GnuplotEfficiency"));
		        String line = null;
		        while ((line = br.readLine()) != null)
		        	gp.println(line);
		        gp.println("pause mouse close;\n");
		        br.close();
		        gp.close();
		        int exitVal = proc.waitFor();
		        System.out.println("Exited with error code "+exitVal);
		        if(exitVal == 0)
		        	break;
		        else 
		        	proc.destroy();
		        proc = rt.exec("exe/pgnuplot.exe");
	        }
	    } catch(Exception e) {
	        System.out.println(e.toString());
	        e.printStackTrace();
	    }
	}
}
