import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.core.behaviours.*;
import jade.lang.acl.*;



public class psi14_Fixed3 extends Agent {
  private int id;
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
          System.out.println(msg.getContent());
        }
        if(msg.getContent().startsWith("GetCoins") && msg.getPerformative() == ACLMessage.REQUEST){
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.INFORM);
          reply.setContent("MyCoins#3");
          send(reply);
        }
        if(msg.getContent().startsWith("GuessCoins") && msg.getPerformative() == ACLMessage.REQUEST){
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.INFORM);
          reply.setContent("MyBet#6");
          send(reply);
        }
        if(msg.getContent().startsWith("Result") && msg.getPerformative() == ACLMessage.INFORM){
          System.out.println(msg.getContent());
        }
      }
      block();

    }
  }
}
