package controllers.chart3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import models.Node;
import models.Packet;
import views.Analyzer;

public class SurfaceChartThroughput {
	
	public static void createData() throws IOException{
		//TraceFile.ConvertTraceFile();
		double totalSize[]=new double[Analyzer.mParser.getListNodes().size()];
        double totalTime[]=new double[Analyzer.mParser.getListNodes().size()];
        for(int i=0;i<Analyzer.mParser.getListPacket().size();i++){
        	Packet packet = Analyzer.mParser.getListPacket().get(i);
        	if(packet.isSuccess){
	        	totalSize[Integer.parseInt(packet.sourceID)]+=Double.parseDouble(packet.size);
	        	totalTime[Integer.parseInt(packet.sourceID)]+=(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
        	}
    	}
        
		FileOutputStream fos= new FileOutputStream("DataThroughput",false);
        PrintWriter pw= new PrintWriter(fos);
        for(int i=0;i<totalSize.length;i++){
        	Node node = Analyzer.mParser.getListNodes().get(i);
        	if (totalTime[i] !=0)
        		pw.println(node.x+" "+node.y+" "+totalSize[i]/totalTime[i]);
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
