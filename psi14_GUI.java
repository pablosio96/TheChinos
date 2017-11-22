import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JCheckBoxMenuItem;
import jade.core.AID;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


 class psi14_GUI extends JFrame implements ActionListener, ItemListener{
    private String[] columnNames = {"Nombre","Id","Ganadas","Perdidas"};
    private DefaultTableModel model = new DefaultTableModel(columnNames, 10);
    private JTable table = new JTable(model);
    private JTextArea textarea = new JTextArea(10,50);
    private JPanel n_rounds_panel;
    private JLabel n_rounds_label;
    private JPanel n_players_panel;
    private JLabel n_players_label;
    private psi14_MainAg mainAgent;
    private boolean stopped = false;

    private boolean verbose = false;
    psi14_GUI(psi14_MainAg mainAgent){

      this.mainAgent = mainAgent;

      JMenuBar oMB = new JMenuBar();
  		JMenu oMenu = new JMenu("Run");
  		JMenuItem oMI = new JMenuItem("New");
  		oMI.addActionListener(this);
  		oMenu.add(oMI);
  		oMI = new JMenuItem("Stop");
  		oMI.addActionListener(this);
  		oMenu.add(oMI);
  		oMI = new JMenuItem("Continue");
  		oMI.addActionListener(this);
  		oMenu.add(oMI);
  		oMenu.add(new JMenuItem("-"));
  		oMI = new JMenuItem("Number of rounds");
  		oMI.addActionListener(this);
  		oMenu.add(oMI);
  		oMB.add(oMenu);

  		oMenu = new JMenu("Window");
  	   oMI= new JCheckBoxMenuItem("Verbose");
      oMI.addItemListener(this);
  		oMenu.add(oMI);
      oMB.add(oMenu);
  		oMenu = new JMenu("Help");
  		oMI = new JMenuItem("About");
  		oMI.addActionListener(this);
  		oMenu.add(oMI);
  		oMB.add(oMenu);
      JFrame frame = new JFrame();
      frame.setBounds(200,100,1000,600);
      frame.getContentPane().setLayout(null);
      JPanel table_panel = new JPanel();
  		table.setPreferredScrollableViewportSize(table.getPreferredSize());
  		table.setFillsViewportHeight(true);
  		table.setEnabled(false);
      JScrollPane scroll_table = new JScrollPane();
      scroll_table.add(table);
      scroll_table.setViewportView(table);
      table_panel.add(scroll_table);
      table_panel.setBounds(25,10,500,300);
      JPanel ta_panel = new JPanel();
      textarea.setLineWrap(true);
      textarea.setEditable(false);
      textarea.setLayout(new BorderLayout());
      JScrollPane scroll_ta = new JScrollPane();
      scroll_ta.add(textarea);
      scroll_ta.setViewportView(textarea);
      ta_panel.add(scroll_ta);
      ta_panel.setBounds(20,350,700,200);
      JPanel text_panel = new JPanel();
      text_panel.add(new JLabel("Number of rounds played "));
      text_panel.setBounds(600,30,400,20);
      n_rounds_panel = new JPanel();
      n_rounds_label = new JLabel();
      n_rounds_label.setText(String.valueOf(mainAgent.getNumberOfRounds()));
      n_rounds_panel.add(n_rounds_label);
      n_rounds_panel.setBounds(600,60,400,20);
      JPanel playersleft_panel = new JPanel();
      playersleft_panel.add(new JLabel("Players left"));
      playersleft_panel.setBounds(600,90,400,20);
      n_players_panel = new JPanel();
      n_players_label = new JLabel();
      n_players_label.setText(String.valueOf(mainAgent.getOriginalPlayersSize()));
      n_players_panel.add(n_players_label);
      n_players_panel.setBounds(600,120,400,20);
      frame.add(n_players_panel);
      frame.add(playersleft_panel);
      frame.add(table_panel);
      frame.add(ta_panel);
      frame.add(text_panel);
      frame.add(n_rounds_panel);
      frame.setJMenuBar(oMB);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void actionPerformed(ActionEvent event){
      if("New".equals(event.getActionCommand())){
        addVerbose("-*-*-*-*-*-*- NEW GAME -*-*-*-*-*-*- \n");
        mainAgent.lookForPlayers();
        try{
          TimeUnit.SECONDS.sleep(2);
        }catch(Exception e){}
        mainAgent.newGame();
      }else if("Stop".equals(event.getActionCommand())){
        addVerbose("-*-*-*-*-*-*- GAME STOPPED -*-*-*-*-*-*- \n");
        stopped = true;
        mainAgent.setStopped(stopped);
      }else if("Continue".equals(event.getActionCommand())){
        stopped = false;
        mainAgent.setStopped(stopped);
        mainAgent.resumeGame();
        addVerbose("-*-*-*-*-*-*- GAME CONTINUED -*-*-*-*-*-*- \n");
      }else if("Number of rounds".equals(event.getActionCommand())){
        //p
        obtainRounds();
      }else if("About".equals(event.getActionCommand())){
        new psi14_OKDialog("Created by Pablo");
      }else{
        return;
      }
    }
    public void obtainRounds(){
      psi14_Dialog rondas = new psi14_Dialog(mainAgent, this);
    }
    public void updateRounds(){
      n_rounds_label.setText(String.valueOf(mainAgent.getNumberOfRounds()));
      n_rounds_panel.validate();
    }
    public void updatePlayers(){
      n_players_label.setText(String.valueOf(mainAgent.getPlayersSize()));
      n_players_panel.validate();
    }
    public void itemStateChanged(ItemEvent event){
      if (event.getStateChange() == ItemEvent.SELECTED) {
        verbose=true;
        addVerbose("Verbose ON\n");
      }else{
        verbose=false;
      }
    }

    public void addVerbose(String text){
      if(verbose)
      textarea.append(text);
    }
    public void updateTable(HashMap<AID, psi14_Player> players){
      /**int rowCount = model.getRowCount();
      for (int i = rowCount - 1; i >= 0; i--) {
        model.removeRow(i);
      }
      int i =0;
      for (HashMap.Entry<AID, psi14_Player> entry : players.entrySet()) {
        model.insertRow(i,new String[]{entry.getValue().getNombre(),String.valueOf(entry.getValue().getId()),String.valueOf(entry.getValue().getGanadas()),String.valueOf(entry.getValue().getPerdidas())});
        i++;
      }*/
      try {
  			if (model.getRowCount() == 0) {
          int i=0;
  				for (HashMap.Entry<AID, psi14_Player> entry : players.entrySet()) {
            model.insertRow(i,new String[]{entry.getValue().getNombre(),String.valueOf(entry.getValue().getId()),String.valueOf(entry.getValue().getGanadas()),String.valueOf(entry.getValue().getPerdidas())});
            i++;
  				}
  			} else {
  				int i=0;
  				for (HashMap.Entry<AID, psi14_Player> entry : players.entrySet()) {
  					model.setValueAt(entry.getValue().getNombre(), i, 0);
  					model.setValueAt(String.valueOf(entry.getValue().getId()), i, 1);
  					model.setValueAt(String.valueOf(entry.getValue().getGanadas()), i, 2);
  					model.setValueAt(String.valueOf(entry.getValue().getPerdidas()), i, 3);
            i++;
  				}
  			}
  		} catch (Exception e) {
  			e.printStackTrace();
  			// No players yet!
  		}

    }

  }
