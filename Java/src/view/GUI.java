package view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import com.sun.security.sasl.ServerFactoryImpl;


//TODO Scroll should be adjusted to frame, not static size
//TODO set limit for characters in chat window
//TODO when connected, ignore talbe listener
/**
 * @author c12ton
 *
 * Graphical representation of the chat client.
 * Provide methods for adding listeners, and manipulating the graphical
 * components like the text area.
 *
 * @version 0.0
 *
 */
public class GUI {

	private final int FRAME_WIDTH = 590;
	private final int FRAME_HEIGHT = 520;
	private final int CONF_PANEL_HEIGHT = 100;
	private final int CONF_PANEL_WIDTH  = 400;
	private final int TAB_PANEL_HEIGHT  = 100;
	private final int TAB_PANEL_WIDTH   = 400;
	private final int NR_TABLE_COLUMNS  = 4;   //Starting values for table
	private final int NR_TABLE_ROWS     = 17;
	private final int TAB_BROWS         = 0;
	private final int TAB_CHAT          = 1;

    private final int KEY_ENTER = 10;
    private final int KEY_BACK_SPACE = 8;
    private final int SEND_MSG_LIMIT = 100;

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

	//used by tab
    JTabbedPane tabbedPane;

    //used by browser panel
	private JTable table;
	private String serverTopic;

	//Used by chat panel
	private JTextArea msgTextArea;
	private JTextArea usrsTextArea;
	private JTextArea sendTextArea;

	private JLabel browsErrLabel;

	public GUI() {

		frame = buildFrame();
		JPanel configPanel = buildConfigPanel();
		JPanel tabPanel    = buildTabPanel();

		initJTableListener();
		buildSendTextArea();

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
		nameServerPortField.setText("1337");
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
		nickField.setText("Neo");
		panel.add(nickField,gbc);

		//Button
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		okButton = new JButton("Ok");
		panel.add(okButton,gbc);

		/*Extra column on nick*/
		gbc.gridx+=2;
		gbc.anchor = GridBagConstraints.LINE_START;
		refreshButton = new JButton("Refresh");
		panel.add(refreshButton);

		return panel;
	}

