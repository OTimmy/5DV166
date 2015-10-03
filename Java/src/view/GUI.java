package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.network.ServerData;
//TODO add listener for jtable, so that selected row is showed in correct fields
/**
 * @author c12ton
 *
 * Graphical representation of the chat client.
 * Provide methods for adding listeners, and using the graphical
 * components like the text area.
 *
 * @version 0.0
 *
 */
public class GUI {

	private final int FRAME_WIDTH = 500;
	private final int FRAME_HEIGHT = 510;
	private final int CONF_PANEL_HEIGHT = 100;
	private final int CONF_PANEL_WIDTH  = 400;
	private final int TAB_PANEL_HEIGHT  = 100;
	private final int TAB_PANEL_WIDTH   = 400;

	private JFrame frame;
	private DefaultTableModel tableModel;

	//Used by configuration panel
	private JButton connectNameServerButton;
	private JButton connectServerButton;
	private JButton okButton;
	private JButton refreshButton;
	private JButton sendButton;

	private JTextField nameServerAddressField;
	private JTextField nameServerPortField;
	private JTextField serverAddressField;
	private JTextField serverPortField;
	private JTextField nickField;

	//Used by chat panel
	private JTextArea msgTextArea;
	private JTextArea usrsTextArea;
	private JTextArea sendTextArea;

	private JLabel browsErrLabel;

	public GUI() {

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

		/*Name server row*/
		//Label
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Name server:"),gbc);

		//Field
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		nameServerAddressField = new JTextField(15);
		nameServerAddressField.setText("itchy.cs.umu.se");
		panel.add(nameServerAddressField,gbc);

		//Label
		gbc.gridx++;
		panel.add(new JLabel("port:"),gbc);

		//Field
		gbc.gridx++;
		nameServerPortField = new JTextField(5);
		panel.add(nameServerPortField,gbc);

		//Button
		gbc.gridx++;
		connectNameServerButton = new JButton("Connect");
		panel.add(connectNameServerButton,gbc);


		/*Server row*/
		//Label
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Server:"),gbc);

		//Field
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		serverAddressField = new JTextField(15);
		panel.add(serverAddressField,gbc);

		//Label
		gbc.gridx++;
		panel.add(new JLabel("port:"),gbc);

		//Field
		gbc.gridx++;
		serverPortField = new JTextField(5);
		panel.add(serverPortField,gbc);

		//Button
		gbc.gridx++;
		gbc.gridwidth = 2;
		connectServerButton = new JButton("Connect");
		panel.add(connectServerButton,gbc);

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
		nickField = new JTextField(10);
		panel.add(nickField,gbc);

		//Button
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		okButton = new JButton("Ok");
		panel.add(okButton,gbc);

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

		/*Message board panal*/
		int msgPanelSize  = 370;
		int msgTextWidth  = 340;
		int msgTextHeight = 325;

		JPanel msgPanel = new JPanel(new BorderLayout());
		msgPanel.setPreferredSize(new Dimension(msgPanelSize,msgPanelSize));
		msgPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));

		msgTextArea = new JTextArea(1,2);
		msgTextArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(msgTextArea);
		scrollPane.setPreferredSize(new Dimension(msgTextWidth,msgTextHeight));

        msgPanel.add(scrollPane);

		/*User panel*/
		int usrsPanelWidth     = 100;
		int usrsPanelHeight    = 400;
		int usrsTextAreaWidth  = 100;
		int usrsTextAreaHeight = 325;

		JPanel usrPanel = new JPanel(new BorderLayout());
		usrPanel.setPreferredSize(new Dimension(usrsPanelWidth,usrsPanelHeight));
		usrPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		usrsTextArea = new JTextArea(1,2);
		usrsTextArea.setLineWrap(true);
		scrollPane = new JScrollPane(usrsTextArea);
		scrollPane.setPreferredSize(new Dimension(usrsTextAreaWidth,
		                                          usrsTextAreaHeight));

        usrPanel.add(scrollPane);

		/*Send panel*/
		int sendPaneHeight  = 43;
		int sendPaneWidth   = 365;

		JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		sendPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		sendTextArea = new JTextArea(1,2);
		sendTextArea.setLineWrap(true);
		scrollPane = new JScrollPane(sendTextArea);
		scrollPane.setPreferredSize(new Dimension(sendPaneWidth,sendPaneHeight));

		sendPanel.add(scrollPane);

		sendButton = new JButton("send");
        sendPanel.add(sendButton);

		panel.add(msgPanel,BorderLayout.WEST);
		panel.add(usrPanel,BorderLayout.EAST);
		panel.add(sendPanel,BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Contains the list of servers and buttons for managing the list
	 */
	private JPanel buildBrowsPanel() {
	    int panelSize = 400;
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(panelSize, panelSize));

		int tablePanelSize = 300;
		int southSize = 50;

		/*Server panel*/
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(tablePanelSize,tablePanelSize));
		northPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));

		String[] columns = {"Address","Port","Connected","Topic"};
		Object[][] data = new Object[21][4];
		tableModel = new DefaultTableModel(data,columns);

		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		northPanel.add(scrollPane,BorderLayout.CENTER);

		/*Panel with refresh*/
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel,BoxLayout.PAGE_AXIS));

		southPanel.setPreferredSize(new Dimension(southSize,southSize));
		southPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		//Error label
		browsErrLabel = new JLabel("");
		browsErrLabel.setForeground(Color.red);
		browsErrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		southPanel.add(browsErrLabel);

		refreshButton = new JButton("Refresh");
		refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		southPanel.add(refreshButton);

		panel.add(northPanel,BorderLayout.NORTH);
		panel.add(southPanel,BorderLayout.SOUTH);

		return panel;
	}
	
    public void addToServerList(ServerData server) {
        synchronized(tableModel) {
        	// if last row is not set then
        	   //for loop 0 to size of tableModel
        	   //tableModel.getValueAt(row, column)
        	   //if(row is empty)
        	      // then add to row
        	   //endif
        	//else
        	//addtolastrow
        }
    }
	
    public void clearServerList() {
        synchronized(tableModel) {
        	tableModel.setRowCount(0);
			//tableModel.addRow(null) 
        }
    }
	
    public synchronized void printOnMessageBoard(String msg) {
	    msgTextArea.append(msg +"\n");
    }

    public void printErrorChat(String errorMsg) {
	    printOnMessageBoard(errorMsg);
    }

    public void printErrorBrowser(String errorMsg) {
	    browsErrLabel.setText(errorMsg);
    }

    public String getSendTextArea() {

	    String text = sendTextArea.getText();
		sendTextArea.setText("");

		return text;
	}

	public String getNameServerAddress() {
	    return nameServerAddressField.getText();
	}

	public String getNameServerPort() {
	    return nameServerPortField.getText();
	}

	public String getServerAddress() {
	    return serverAddressField.getText();
	}

	public String getServerPort() {
	    return serverPortField.getText();
	}

	public String getNick() {
	    return nickField.getText();
	}

	public void addConnectNameServerButtonListener(ActionListener l) {
		connectNameServerButton.addActionListener(l);
	}

	public void addConnectSeverButtonListener(ActionListener l) {
		connectServerButton.addActionListener(l);
	}

	public void addOkButtonListener(ActionListener l) {
		okButton.addActionListener(l);
	}

	public void addRefreshButtonListener(ActionListener l) {
		refreshButton.addActionListener(l);
	}

	public void addSendButtonListener(ActionListener l) {
		sendButton.addActionListener(l);
	}
}