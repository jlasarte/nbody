package nbody.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import nbody.model.Body;
import nbody.model.universe.*;

public class SimulationPanel extends JPanel {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int x 	= 10;	//Start Drawing from X=10	
    public int delay 	= 5; 	//milliseconds
    private UniverseTemplate u;
    private Timer t;
	private JButton reset;
	private String selected_universe;
    public SimulationPanel()
    {
    	//this.u = new SecuentialBruteForceUniverse();
    	//this.u = new BarnesHutUniverse();
    	//this.u = new ParallelBruteForceUniverse();
    	//this.u = new ParallelBarnesHutUniverse();
    	this.u = new ParallelBalancedBarnesHutUniverse();
		this.reset = new JButton("Reset");
		this.reset.setVisible(false);
		this.add(reset);
		
		this.reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				reset();
			}
		});

    }
    
    protected void reset() {
		this.initialize(selected_universe);
		this.repaint();
	}

	public void initialize(String universe) {
    	this.selected_universe = universe;
    	try {
			u.initialize(universe);
	    	this.reset.setVisible(true);
	    	this.repaint();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "My Goodness, this is so concise");
		}

    }
    
    public void start(double dt) {
    	 ActionListener counter = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
    			{ 
    			      repaint();
    			      u.update(dt);
    			}
    		 };
    	this.t =  new Timer(delay, counter);
    	this.t.start();
    }

    public double convertX(double x) {
    	double min = - u.scale();
    	double max = u.scale();
    	
    	return (this.getWidth()) * (x - min) / (max - min);
    }
    
    public double convertY(double y) {
    	double min = - u.scale();
    	double max = u.scale();
    	return (this.getHeight()) * (max - y) / (max - min);
    }

    public void paintComponent(Graphics g)
    {
		
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g2d);
		g2d.setColor(Color.red);

	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (Body b : this.u.bodies()) {
			g2d.setColor(b.color());
			g2d.fill(new Ellipse2D.Double(convertX(b.rx()), convertY(b.ry()), 5,5));
		}
    }

	public void stop() {
		t.stop();
	}

}