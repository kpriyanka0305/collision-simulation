package kpi;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Vehicletype;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.ws.container.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;
import java.io.FileWriter;  

public class Kpi {		
	SumoTraciConnection conn;
	static HashMap<String, Integer> kpi = new HashMap<String, Integer>();
	Double collision_limit = 0.5;
	Double near_collision_limit = 1.0;
	static Set<String> collision_ids = new HashSet<String>();
	static Set<String> near_collision_ids = new HashSet<String>();	
	static KpiWindow kpiDataWindow;
    String s = new String();
    static boolean write_flag = false;
    static Double max_sim_time = 300.0;
    static int i = 0;
    static String type_scen = "chaotic";
    //static String type_scen = "onlyrsu";
    //static String type_scen = "rsuandobu";
	
	public Kpi(SumoTraciConnection connection) throws Exception {
		this.conn = connection;
		try {
			kpiDataWindow = new KpiWindow();
		}
		finally{
			
		}
	}
	
	public void checkKPIs() throws Exception{
		List<String> vehicles = new ArrayList<String>();
		vehicles = (List<String>)this.conn.do_job_get(Vehicle.getIDList());
		HashMap<String, List<Double>> buses = new HashMap<>();
		HashMap<String, List<Double>> bicycles = new HashMap<>();
		for (String v : vehicles) {
			if(v.contains("bus")) {
				buses.put(v, getData(v));
			}
			else {
				bicycles.put(v, getData(v));
			}			
		}
		
		for (Map.Entry<String, List<Double>> o : buses.entrySet()) {
		    String key = o.getKey();
		    List<Double> bus_values = o.getValue();
		    for (Map.Entry<String, List<Double>> p : bicycles.entrySet()) {
		    	List<Double> bicycle_values = p.getValue();
		    	String cycleKey = p.getKey();
		    	for (int i = 0; i < bus_values.size(); i++) {
		    		for (int j = 0; j < bicycle_values.size(); j++) {
		    			if( (bus_values.get(i) < collision_limit) && (bicycle_values.get(j) < collision_limit) ) {
		    				if(!collision_ids.contains(key)) {		    					
								collision_ids.add(key);
								
								System.out.println("-> Collision Hit");
								Double speed = (double)this.conn.do_job_get(Vehicle.getSpeed(key));
								String type = (String)this.conn.do_job_get(Vehicle.getTypeID(key));
								Double cycleSpeed = (double)this.conn.do_job_get(Vehicle.getSpeed(cycleKey));
								String cycleType = (String)this.conn.do_job_get(Vehicle.getTypeID(cycleKey));
							
								System.out.println("-> Bus type & Key : " + type + "   " + key + "     Cycle type & key  " + cycleKey + "    " + cycleType);
								System.out.println("-> BUS speed is " + speed + "    Cycle Speed is " +  cycleSpeed);
								System.out.println("-> BUS values is " + bus_values.get(i) + "    Cycle Values is " +  bicycle_values.get(j));
								System.out.println("-------------------------------------------------------------------");
								
								//System.out.println(" KPI JAVA,: bus value size:"+ bus_values.size() + " collision bus values : " + bus_values.get(i) + " bicycle values : " +  bicycle_values.get(j)+ "-- size--" + collision_ids.size());
								
		    				}
		    				
		    			}
		    			else {
			    			if( (i == 0) && (j == bicycle_values.size()-1) ) {
			    				
			    			//	System.out.println("--Collision limit-- " + bus_values.get(i) + "--------");
			    				if( bus_values.get(i) > collision_limit && bus_values.get(i) <= near_collision_limit
			    						&& bicycle_values.get(j) >= 0.0 && bicycle_values.get(j) <= collision_limit ) {
			    					if(!collision_ids.contains(cycleKey)) {
			    						near_collision_ids.add(cycleKey);
			    					}
			    					
			    					System.out.println("-> Near Collision Hit");
									Double speed = (double)this.conn.do_job_get(Vehicle.getSpeed(key));
									Double cycleSpeed = (double)this.conn.do_job_get(Vehicle.getSpeed(cycleKey));
									String type = (String)this.conn.do_job_get(Vehicle.getTypeID(key));
									String cycleType = (String)this.conn.do_job_get(Vehicle.getTypeID(cycleKey));
									System.out.println("-> Bus type & Key : " + type + "  " + key + "     Cycle type & key  " + cycleKey + "    " + cycleType);
									System.out.println("-> BUS speed is " + speed + "    Cycle Speed is " +  cycleSpeed);
									System.out.println("-> BUS values is " + bus_values.get(i) + "    Cycle Values is " +  bicycle_values.get(j));
									System.out.println("-------------------------------------------------------------------");
			    					//System.out.println(" KPI JAVA,: bicyclce value size  " + bicycle_values.size() +" near collision bus values1 : " + bus_values.get(i) + " bicycle values : " +  bicycle_values.get(j)+ "-- near collision size is--" + near_collision_ids.size());
			    					//Double cycleSpeed = (double)this.conn.do_job_get(Vehicle.getSpeed(cycleKey));
			    					//System.out.println("KPI JAVA Cycle speed is " + cycleSpeed);
				    			}
			    				
			    			}
			    			if( (i == bus_values.size()-1) && (j == 0) ) {
			    				if( bus_values.get(i) >= 0.0 && bus_values.get(i) <= collision_limit
			    						&& bicycle_values.get(j) > collision_limit && bicycle_values.get(j) <= near_collision_limit ) {
			    					if(!collision_ids.contains(cycleKey)) {
			    						near_collision_ids.add(cycleKey);
			    					}
			    					System.out.println(" KPI JAVA, near collision values2 : " + bus_values.get(i) + " bicycle values : " +  bicycle_values.get(j)+ "-- size--" + near_collision_ids.size());
			    				}
			    				
			    			
			    			}
			    			
			    			
		    			}
		    		}
				}
		    }
		}
		
		kpi.put("Collision", collision_ids.size());
		kpi.put("NearCollision", near_collision_ids.size());
		kpiDataWindow.updateKpiDataWindow(collision_ids.size(), near_collision_ids.size());
		Double time_ins;
		time_ins = (Double)this.conn.do_job_get(Simulation.getTime());
		if(time_ins >= i) {
			s = s + time_ins + "," + collision_ids.size() + "," + near_collision_ids.size() + "," + 111 + "\n";
			i = i + 50;
		}
		
		if(write_flag == false && time_ins > max_sim_time) {
			 write_flag = true; 
	         try{    
	           FileWriter fw=new FileWriter("data/" + type_scen + ".csv");    
	           fw.write(s);    
	           fw.close();    
	         }
	         catch(Exception e){
	        	 System.out.println(e);
	         }
		}		
	}
		
	public List<Double> getData(String id) throws Exception{
		SumoPosition2D position = (SumoPosition2D)this.conn.do_job_get(Vehicle.getPosition(id));
		SumoPosition2D centre = new SumoPosition2D(2.28, 0.88);
		String type = (String)this.conn.do_job_get(Vehicle.getTypeID(id));
		Double speed = (double)this.conn.do_job_get(Vehicle.getSpeed(id)); 
		Double length = (double)this.conn.do_job_get(Vehicle.getLength(id));
		Double tempx = Math.abs(centre.x - position.x);
		Double tempy = Math.abs(centre.y - position.y);	
		Double distance = Math.sqrt(tempx * tempx + tempy * tempy);
		distance = Math.abs(distance - length / 2);
		
		List<Double> times = new ArrayList<Double>();
		if(type.contains("bus")) {
			for (double i = 0; i < length; i++) {
				times.add( (distance+i)/speed );
			}
		}
		if(type.contains("bicycle")) {
			times.add(distance/speed);
			times.add((distance+length)/speed);
		}
		return times;
	}	
}