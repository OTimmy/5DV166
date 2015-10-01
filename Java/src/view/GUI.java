package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
	private final int FRAME_HEIGHT = 568;
	private final int CONF_PANEL_HEIGHT = 100;
	private final int CONF_PANEL_WIDTH  = 400;
	private final int TAB_PANEL_HEIGHT  = 100;
	private final int TAB_PANEL_WIDTH   = 400;

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

		//Label
		gbc.gridx++;
		panel.add(new JLabel("port:"),gbc);

		//Field
		gbc.gridx++;
		JTextField portField = new JTextField(5);
		panel.add(portField,gbc);

		//Button
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
		//Label
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(new JLabel("Nick"),gbc);

		//Field
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		JTextField nickField = new JTextField(10);
		panel.add(nickField,gbc);

		//Button
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
		panel.setPreferredSize(new Dimension(TAB_PANEL_WIDTH,TAB_PANEL_HEIGHT));
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel chatPanel  = buildChatPanelPanel();
		JPanel browsPanel = buildBrowsPanel();

		tabbedPane.addTab("Browse", browsPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


		tabbedPane.addTab("Chat", chatPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		panel.add(new JButton("Refresh"));
		panel.add(tabbedPane,BorderLayout.CENTER);

		return panel;
	}

	/**
	 * @return Panel containing three panels.
	 */
	private JPanel buildChatPanelPanel() {
	    /*Main panel*/
	    int panelSize = 400;
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(panelSize,
										     panelSize));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel.setBackground(Color.white);

		/*Message panal*/
		int msgPanelSize  = 370;
		int txtOutWidth   = 340;
		int txtOutHeight  = 325;

		JPanel msgPanel = new JPanel(new BorderLayout());
		msgPanel.setPreferredSize(new Dimension(msgPanelSize,msgPanelSize));
		msgPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));

		JTextArea txtAreaInput = new JTextArea(1,2);
		txtAreaInput.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(txtAreaInput);
		scrollPane.setPreferredSize(new Dimension(txtOutWidth,txtOutHeight));

		//msgPanel.add(scrollPane,BorderLayout.WEST);
        msgPanel.add(scrollPane);

		/*User panel*/
		int usrPanelWidth  = 100;
		int usrPanelHeight = 400;
		int txtUsrWidth    = 100;
		int txtUsrHeight   = 325;

		JPanel usrPanel = new JPanel(new BorderLayout());
		usrPanel.setPreferredSize(new Dimension(usrPanelWidth,usrPanelHeight));
		usrPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		JTextArea txtAreaUsr = new JTextArea(1,2);
		txtAreaUsr.setLineWrap(true);
		scrollPane = new JScrollPane(txtAreaUsr);
		scrollPane.setPreferredSize(new Dimension(txtUsrWidth,txtUsrHeight));

        usrPanel.add(scrollPane);

		/*Chat panel (JtextArea + JButton)*/
		int panelOutHeight = 50;
		int panelOutWidth  = 400;
		int paneOutHeight  = 43;
		int paneOutWidth   = 365;

		JPanel writePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		writePanel.setPreferredSize(new Dimension(panelOutWidth,panelOutHeight));
		writePanel.setBorder(BorderFactory.createLineBorder(Color.black));

		JTextArea txtAreaWrite = new JTextArea(1,2);
		txtAreaWrite.setLineWrap(true);
		scrollPane = new JScrollPane(txtAreaWrite);
		scrollPane.setPreferredSize(new Dimension(paneOutWidth,paneOutHeight));

		writePanel.add(scrollPane);
        writePanel.add(new JButton("Send"));

		panel.add(msgPanel,BorderLayout.WEST);
		panel.add(usrPanel,BorderLayout.EAST);
		panel.add(writePanel,BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Contains the list of servers and buttons for managing the list
	 */
	private JPanel buildBrowsPanel() {
	    int panelSize = 400;
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(panelSize, panelSize));

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

		JButton button = new JButton("Refresh");
		southPanel.add(button);

		panel.add(northPanel,BorderLayout.NORTH);
		panel.add(southPanel,BorderLayout.SOUTH);

		return panel;
	}


	/**
	 *
	 */
	public void printError(String errorMsg) {

	}

}
