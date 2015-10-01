package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

/**
 * @author c12ton
 *
 * Graphical represenation of the chat client
 *
 * @version 0.0
 *
 */
public class GUI {

	private final int FRAME_WIDTH = 480;
	private final int FRAME_HEIGHT = 600;
	private final int CONF_PANEL_HEIGHT = 100;
	private final int CONF_PANEL_WIDTH  = 400;

	private JFrame frame;
	private DefaultTableModel tableModel;

	public GUI() {
		//UILookAndFeel();
		frame = buildFrame();
		JPanel configPanel = buildConfigPanel();
		JPanel tabPanel    = buildTabPanel();


		frame.add(configPanel,BorderLayout.NORTH);
		frame.add(tabPanel,BorderLayout.CENTER);
		frame.revalidate();
	}


    private JFrame buildFrame() {
        JFrame frame = new JFrame("Client");

        /*Frame settings*/
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLayout(new BorderLayout());
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.white);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        return frame;
	}


	private JPanel buildConfigPanel() {
		JPanel panel  = new JPanel();

		/*panel settings*/
		panel.setPreferredSize(new Dimension(CONF_PANEL_WIDTH, CONF_PANEL_HEIGHT));
		panel.setBorder(BorderFactory.createLineBorder(Color.red));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets.bottom = 2;
		gbc.insets.right = 5;

		/*Server name row*/
		//Label
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Name server:"),gbc);

		//Field
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		JTextField hostField = new JTextField(15);
		panel.add(hostField,gbc);

		gbc.gridx++;
		panel.add(new JLabel("port:"),gbc);

		gbc.gridx++;
		JTextField portField = new JTextField(5);
		panel.add(portField,gbc);

		gbc.gridx++;
		JButton button = new JButton("Connect");
		panel.add(button,gbc);


		/*Server row*/
		//Label
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Server:"),gbc);

		//Field
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		JTextField serverField = new JTextField(15);
		panel.add(serverField,gbc);

		//Label
		gbc.gridx++;
		panel.add(new JLabel("port:"),gbc);

		//Field
		gbc.gridx++;
		JTextField serverPortField = new JTextField(5);
		panel.add(serverPortField,gbc);

		//Button
		gbc.gridx++;
		gbc.gridwidth = 2;
		button = new JButton("Connect");
		panel.add(button,gbc);

		/*Nick row*/
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(new JLabel("Nick"),gbc);

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		JTextField nickField = new JTextField(10);
		panel.add(nickField,gbc);

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		button = new JButton("Ok");
		panel.add(button,gbc);

		return panel;
	}

	/**
	 * @return panel with two tabs, that contains the browser and chat
	 */
	private JPanel buildTabPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(100,400));
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel chatPanel = buildChatPanelPanel();
		JPanel browsPanel = buildBrowsPanel();
		tabbedPane.addTab("Browse", browsPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


		tabbedPane.addTab("Chat", chatPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		panel.add(new JButton("Refresh"));
		panel.add(tabbedPane,BorderLayout.CENTER);

		return panel;
	}


	private JPanel buildChatPanelPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(frame.WIDTH - CONF_PANEL_WIDTH,
										     frame.HEIGHT - CONF_PANEL_HEIGHT));
		panel.setBorder(BorderFactory.createLineBorder(Color.green));
		panel.setBackground(Color.white);


		/*Message panal*/
		int msgPanelSize  = 400;
		int txtOutHeight  = 360;
		int txtOutWidth   = 330;

		JPanel msgPanel = new JPanel();
		msgPanel.setPreferredSize(new Dimension(msgPanelSize,msgPanelSize));
		msgPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));

		JTextArea txtAreaInput = new JTextArea(1,2);
		txtAreaInput.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(txtAreaInput);
		scrollPane.setPreferredSize(new Dimension(txtOutWidth,txtOutHeight));

		msgPanel.add(scrollPane);


		/*User panel*/
		int usrPanelWidth  = 100;
		int usrPanelHeight = 400;
		int txtUsrWidth    = 100;
		int txtUsrHeight   = 400;

		JPanel usrPanel = new JPanel();
		usrPanel.setPreferredSize(new Dimension(usrPanelWidth,usrPanelHeight));
		usrPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		JTextArea txtAreaUsr = new JTextArea(1,2);
		scrollPane = new JScrollPane(txtAreaUsr);
		scrollPane.setPreferredSize(new Dimension(txtUsrWidth,txtUsrHeight));
		usrPanel.add(scrollPane);


		/*Chat panel (JtextArea + JButton)*/
		int panelOutHeight = 100;
		int panelOutWidth  = 400;
		int paneOutHeight  = 400;
		int paneOutWidth   = 100;

		JPanel writePanel = new JPanel();
		writePanel.setPreferredSize(new Dimension(panelOutWidth,panelOutHeight));
		writePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JTextArea txtArea = new JTextArea(1,2);
		scrollPane = new JScrollPane(txtArea);
		scrollPane.setPreferredSize(new Dimension(paneOutWidth,panelOutHeight));
		writePanel.add(scrollPane);


		panel.add(msgPanel,BorderLayout.CENTER);
		panel.add(usrPanel,BorderLayout.EAST);
		panel.add(writePanel,BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Contains the list of servers and buttons for managing the list
	 */
	private JPanel buildBrowsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(CONF_PANEL_WIDTH, CONF_PANEL_HEIGHT));

		int northSize = 350;
		int southSize = 50;

		/*Server panel*/
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(northSize,northSize));
		northPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));

		String[] columns = {"Address","Port","Connected","Topic"};
		Object[][] data = new Object[21][4];
		tableModel = new DefaultTableModel(data,columns);

		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		northPanel.add(scrollPane,BorderLayout.CENTER);

		/*Panel with refresh and connect*/
		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(southSize,southSize));
		southPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		//JButton button = new JButton("Connect");
		//southPanel.add(button,BorderLayout.EAST);
		JButton button = new JButton("Refresh");
		southPanel.add(button,BorderLayout.EAST);

		panel.add(northPanel,BorderLayout.NORTH);
		panel.add(southPanel,BorderLayout.SOUTH);

		return panel;
	}

}