	/**
	 * @return panel with two tabs, that contains the browser and chat
	 */
	private JPanel buildTabPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(TAB_PANEL_WIDTH,TAB_PANEL_HEIGHT));
		tabbedPane = new JTabbedPane();

		JPanel chatPanel  = buildChatPanelPanel();
		JPanel browsPanel = buildBrowsPanel();

		tabbedPane.addTab("Browse", browsPanel);
		tabbedPane.setMnemonicAt(TAB_BROWS, KeyEvent.VK_1);



		tabbedPane.addTab("Chat", chatPanel);
		tabbedPane.setMnemonicAt(TAB_CHAT, KeyEvent.VK_2);

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

		/*Message board panal*/
		int msgPanelSize  = 370;
		int msgTextWidth  = 340;
		int msgTextHeight = 325;

		JPanel msgPanel = new JPanel(new BorderLayout());
		msgPanel.setPreferredSize(new Dimension(msgPanelSize,msgPanelSize));

		msgTextArea = new JTextArea(1,2);
		msgTextArea.setWrapStyleWord(true);
		msgTextArea.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret) msgTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(msgTextArea);
		scrollPane.setPreferredSize(new Dimension(msgTextWidth,msgTextHeight));

        msgPanel.add(scrollPane);

		/*User panel*/
		int usrsPanelWidth     = 160; //100;
		int usrsPanelHeight    = 400;
		int usrsTextAreaWidth  = 100;
		int usrsTextAreaHeight = 325;

		JPanel usrPanel = new JPanel(new BorderLayout());
		usrPanel.setPreferredSize(new Dimension(usrsPanelWidth,usrsPanelHeight));

		usrsTextArea = new JTextArea(1,2);
		scrollPane = new JScrollPane(usrsTextArea,
		        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(usrsTextAreaWidth,
		                                          usrsTextAreaHeight));

        usrPanel.add(scrollPane);

		/*Send panel*/
		int sendPaneHeight  = 43;
		int sendPaneWidth   =  420;//365; //365;

		JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		sendTextArea = new JTextArea(1,2);
		sendTextArea.setWrapStyleWord(true);
		sendTextArea.setLineWrap(true);

		scrollPane = new JScrollPane(sendTextArea);
		scrollPane.setPreferredSize(new Dimension(sendPaneWidth,sendPaneHeight));

		sendPanel.add(scrollPane);

		sendButton = new JButton("send");
        sendPanel.add(sendButton);

		panel.add(msgPanel,BorderLayout.CENTER);
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

		String[] columns = {"Address","Port","Connected","Topic"};
		Object[][] data = new Object[NR_TABLE_ROWS][NR_TABLE_COLUMNS];
		tableModel = new DefaultTableModel(data,columns);

		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		northPanel.add(scrollPane,BorderLayout.CENTER);

		/*Panel with refresh*/
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel,BoxLayout.PAGE_AXIS));

		southPanel.setPreferredSize(new Dimension(southSize,southSize));

		//Error label
		browsErrLabel = new JLabel("");
		browsErrLabel.setForeground(Color.red);
		browsErrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		southPanel.add(browsErrLabel);

		panel.add(northPanel,BorderLayout.NORTH);
		panel.add(southPanel,BorderLayout.SOUTH);

		return panel;
	}

	             //Change to buildJTable
	private void initJTableListener() {
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseClicked(MouseEvent arg0) {

              SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					int row = table.getSelectedRow();
					String address = (String) table.getValueAt(row, 0);
					String port = (String) table.getValueAt(row, 1);
					serverTopic     = (String) table.getValueAt(row, 3);

					serverAddressField.setText(address);
					serverPortField.setText(port);
				}
			});

			}
		});
	}

	/**
	 * Builds a new JTextarea to be used for sending messages.
	 * It also handels the amount of characters that are allowed to be typed.
	 *
	 * @return text area with actionlistener
	 */
    private JTextArea buildSendTextArea() {
        JTextArea textArea = new JTextArea(1,1);

        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int size = sendTextArea.getText().length();
                if(size > SEND_MSG_LIMIT) {
                  final String outmsg = sendTextArea.getText().substring(0,
                                                size - (size - SEND_MSG_LIMIT));

                  SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        sendTextArea.setText(outmsg);
                    }
                });
              }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        return textArea;
    }


    public void addToServerList(String address, String port, String nrClients,
                                String name) {
        int row = tableModel.getRowCount() -1;
        String value = (String)tableModel.getValueAt(row, 0);


        Object[] rowData = {address,port,nrClients,name};

        //If last value is null then find the first empty (null) in table
        if(value == null) {
            int index = 0;
            for(; index < tableModel.getRowCount()
                        && tableModel.getValueAt(index, 0) != null; index++);

            tableModel.insertRow(index, rowData);
            tableModel.removeRow(tableModel.getRowCount() -1);
        } else { //if last value is not null  then add a new row
            tableModel.addRow(rowData);
        }
    }

    public void clearTable() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String[] columns = {"Address","Port","Connected","Topic"};
                Object[][] data = new Object[NR_TABLE_ROWS][NR_TABLE_COLUMNS];
                tableModel.setDataVector(data, columns);
            }
        });
    }

    public void addNick(final String nick) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                usrsTextArea.append(nick +"\n");
            }
        });

    }

    public void clearNicks() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                usrsTextArea.setText("");
            }
        });

    }

    public void printOnMessageBoard(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                msgTextArea.append(msg +"\n");
            }
        });

    }

    public void clearMessageBoard() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                msgTextArea.setText("");
            }
        });
    }

    public void setConnectNameServerButton(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                connectNameServerButton.setText(text);
            }
        });
    }

    public void setConnectServerButton(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                connectServerButton.setText(text);
            }
        });
    }

    public String getSendTextArea() {
        String text = sendTextArea.getText();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                sendTextArea.setText("");
                sendTextArea.setCaretPosition(0);
            }
        });

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

	public String getServerTopic() {
		return serverTopic;
	}

	public String[] getServerAtRow(int row) {
	    String[] server = new String[2];
	        server[0] = (String) tableModel.getValueAt(row, 0);
	        server[1] = (String) tableModel.getValueAt(row, 1);

	    return server;
	}

	public void openTab(int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public void setChatTabTitle(final String title) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				tabbedPane.setTitleAt(TAB_CHAT, title);
			}
		});
	}

    public void printErrorChat(String errorMsg) {
        printOnMessageBoard(errorMsg);
    }

    public void printErrorBrowser(String errorMsg) {
        browsErrLabel.setText(errorMsg);
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

	public void addTableListener(MouseListener l) {
	    table.addMouseListener(l);
	}

	public void addSendTextAreaListener(KeyListener keyListener) {
	    sendTextArea.addKeyListener(keyListener);
	}
}