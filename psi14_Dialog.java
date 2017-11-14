import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JCheckBoxMenuItem;

class psi14_Dialog extends JDialog implements ActionListener {

  private JFrame frame;
  public Label rounds;

	public JTextField textField;
  private psi14_MainAg mainAgent;
  private psi14_GUI myGui;

	public psi14_Dialog(psi14_MainAg mainAgent, psi14_GUI myGui) {
    this.mainAgent = mainAgent;
    this.myGui = myGui;
    frame = new JFrame();
    frame.setBounds(400,200,350,200);
    frame.getContentPane().setLayout(null);
    JPanel label_panel = new JPanel();
    rounds = new Label("Number of rounds:");
    label_panel.add(rounds);
    label_panel.setBounds(100,30,150,30);
    JPanel tf_panel = new JPanel();
    textField = new JTextField(8);
    tf_panel.add(textField);
    tf_panel.setBounds(100,60,150,30);
    JButton ok = new JButton("OK");
    ok.addActionListener(this);
    ok.setBounds(95,120,80,30);
    JButton cancel = new JButton("CANCEL");
    cancel.addActionListener(this);
    cancel.setBounds(175,120,80,30);
    frame.add(cancel);
    frame.add(ok);
    frame.add(tf_panel);
    frame.add(label_panel);
    frame.setVisible(true);
	}
  public void actionPerformed(ActionEvent evt) {
    if ("OK".equals(evt.getActionCommand())) {
			try{
		  	int n_rounds = Integer.parseInt(textField.getText());
        mainAgent.setNumberOfRounds(n_rounds);
        myGui.updateRounds();
				frame.dispose();
			}catch(NumberFormatException e){
				new psi14_OKDialog("Please, introduce a number");
			}



    } else if ("CANCEL".equals(evt.getActionCommand())) {
			frame.dispose();
  	}
  }

}
