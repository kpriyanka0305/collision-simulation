package kpi;

import javax.swing.*;
import java.util.*;

class KpiWindow extends JFrame
{
    JPanel panel;
    Map<String, Integer> kpi_data= new HashMap<String, Integer>();
    JLabel label1;
    JLabel label2;
    int collision_count = 0;
    int near_coll_count = 0;
    KpiWindow(){
	    panel = new JPanel();
	    add(panel);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(200, 200);
	    
	    this.label1 = new JLabel("<html>Collisions : <html>" + String.valueOf(collision_count) + "<html><br><html>");
	    this.label2 = new JLabel("<html>Near Collisions : <html>" + String.valueOf(near_coll_count));
	    label1.setOpaque(true);
	    label2.setOpaque(true);
	    
	    panel.add(label1);
	    panel.add(label2);
	    
	    panel.validate();
	    panel.repaint();
	    setVisible(true);
	    setTitle("KPI Data");
    }

   public void updateKpiDataWindow(int colls, int near_colls){
	   //System.out.println("********" +colls +","+ near_colls);
	   if(this.collision_count != colls) {
		   //System.out.println("-----");
		   this.label1.setText("<html>Collisions : <html>" + String.valueOf(colls) + "<html><br><html>");
		   this.collision_count = colls;
	   }
	   if(this.near_coll_count != near_colls) {
		   //System.out.println("^^^^");
		   this.label2.setText("<html>Near Collisions : <html>" + String.valueOf(near_colls));
		   this.near_coll_count = near_colls;
	   }
    }
  }