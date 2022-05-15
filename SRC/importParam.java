package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Read in parameters from text file --DMH
 * "parameter names =" defines what each parameter is 
 **/

public class importParam {
	

	public static double fullDuration,p_nucleation,nucSite_x,nucSite_y,k_polyRate,p_spec,k_depolyRate,k_depolyBERate,
		k_capRate,k_uncapRate,k_branchRate,k_debranchRate,k_severRate,k_severRate_u,severCap,k_oligomerAnneal,z_branch,
		retFlow,l_mon,denovoSize,l_crit,fil_diffusion,dt,steptime,outPlaneAng,filament_kink,depolyBE,severType,p_destab, k_polyUncap;
	
	public static void import_params(String filepath, String exename){		
		System.out.println("starting to read in paramerters");
		try{
			   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
			    String strLine;
			    while((strLine=br.readLine()) != null){
					if(strLine.contains("fullDuration =")){
						String paraName = "fullDuration =";
						fullDuration = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("p_nucleation =")){
						String paraName = "p_nucleation =";
						p_nucleation  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("nucSite_x =")){
						String paraName = "nucSite_x =";
						nucSite_x = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("nucSite_y =")){
						String paraName = "nucSite_y =";
						nucSite_y = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_polyRate =")){
						String paraName = "k_polyRate =";
						k_polyRate = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("p_spec =")){
						String paraName = "p_spec =";
						p_spec = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_depolyRate =")){
						String paraName = "k_depolyRate =";
						k_depolyRate = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_depolyBERate =")){
						String paraName = "k_depolyBERate =";
						k_depolyBERate = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_capRate =")){
						String paraName = "k_capRate =";
						k_capRate = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_uncapRate =")){
						String paraName = "k_uncapRate =";
						k_uncapRate  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_branchRate =")){
						String paraName = "k_branchRate =";
						k_branchRate = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_debranchRate =")){
						String paraName = "k_debranchRate =";
						k_debranchRate = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_severRate =")){
						String paraName = "k_severRate =";
						k_severRate  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_severRate_u =")){
						String paraName = "k_severRate_u =";
						k_severRate_u  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_oligomerAnneal =")){
						String paraName = "k_oligomerAnneal =";
						k_oligomerAnneal  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("z_branch =")){
						String paraName = "z_branch =";
						z_branch = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("retFlow =")){
						String paraName = "retFlow =";
						retFlow = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("l_mon =")){
						String paraName = "l_mon =";
						l_mon = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("denovoSize =")){
						String paraName = "denovoSize =";
						denovoSize  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("l_crit =")){
						String paraName = "l_crit =";
						l_crit = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("fil_diffusion =")){
						String paraName = "fil_diffusion =";
						fil_diffusion = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("dt =")){
						String paraName = "dt =";
						dt  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("steptime =")){
						String paraName = "steptime =";
						steptime = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("outPlaneAng =")){
						String paraName = "outPlaneAng =";
						outPlaneAng = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("filament_kink =")){
						String paraName = "filament_kink =";
						filament_kink = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("depolyBE =")){
						String paraName = "depolyBE =";
						depolyBE  = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("severType =")){
						String paraName = "severType =";
						severType = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("severCap =")){
						String paraName = "severCap =";
						severCap = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("p_destab =")){
						String paraName = "p_destab =";
						p_destab = getParameterValue(paraName, strLine, exename);
					}
					else if(strLine.contains("k_polyUncap =")){
						String paraName = "k_polyUncap =";
						k_polyUncap = getParameterValue(paraName, strLine, exename);
					}
			    }
			    br.close();
			    
		} catch (IOException e) {
			    System.out.println("ERROR: unable to read file " + filepath);
			    e.printStackTrace();   	
		}
		
	}
	
	
private static double getParameterValue(String parameterName, String line, String exename) {
	double para;	
	para = Double.parseDouble(line.substring(line.indexOf("=")+1,line.length()));
	if(exename!=null){
		if(exename.contains(parameterName)){
			try{
				String token = exename.substring(exename.indexOf(parameterName), exename.length());
				token = token.substring(token.indexOf(parameterName)+parameterName.length(), token.indexOf("~"));	//the parameter change must end by ~
				para = Double.parseDouble(token);
			}
			catch(NumberFormatException nfe){
				para = Double.parseDouble(line.substring(line.indexOf("=")+1,line.length()));
			}
		}
	}
	else{
		para = Double.parseDouble(line.substring(line.indexOf("=")+1,line.length()));
	}
	return para;
}
}
