package controllers.chart3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class SurfaceChartEnergy {
	public static void drawChart3D(){
		try {
	        Runtime rt = Runtime.getRuntime();
//	        Process proc = rt.exec("exe/pgnuplot.exe");
	        String[] s = new String[]{"gnuplot"};
	        Process proc = rt.exec(s);
//	        while(true){
		        java.io.OutputStream opStream = proc.getOutputStream();
		        PrintWriter gp = new PrintWriter(new BufferedWriter(new OutputStreamWriter(opStream)));
		  
		        BufferedReader br = new BufferedReader(new FileReader("GnuplotEnergy"));
		        String line = null;
		        while ((line = br.readLine()) != null)
		        	gp.println(line+"");
		        gp.println("pause mouse close;\n");
		        br.close();
		        gp.close();
//		        int exitVal = proc.waitFor();
//		        System.out.println("Exited with error code "+exitVal);
//		        if(exitVal == 0)
//		        	break;
//		        else 
//	        		proc.destroy();
//		        proc = rt.exec("exe/pgnuplot.exe");
//	        }
	    } catch(Exception e) {
	        System.out.println(e.toString());
	        e.printStackTrace();
	    }
	}
}
