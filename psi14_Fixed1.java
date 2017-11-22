import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.Random;



public class psi14_Fixed1 extends Agent {
  private int id;
  private int c_chosen;
  private int c_hidden;
  private int n_players;
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		sd.setName("");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
    addBehaviour(new MainBehaviour());
	}

	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
  private class MainBehaviour extends CyclicBehaviour {
		public void action() {

      ACLMessage msg= receive();
      if (msg!=null){
        if(msg.getContent().startsWith("Id") && msg.getPerformative() == ACLMessage.INFORM){
          id = Integer.parseInt(msg.getContent().substring(3));
        }
        if(msg.getContent().startsWith("GetCoins") && msg.getPerformative() == ACLMessage.REQUEST){
          String data[]= msg.getContent().split("#");
          String info = data[1].replace(",","");
          n_players = info.length();
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.INFORM);
          c_hidden = 1;
          reply.setContent("MyCoins#"+c_hidden);
          send(reply);
        }
        if(msg.getContent().startsWith("GuessCoins") && msg.getPerformative() == ACLMessage.REQUEST){
          Random rand = new Random();
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.INFORM);
          String receive = msg.getContent().substring(11);
          String splitted[] = receive.split(",");
          boolean isplayed = true;
          while(isplayed){
            if(splitted[0].equals("")){
              isplayed=false;
            }else{
              boolean check = false;
              c_chosen = rand.nextInt(n_players + 1)+c_hidden;
              for( int i = 0; i < splitted.length; i++){
                if(splitted[i].equals(String.valueOf(c_chosen))){

                  check = true;
                }
              }
              if(check){
                isplayed = true;
              }else{
                isplayed = false;
              }
            }
          }
          reply.setContent("MyBet#"+c_chosen);
          send(reply);
        }
        if(msg.getContent().startsWith("Result") && msg.getPerformative() == ACLMessage.INFORM){
        }
      }
      block();

    }
  }
}
