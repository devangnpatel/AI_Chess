package chess;

import game.network.Client;
import game.network.Server;
import game.utility.Properties.PlayerColor;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author devang
 */
public class ChessNetworkWindow implements ActionListener {
    
    public Client client;
    
    private Frame gameFrame;
    private JTextField serverTextField;
    private JTextField portTextField;
    
    private JButton startClientButton;
    private JButton launchServerButton;
    
    private JLabel serverLabel;
    private JLabel portLabel;
    
    private String gameName = "AI CHESS";
    
    private InformationPanel statusPanel;
    private JPanel networkInfoPanel;
    private JPanel networkButtonsPanel;
    
    /**
    * InformationPanel
    * - holds a label with text that can be easily updated
    * @author devang
    */
   public class InformationPanel extends JPanel {
	   private static final long serialVersionUID = 1L;
	   
	   private String informationText;
       private JLabel informationLabel;

       public InformationPanel(String informationText)
       {
           this.informationText = informationText;
           informationLabel = new JLabel(informationText,SwingConstants.CENTER);
           informationLabel.setVisible(true);
           add(informationLabel);
           setVisible(true);
       }

       public InformationPanel()
       {
           this("");
       }

       public void setInformationText(String informationText)
       {
           this.informationText = informationText;
           informationLabel.setText(informationText);
           repaint();
       }

       public String getinformationText()
       {
           return informationText;
       }
   }

    public ChessNetworkWindow(int level) {
        init();
    }
    
    public final void init()
    {
        gameFrame = new Frame("Network Chess");
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                gameFrame.dispose();
                //System.exit(0);
            }
        });

        gameFrame.add(gamePanel());
        int xLocation = (int)Math.floor(Math.random()*500);
        int yLocation = (int)Math.floor(Math.random()*400);
        gameFrame.setLocation(xLocation,yLocation);
        gameFrame.setSize(320, 180); 
        gameFrame.setResizable(false);
        //gameFrame.pack();
        gameFrame.setVisible(true);
    }
    
    public JPanel gamePanel()
    {
        JPanel gamePanel = new JPanel();
        GridLayout gamePanelLayout = new GridLayout(3,1);
        gamePanel.setLayout(gamePanelLayout);

        networkInfoPanel = networkInfoPanel();
        networkButtonsPanel = networkButtonsPanel();
        statusPanel = new InformationPanel();
        
        gamePanel.add(networkInfoPanel);
        gamePanel.add(networkButtonsPanel);
        gamePanel.add(statusPanel);

        startClientButton.setEnabled(true);
        launchServerButton.setEnabled(true);
        serverTextField.setEnabled(true);
        portTextField.setEnabled(true);
        serverLabel.setEnabled(true);
        portLabel.setEnabled(true);
        
        return gamePanel;
    }
    
    public JPanel networkInfoPanel()
    {
        JPanel infoPanel = new JPanel();
        GridLayout networkInfoPanelLayout = new GridLayout(2,2);
        infoPanel.setLayout(networkInfoPanelLayout);
        serverLabel = new JLabel("  server:",SwingConstants.LEFT);
        portLabel = new JLabel("  port:",SwingConstants.LEFT);
        serverTextField = new JTextField();
        portTextField = new JTextField();
        serverTextField.setText("127.0.0.1");
        portTextField.setText("8080");
        serverLabel.setVisible(true);
        portLabel.setVisible(true);
        serverTextField.setVisible(true);
        portTextField.setVisible(true);
        infoPanel.add(serverLabel);
        infoPanel.add(portLabel);
        infoPanel.add(serverTextField);
        infoPanel.add(portTextField);
        infoPanel.setVisible(true);

        return infoPanel;
    }
    
    public JPanel networkButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel();
        GridLayout networkButtonsPanelLayout = new GridLayout(2,1);
        networkButtonsPanel.setLayout(networkButtonsPanelLayout);
        startClientButton = new JButton("Connect Client");
        launchServerButton = new JButton("Launch Server");
        startClientButton.setVisible(true);
        launchServerButton.setVisible(true);
        buttonsPanel.add(startClientButton);
        buttonsPanel.add(launchServerButton);
        buttonsPanel.setVisible(true);
        
        startClientButton.addActionListener(this);
        launchServerButton.addActionListener(this);
        
        return networkButtonsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
                
        if (s.equals("Connect Client"))
        {
            client = new Client(this);
            String server = serverTextField.getText();
            int port = Integer.valueOf(portTextField.getText());
            
            client.startClient(server,port);
            
            startClientButton.setEnabled(false);
            launchServerButton.setEnabled(false);
            serverTextField.setEnabled(false);
            portTextField.setEnabled(false);
            serverLabel.setEnabled(false);
            portLabel.setEnabled(false);
            
            gameFrame.repaint();
        }
        
        if (s.equals("Launch Server"))
        {
            new Server(gameName);
            
            startClientButton.setEnabled(true);
            launchServerButton.setEnabled(false);
            serverTextField.setEnabled(true);
            portTextField.setEnabled(true);
            serverLabel.setEnabled(true);
            portLabel.setEnabled(true);
            
            gameFrame.repaint();

        }

    }
    
    public void clientConnectedMessage()
    {
        statusPanel.setInformationText("Connected to Server . . . waiting for opponent");
    }
    
    public void gameStartedMessage(PlayerColor color)
    {
        statusPanel.setInformationText("game started . . . good luck!");
        ChessGame.newChessNetworkGame(color, client);
    }
}
