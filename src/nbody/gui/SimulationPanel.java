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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import nbody.model.Body;
import nbody.model.universe.*;


/**
 * Panel donde se anima la simulaci�n del movimiento de los N-cuerpos.
 * @author jlasarte
 */
public class SimulationPanel extends JPanel {
	/**
	 * serialVerison
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * delay, en milisegundos, utilizado para refrescar la pantalla.
	 */
	public int delay 	= 5; 	//milliseconds
	/**
	 * Variable UniverseTemplate que contiene al universo actual.
	 */
    private UniverseTemplate u;
    /**
     * Timer utilizado para refrescar la pantalla.
     */
    private Timer t;
    /**
     * Boton para resetear la simulacion
     */
	private JButton reset;
	/**
	 * Universo actual
	 */
	private String selected_universe;
	private JComboBox<String> N;
	private String[] proc = {"1","2","4","8","16","32","64"};
	private JLabel proc_label;
	/**
	 * Constructor.
	 */
    public SimulationPanel()
    {
    	this.u = new BarnesHutUniverse();
		this.reset = new JButton("Reset");
		this.N = new JComboBox<String>(proc);
		this.reset.setVisible(false);
		
		this.proc_label = new JLabel("Threads");
		this.proc_label.setForeground(Color.WHITE);
		
		this.add(reset);
		this.add(this.proc_label);
		this.add(N);
		
		this.proc_label.setVisible(false);
		N.setVisible(false);
		
		this.reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				reset();
			}
		});

    }
    
    /**
     * Resetea el universo actual con los datos originales.
     */
    protected void reset() {
		this.initialize(selected_universe);
		this.repaint();
	}

    /**
     * Inicializa el panel, cargando la simulacion.
     * @param universe el archivo donde se encuentran los cuerpos a cargar.
     */
	public void initialize(String universe) {
		// TODO: revisar el caso de que se abra un archivo con errores o con el formato incorrecto.
    	this.selected_universe = universe;
    	try {
			u.initialize(universe);
	    	this.reset.setVisible(true);
	    	this.repaint();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Error al abrir el archivo.");
		}

    }
    
	/**
	 * Comienza la simulacion
	 * @param dt el delta tiempo para utilizar en las simulaciones.
	 */
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

    /**
     * Convierte una coordenada cartesiana X en un punto en el frame utilizando el radio del universo actual.
     * @param x coordenada cartesiana sobre el eje X
     * @return punto en el frame.
     */
    public double convertX(double x) {
    	double min = - u.scale();
    	double max = u.scale();
    	
    	return (this.getWidth()) * (x - min) / (max - min);
    }
    
    /**
     * Convierte una coordenada cartesiana Y en un punto en el frame untilizando el radio del universo actual.
     * @param y coordenada cartesiana sobre el eje Y
     * @return punto en el frame.
     */
    public double convertY(double y) {
    	double min = - u.scale();
    	double max = u.scale();
    	return (this.getHeight()) * (max - y) / (max - min);
    }

    public void paintComponent(Graphics g)
    {
		
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g2d);

	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    drawCartesianAxis(g2d);
	    for (Body b : this.u.bodies()) {
			g2d.setColor(b.color());
			g2d.fill(new Ellipse2D.Double(convertX(b.rx()), convertY(b.ry()), 5,5));
		}
    }

	private void drawCartesianAxis(Graphics2D g2d) {
		g2d.setColor(Color.GRAY);
	    g2d.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
	    g2d.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);

	}
    
    /**
     * Detiene la simulacion.
     */
	public void stop() {
		t.stop();
	}

	public void setUniverse(String simulation) {
		switch (simulation) {
		case "barnes" : 
			this.u = new BarnesHutUniverse();
			proc_label.setVisible(false);
			N.setVisible(false);
			break;
		case "brute":
			this.u = new SecuentialBruteForceUniverse();
			proc_label.setVisible(false);
			N.setVisible(false);
			break;
		case "brutep":
			proc_label.setVisible(true);
			N.setVisible(true);
			this.u = new ParallelBruteForceUniverse(Integer.parseInt(this.N.getSelectedItem().toString()));
			break;
		case "barnesp":
			proc_label.setVisible(true);
			N.setVisible(true);
			this.u = new ParallelBarnesHutUniverse(Integer.parseInt(this.N.getSelectedItem().toString()));
			break;
		case "barnespb":
			proc_label.setVisible(true);
			N.setVisible(true);
			this.u = new ParallelBalancedBarnesHutUniverse(Integer.parseInt(this.N.getSelectedItem().toString()));
			break;
		default:
			JOptionPane.showMessageDialog(null, "Algoritmo no permitido");
            break;
           
		}
		try {
			this.u.initialize(selected_universe);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Error al abrir el archivo.");
		}
		this.repaint();
		
	}

}