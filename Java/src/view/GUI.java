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
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.PlainDocument;


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

    private final int FRAME_WIDTH       = 590;
    private final int FRAME_HEIGHT      = 520;
    private final int CONF_PANEL_HEIGHT = 100;
    private final int CONF_PANEL_WIDTH  = 400;
    private final int TAB_PANEL_HEIGHT  = 100;
    private final int TAB_PANEL_WIDTH   = 400;
    private final int TAB_BROWS         = 0;    //Index values for tabbedpane
    private final int TAB_CHAT          = 1;
    private final int TAB_MAX_NAME      = 20;
    private final int SEND_MSG_LIMIT    = 65535;  //From the spec
    private final int NICK_LIMIT        = 255;


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

    public GUI(String nameServerAddress,String nameServerPort,
               String serverAddress, String serverPort) {

        setup();

        nameServerAddressField.setText(nameServerAddress);
        nameServerPortField.setText(nameServerPort);
        serverAddressField.setText(serverAddress);
        serverPortField.setText(serverPort);

    }

    public GUI(final String nameServerAddress,final String nameServerPort) {
        setup();

        nameServerAddressField.setText(nameServerAddress);
        nameServerPortField.setText(nameServerPort);
    }

    private void setup() {
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


    /**
     * @return
     */
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
        panel.add(nameServerAddressField,gbc);

        //Label
        gbc.gridx++;
        panel.add(new JLabel("port:"),gbc);

        //Field
        gbc.gridx++;
        nameServerPortField = new JTextField(5);
        panel.add(nameServerPortField,gbc);

        //Button Refresh
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        refreshButton = new JButton("Refresh");
        panel.add(refreshButton);

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

        ((PlainDocument) nickField.getDocument()).setDocumentFilter(
                                                new TextAreaFilter(NICK_LIMIT));
        nickField.setText("Neo");

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
		int sendPaneWidth   = 420;

		JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		sendTextArea = buildSendTextArea();
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
    * Builds a new JTextarea to be used for sending messages.
    * It also handels the amount of characters that are allowed to be typed.
    *
    * @return text area with actionlistener
    */
   private JTextArea buildSendTextArea() {
       JTextArea textArea = new JTextArea(1,2);
       textArea.setWrapStyleWord(true);
       textArea.setLineWrap(true);
       TextAreaFilter filter = new TextAreaFilter(SEND_MSG_LIMIT);

      ( (PlainDocument) textArea.getDocument()).setDocumentFilter(filter);

       return textArea;
   }

	/**
	 * Contains the list of servers and buttons for managing the list
	 * @return
	 */
	private JPanel buildBrowsPanel() {
	    int panelSize = 400;
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(panelSize, panelSize));
		panel.setBackground(Color.white);

		int southSize = 40;

		/*Server panel*/
		JPanel northPanel = new JPanel(new BorderLayout());
		//northPanel.setPreferredSize(new Dimension(tablePanelSize,tablePanelSize));
		northPanel.setBackground(Color.white);

		table = buildTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(Color.white);
		scrollPane.getViewport().setBackground(Color.white);
		northPanel.add(scrollPane,BorderLayout.CENTER);

		/*Panel with error message*/
		JPanel southPanel = new JPanel();
		//southPanel.setLayout(new BoxLayout(southPanel,BoxLayout.PAGE_AXIS));
		southPanel.setPreferredSize(new Dimension(southSize,southSize));

		//Error label
		browsErrLabel = new JLabel("");
		browsErrLabel.setForeground(Color.red);
		browsErrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		browsErrLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		southPanel.add(browsErrLabel,BorderLayout.CENTER);

		panel.add(southPanel,BorderLayout.SOUTH);
		panel.add(northPanel,BorderLayout.CENTER);
		return panel;
	}

    /**
     * @return
     */
     private JTable buildTable() {
	     String[] columns = {"Address","Port","Connected","Topic"};
	     tableModel = new DefaultTableModel(null,columns);

	     final JTable table = new JTable(tableModel);

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
                       int row         = table.getSelectedRow();
                       String address  = (String) table.getValueAt(row, 0);
                       String port     = (String) table.getValueAt(row, 1);
                       serverTopic     = (String) table.getValueAt(row, 3);

                       serverAddressField.setText(address);
	                   serverPortField.setText(port);
                   }
               });

               }
        });

	    table.setShowGrid(false);
        return table;
    }

	/**
	 * Is used to add server information to the graphical table.
	 *
	 * @param address for server
	 * @param nrClients is the number of clients of the current server
	 * @param name the server name
	 */
    public void addToTable(final String address, final String port, final String nrClients,
                                final String name) {

        SwingUtilities.invokeLater(new Runnable() {


		@Override
		public void run() {
			Object[] rowData = {address,port,nrClients,name};
	        tableModel.addRow(rowData);
		}

        });
    }

    /**
     *  Removes all
     */
    public void clearTable() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String[] columns = {"Address","Port","Connected","Topic"};
                tableModel.setDataVector(null, columns);
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

    public void setConnectServerButton(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                connectServerButton.setText(text);
            }
        });
    }

    public void setChatTabTitle(final String argTitle) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                String title;
                if(argTitle.length() > TAB_MAX_NAME) {

                    title = argTitle.substring(0, argTitle.length()
                            - (argTitle.length() - TAB_MAX_NAME));
                    title+= "...";
                } else {
                    title = argTitle;
                }

                tabbedPane.setTitleAt(TAB_CHAT, title);
            }
        });
    }

    public void openTab(int index) {
        tabbedPane.setSelectedIndex(index);
    }

    public String getSendTextArea() {
        String text = sendTextArea.getText();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                synchronized(sendTextArea) {
                    sendTextArea.setText("");
                    sendTextArea.setCaretPosition(0);
                }
            }
        });

	    return text;
	}


    public void printErrorBrowser(final String errorMsg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                browsErrLabel.setText(errorMsg);
            }
        });

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