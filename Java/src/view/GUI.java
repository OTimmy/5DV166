package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
	//------------int panel_height etc
	private final int CONF_PANEL_HEIGHT = 100;
	private final int CONF_PANEL_WIDTH  = 400;
	private final int MSG_PANEL_HEIGHT  = 400;
	private final int MSG_PANEL_WIDTH   = 300;
	private final int USR_PANEL_HEIGHT  = 400;
	private final int USR_PANEL_WIDTH   = 100;
	private final int WRT_PANEL_HEIGHT  = 100;
	private final int WRT_PANEL_WIDTH   = 400;
	
	private JFrame frame;
	
	public GUI() {
		UILookAndFeel();
		frame = buildFrame();
		JPanel configPanel = buildConfigPanel();
		JPanel tabPanel    = buildTabPanel();
		
		
		
		
		frame.add(configPanel,BorderLayout.NORTH);

		frame.add(tabPanel,BorderLayout.CENTER);
		frame.setVisible(true);
		frame.revalidate();
	}

	
	private void UILookAndFeel() {
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Name server:"),gbc);
		
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
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Server:"),gbc);
		
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx++;
		JTextField serverField = new JTextField(15);
		panel.add(serverField,gbc);
			
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
		
		gbc.gridx++;
		gbc.gridwidth = 2;
		button = new JButton("Ok");
		panel.add(button,gbc);

		return panel;
	}
	
	private JPanel buildTabPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(100,400));
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel chatPanel = buildChatPanelPanel();
		
		tabbedPane.addTab("Browser", new JLabel("Browser"));
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		
		tabbedPane.addTab("Chat", chatPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		panel.add(tabbedPane);
		
		return panel;
	}

	
	private JPanel buildChatPanelPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(frame.WIDTH - CONF_PANEL_WIDTH,
										     frame.HEIGHT - CONF_PANEL_HEIGHT));
		panel.setBorder(BorderFactory.createLineBorder(Color.green));
		panel.setBackground(Color.white);
		
		
		/*Message panal*/
		JPanel msgPanel = new JPanel();
		msgPanel.setPreferredSize(new Dimension(MSG_PANEL_WIDTH,MSG_PANEL_HEIGHT));
		msgPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));
		
		
		/*User panel*/
		JPanel usrPanel = new JPanel();
		usrPanel.setPreferredSize(new Dimension(USR_PANEL_WIDTH,USR_PANEL_HEIGHT));
		usrPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		
		/*Chat panel (JtextArea + JButton)*/
		JPanel writePanel = new JPanel();
		writePanel.setPreferredSize(new Dimension(WRT_PANEL_WIDTH,WRT_PANEL_HEIGHT));
		writePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		panel.add(msgPanel,BorderLayout.CENTER);
		panel.add(usrPanel,BorderLayout.EAST);
		panel.add(writePanel,BorderLayout.SOUTH);
		return panel;
	}
	
	private void buildServerPanel() {
		
	}
	
	/*Implement general error message window, to be used by Listsener*/
	//errorMsg(int type,String errmsg)
}
