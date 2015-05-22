package nbody.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private SimulationPanel simulationPanel;
	private boolean simulation_running;
	private final Action action = new SwingAction("Abrir ARCHIV");
    private JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    private String selected_universe = "";
    private JMenuBar menu_bar;
    private JTextField delta_t_field;
    private JTextField time_field;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void buildMenu() {

		this.menu_bar = new JMenuBar();
		setJMenuBar(this.menu_bar);
		
		JMenu main_menu = new JMenu("Archivo");
		this.menu_bar.add(main_menu);
		
		JMenuItem menu_item_abrir = new JMenuItem();
		menu_item_abrir.setAction(action);
		main_menu.add(menu_item_abrir);
		
		
		JMenuItem mntmGug = new JMenuItem("Gug");
		main_menu.add(mntmGug); 
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
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
		
		time_field = new JTextField();
		panel.add(time_field);
		time_field.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("DeltaT");
		panel.add(lblNewLabel);
		
		delta_t_field = new JTextField();
		panel.add(delta_t_field);
		delta_t_field.setColumns(10);
		
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setIcon(new ImageIcon(MainFrame.class.getResource("/com/sun/javafx/webkit/prism/resources/mediaPlayDisabled.png")));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!simulation_running) {
					simulationPanel.start(Double.parseDouble(delta_t_field.getText()));
					simulation_running = true;
					btnNewButton.setText("Detener Simulacion");
					menu_bar.getMenu(0).getItem(0).setEnabled(false); //very negro
				} else {
					simulation_running = false;
					simulationPanel.stop();
					btnNewButton.setText("Iniciar Simulacion");
					menu_bar.getMenu(0).getItem(0).setEnabled(true); //very negro
				}
			}
		});
		

		panel.add(btnNewButton);

	}
	
	public void what() {
		int retval = fileChooser.showOpenDialog(contentPane);
		 if (retval == JFileChooser.APPROVE_OPTION) {
               //... The user selected a file, get it, use it.
               File file = fileChooser.getSelectedFile();
               selected_universe = file.getPath();
               simulationPanel.initialize(selected_universe);
		 }
	}
	
	private class SwingAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6888240617193674700L;
		public SwingAction(String title) {
			putValue(NAME, "Abrir Archivo");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			what();
		}
	}
}
