import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import jade.lang.acl.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashMap;

public class psi14_MainAg extends Agent{

  private psi14_GUI myGui;
  private MainBehaviour mb;
  private LinkedHashMap<AID, psi14_Player> original_players = new LinkedHashMap<AID, psi14_Player>();
  private LinkedHashMap<AID, psi14_Player> players = new LinkedHashMap<AID,psi14_Player>();
  private LinkedHashMap<AID, psi14_Player> aux = new LinkedHashMap<AID, psi14_Player>();
  private LinkedHashMap<AID, psi14_Player> aux2 = new LinkedHashMap<AID, psi14_Player>();
  private HashMap<AID, Integer> hidden;
  private HashMap<AID, Integer> bets;
  private boolean stopped = false;
  private int n_rounds = 0;
  private int rounds_played=0;


  protected void setup(){
    myGui = new psi14_GUI(this);
  }
  public void lookForPlayers() {
		addBehaviour(new OneShotBehaviour() {

			public void action() {

				DFAgentDescription template = new DFAgentDescription();
				try {
					DFAgentDescription[] result = DFService.search(myAgent,template);
          for (int i = 0; i < result.length; i++) {
            addPlayer(i,result[i].getName());
					}
          myGui.updateTable(original_players);


				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}

		});
	}
  public void addPlayer(int id, AID name){
  original_players.put(name, new psi14_Player(name.getName().split("@")[0],id, 0, 0));
  }
  public void newGame(){
    rounds_played=0;
    resetPlayers();
    if (mb != null) {
			removeBehaviour(mb);
		}
    for(HashMap.Entry<AID, psi14_Player> entry  : original_players.entrySet()) {
      players.put(entry.getKey(),entry.getValue());
    }
    mb = new MainBehaviour();
		addBehaviour(mb);
  }
  public void setStopped(boolean stopped){
    this.stopped = stopped;
  }
  public void setNumberOfRounds(int n_rounds){
    this.n_rounds = n_rounds;
    myGui.addVerbose("-*-*-*-*-*-*- NUMBER OF ROUNDS SET TO "+n_rounds+"-*-*-*-*-*-*- \n");
  }
  public int getNumberOfRounds(){
    return rounds_played;
  }
  public int getPlayersSize(){
    return players.size();
  }
  public int getOriginalPlayersSize(){
    return original_players.size();
  }

  public void resumeGame(){
    mb.restart();
  }
  public void resetPlayers(){
    for(HashMap.Entry<AID, psi14_Player> entry  : original_players.entrySet()) {
      entry.getValue().setGanadas(0);
      entry.getValue().setPerdidas(0);
    }
    myGui.updateTable(original_players);
  }

  private class MainBehaviour extends CyclicBehaviour {
    int step =1;
    String p_coins="";
    String p_guessed="";
    int hid_coins = 0;
    public void action(){
      switch (step){
        case 1:
        for (HashMap.Entry<AID, psi14_Player> e : original_players.entrySet()) {
          aux.put( e.getKey(), e.getValue());
        }
        step1();
        break;
        case 2:
        if(stopped){
          block();
        }else{
          hidden = new HashMap<AID, Integer>();
          myGui.updatePlayers();
          if(n_rounds != 0){
            step2();
            myGui.addVerbose("NUmero de escondidas: "+hid_coins);
          }
          else{
            removeBehaviour(mb);
          }
        }
        break;
        case 3:
        bets = new HashMap<AID,Integer>();
        step3();
        break;
        case 4:
        step4();
        updateRounds();
        step=2;
        break;
      }
    }
    public void step1(){
      for (HashMap.Entry<AID, psi14_Player> entry : players.entrySet()) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(entry.getKey());
        msg.setContent("Id#" + entry.getValue().getId());
        myGui.addVerbose("Player "+ entry.getValue().getNombre()+" is playing with ID "+entry.getValue().getId()+"\n");
				send(msg);
      }
      step++;
    }
    public void step2(){
      int i = 1;
      String n_players ="";
      for(HashMap.Entry<AID, psi14_Player> entry  : players.entrySet()){
        n_players = n_players + entry.getValue().getId() + ",";
      }
      n_players = n_players.substring(0, n_players.length() -1);
      for(HashMap.Entry<AID, psi14_Player> entry  : players.entrySet()) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(entry.getKey());
        msg.setContent("GetCoins#"+n_players+"#"+i);
        myGui.addVerbose("Player "+entry.getValue().getNombre()+" has turn "+i+"\n");
        i++;
        send(msg);
        int msg_count = 0;
        while(msg_count == 0){
          msg_count = checkGetCoins(entry.getKey() , entry.getValue());
        }
      }
      step ++;
    }
    public int checkGetCoins(AID identity, psi14_Player player){
      int msg_count = 0;
      ACLMessage msg = receive();
      if(msg != null){
        if(msg.getContent().startsWith("MyCoins") && msg.getPerformative() == ACLMessage.INFORM){
          p_coins = p_coins + msg.getContent().substring(8)+",";
          msg_count ++;
          hidden.put(identity,Integer.parseInt(msg.getContent().substring(8)));
          hid_coins += Integer.parseInt(msg.getContent().substring(8));
          myGui.addVerbose("Player "+player.getNombre()+" with ID "+player.getId()+" has hidden "+msg.getContent().substring(8)+" coins\n");
        }
      }else{
        block();
      }
      return msg_count;
    }
    public void step3(){
      for(HashMap.Entry<AID, psi14_Player> entry  : players.entrySet()) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(entry.getKey());
        msg.setContent("GuessCoins#"+p_guessed);
        send(msg);
        int msg_count = 0;
        while(msg_count == 0){
          msg_count = checkGuessCoins(entry.getKey(), entry.getValue());
        }
      }
      step++;
    }
    public int checkGuessCoins(AID identity, psi14_Player player){
      int msg_count = 0;
      ACLMessage msg = receive();
      if(msg != null){
        if(msg.getContent().startsWith("MyBet") && msg.getPerformative() == ACLMessage.INFORM){
          bets.put(identity,Integer.parseInt(msg.getContent().substring(6)));
          if(p_guessed.length() == 0){
            p_guessed = p_guessed + msg.getContent().substring(6);
          }else{
            p_guessed = p_guessed +","+ msg.getContent().substring(6);
          }
          myGui.addVerbose("Player "+player.getNombre()+" with ID "+player.getId()+" says that "+msg.getContent().substring(6)+" coins were hidden\n");
          msg_count ++;
        }
      }else{
        block();
      }
      return msg_count;
    }
    public void step4(){
      AID winner=null;
      int id=0;
      boolean win=false;
      String name = "";
      String p_hidden = "";
      for(HashMap.Entry<AID,Integer> entry : bets.entrySet()){
        if(entry.getValue() == hid_coins){
          winner = entry.getKey();
          for(HashMap.Entry<AID,psi14_Player> play : original_players.entrySet()){
            if(play.getKey() == winner){
              for(HashMap.Entry<AID,Integer> hide : hidden.entrySet()){
                p_hidden += hide.getValue() + ",";
              }
              p_hidden = p_hidden.substring(0, p_hidden.length()-1);
              id = play.getValue().getId();
              win=true;
              name = play.getValue().getNombre();
              play.getValue().setGanadas(1);
            }
          }
        }
      }
      for(HashMap.Entry<AID,psi14_Player> player : players.entrySet()){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(player.getKey());
        if(win){
          msg.setContent("Result#"+id+"#"+hid_coins+"#"+p_guessed+"#"+p_hidden);
          send(msg);
        }else{
          msg.setContent("Result##"+hid_coins+"#"+p_guessed+"#"+p_hidden);
        }
      }
      if(win){
        myGui.addVerbose("Player "+name+" with ID "+id+" won this round\n");
        players.remove(winner);
      }else{
        myGui.addVerbose("No player won this round\n");
      }
    }
    public void updateRounds(){
      if(players.size() == 1){
        n_rounds--;
        rounds_played++;
        AID loser=null;
        for(HashMap.Entry<AID, psi14_Player> losers  : players.entrySet()) {
          loser= losers.getKey();
        }
        for(HashMap.Entry<AID, psi14_Player> entry  : original_players.entrySet()) {
          if(entry.getKey() == loser)
            entry.getValue().setPerdidas(1);
        }
        AID key=null;
        psi14_Player value=null;
        boolean primero=true;
        aux2 = new LinkedHashMap<AID, psi14_Player>();
        for(HashMap.Entry<AID, psi14_Player> update  : aux.entrySet()) {
          if(primero){
            key= update.getKey();
            value = update.getValue();
            primero = false;
          }else{
            aux2.put(update.getKey(), update.getValue());
          }
        }
        aux2.put(key,value);
        aux = new LinkedHashMap<AID, psi14_Player>();
        players = new LinkedHashMap<AID, psi14_Player>();
        for(HashMap.Entry<AID,psi14_Player> ent : aux2.entrySet()){
          aux.put(ent.getKey(), ent.getValue());
          players.put(ent.getKey(), ent.getValue());
        }
      }


      try{
        myGui.updateTable(original_players);
        TimeUnit.MILLISECONDS.sleep(30);
      }catch(Exception e){
        System.out.println("fallo");
      }


      hid_coins=0;
      p_coins="";
      p_guessed="";
      myGui.updateRounds();
    }
  }
}
