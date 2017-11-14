import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JCheckBoxMenuItem;

class psi14_OKDialog extends JDialog implements ActionListener {

  private JFrame frame;
  public Label info;

	public JTextField textField;

	public psi14_OKDialog(String text) {
    frame = new JFrame();
    frame.setBounds(400,200,350,200);
    frame.getContentPane().setLayout(null);
    JPanel info_panel = new JPanel();
    info = new Label(text);
    info_panel.add(info);
    info_panel.setBounds(40,30,300,30);
    JButton ok = new JButton("OK");
    ok.addActionListener(this);
    ok.setBounds(120,120,80,30);
    frame.add(ok);
    frame.add(info_panel);
    frame.setVisible(true);
	}
  public void actionPerformed(ActionEvent evt) {
    if ("OK".equals(evt.getActionCommand())) {
  	   //Integer.parseInt(textField.getText());
        frame.dispose();
    }
  }
}
