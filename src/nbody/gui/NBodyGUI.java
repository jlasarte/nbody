package nbody.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JTextField;

import java.awt.Toolkit;

/**
 * Aplicación gráfica para la simulación, con visualización de los cuerpos y su movimiento.
 * @author julia
 */
public class NBodyGUI extends JFrame {
	
	/**
	 * variable para seialización.
	 */
	private static final long serialVersionUID = 5439677820771990289L;
	/**
	 * Panel principal del contenido.
	 */
	private JPanel contentPane;
	/**
	 * Panel que contiene la simulación.
	 */
	private SimulationPanel simulationPanel;
	/**
	 * Variable para indicar si actualmente se esta corriendo la simulación.
	 */
	private boolean simulation_running;
	/**
	 * Acción que se ejecuta cuando el usuario habre un archivo.
	 */
	private final Action action = new SwingAction("Abrir Archivo");
	
	/**
	 * Seleccionador de archivo
	 */
    private JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    /**
     * Universo actual, seleccionado por el usuario.
     */
    private String selected_universe = "";
    /**
     * Barra de menu.
     */
    private JMenuBar menu_bar;
    /**
     * Delta tiempo elegido por el usuario
     */
    private JTextField delta_t_field;
	
    /**
	 * Inicia la aplicación.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NBodyGUI frame = new NBodyGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Construye el menu de la aplicación.
	 */
	private void buildMenu() {

		this.menu_bar = new JMenuBar();
		setJMenuBar(this.menu_bar);
		
		JMenu main_menu = new JMenu("Archivo");
		this.menu_bar.add(main_menu);
		
		JMenu sim_menu = new JMenu("Simulacion");
		this.menu_bar.add(sim_menu);
		
		JMenuItem menu_item_abrir = new JMenuItem();
		menu_item_abrir.setAction(action);
		main_menu.add(menu_item_abrir);
		
		JMenuItem barnes = new JCheckBoxMenuItem("Barnes Hut");
		sim_menu.add(barnes);
		
		JMenuItem barnesp = new JCheckBoxMenuItem("Barnes Hut Paralelo");
		sim_menu.add(barnesp);
		
		JMenuItem barnespb = new JCheckBoxMenuItem("Barnes Hut Paralelo ORB");
		sim_menu.add(barnespb);
		
		JMenuItem brute = new JCheckBoxMenuItem("MetodoPP");
		sim_menu.add(brute);
		
		JMenuItem brutep = new JCheckBoxMenuItem("MétodoPP Paralelo");
		sim_menu.add(brutep);
		
		barnes.setSelected(true);
		
		barnes.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (barnes.isSelected()) {
					simulationPanel.setUniverse("barnes");
			        barnesp.setSelected(false);
			        barnespb.setSelected(false);
			        brute.setSelected(false);
			        brutep.setSelected(false);
				}
			}
	    });
		
		barnesp.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (barnesp.isSelected()) {
					simulationPanel.setUniverse("barnesp");
			        barnes.setSelected(false);
			        barnespb.setSelected(false);
			        brute.setSelected(false);
			        brutep.setSelected(false);
				}
			}
	    });
		
		barnespb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (barnespb.isSelected()) {
					simulationPanel.setUniverse("barnespb");		        
					barnes.setSelected(false);
			        barnesp.setSelected(false);
			        brute.setSelected(false);
			        brutep.setSelected(false);
			        
				}
			}
	    });
		
		brute.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (brute.isSelected()) {
					simulationPanel.setUniverse("brute");
			        barnes.setSelected(false);
			        barnesp.setSelected(false);
			        barnespb.setSelected(false);
			        brutep.setSelected(false);
				}
			}
	    });
		
		brutep.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (brutep.isSelected()) {
					simulationPanel.setUniverse("brutep");
					barnes.setSelected(false);
			        barnesp.setSelected(false);
			        barnespb.setSelected(false);
			        brute.setSelected(false);
				}
			}
	    });
	}

	/**
	 * Crea el frame principal de la aplicación.
	 */
	public NBodyGUI() {
		setTitle("NBodyGui");
		setIconImage(Toolkit.getDefaultToolkit().getImage(NBodyGUI.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 640);
		this.simulation_running = false;
		
		this.buildMenu();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		this.simulationPanel = new SimulationPanel();
		simulationPanel.setBackground(Color.BLACK);
		contentPane.add(simulationPanel, BorderLayout.CENTER);
		

		JButton btnNewButton = new JButton("Iniciar Simulacion");
		
		JLabel lblTt = new JLabel("Tiempo");
		panel.add(lblTt);
		
		
		JLabel lblNewLabel = new JLabel("DeltaT");
		panel.add(lblNewLabel);
		
		delta_t_field = new JTextField();
		panel.add(delta_t_field);
		delta_t_field.setText("1");
		delta_t_field.setColumns(10);
		
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setIcon(new ImageIcon(NBodyGUI.class.getResource("/com/sun/javafx/webkit/prism/resources/mediaPlayDisabled.png")));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!simulation_running) {
					simulationPanel.start(Double.parseDouble(delta_t_field.getText()));
					simulation_running = true;
					btnNewButton.setText("Detener Simulacion");
					menu_bar.getMenu(0).getItem(0).setEnabled(false); //very negro
					menu_bar.getMenu(1).setEnabled(false); //very negro

				} else {
					simulation_running = false;
					simulationPanel.stop();
					btnNewButton.setText("Iniciar Simulacion");
					menu_bar.getMenu(0).getItem(0).setEnabled(true); //very negro
					menu_bar.getMenu(1).setEnabled(true); //very negro
				}
			}
		});
		

		panel.add(btnNewButton);

	}
	
	/**
	 * Abre un archivo de cuerpos y carga los cuerpos en el universo actual.
	 */
	public void openFile() {
		int retval = fileChooser.showOpenDialog(contentPane);
		 if (retval == JFileChooser.APPROVE_OPTION) {
               //... The user selected a file, get it, use it.
               File file = fileChooser.getSelectedFile();
               selected_universe = file.getPath();
               simulationPanel.initialize(selected_universe);
		 }
	}
	
	/**
	 * Acción ejecutada cuando se selecciona en el menu "abrir archivo".
	 *
	 */
	private class SwingAction extends AbstractAction {

		private static final long serialVersionUID = 6888240617193674700L;
		/**
		 * Constructor
		 * @param title  El titulo de la acción.
		 */
		public SwingAction(String title) {
			putValue(NAME, "Abrir Archivo");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		/**
		 * Evento que se ejecuta cuando la acción ocurre.
		 * @param e evento.
		 */
		public void actionPerformed(ActionEvent e) {
			openFile();
		}
	}

}
