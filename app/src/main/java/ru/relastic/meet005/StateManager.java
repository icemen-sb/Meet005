package ru.relastic.meet005;

import java.util.Random;
import java.util.Vector;

public final class StateManager {
    //singleton
    protected volatile States  currentState;

    public StateManager(){
        currentState = States.values()[0];
    }
    private static class SingletonHolder {
        private final static StateManager instance = new StateManager();
    }
    public static StateManager getInstance(){
        return SingletonHolder.instance;
    }


    public States getState(){
        return currentState;
    }
    public void setState(States newValue) {
        currentState=newValue;
    }
    public States nextState(){
        //if optional is true: forvard ordering
        States[] states = States.values();
        int nextindex = this.getState().ordinal()+1;
        int maxindex = states.length-1;
        this.setState((nextindex<=maxindex)?states[nextindex]:states[0]);
        return this.getState();
    }
    public States randomState(boolean optional){
        //if optional is true: except current value
        States retValue;
        if (optional) {
            Vector v = new Vector();
            int exceptIndex=this.getState().ordinal();
            for(States s : States.values()) {
                if (s.ordinal()!=exceptIndex) {v.add(s);}
            }
            retValue = (States)v.get(new Random().nextInt(v.size()));
        }else {
            retValue = States.values()[new Random().nextInt(States.values().length)];
        }
        this.setState(retValue);
        return retValue;
    }
    public enum States {
        SM_STATE_A("Состояние A"),
        SM_STATE_B("Состояние B"),
        SM_STATE_C("Состояние C"),
        SM_STATE_D("Состояние D"),
        SM_STATE_E("Состояние E");

        private final String state;

        States(String state) {
            this.state = state;
        }
        public String getValue() {
            return state;
        }
    }
}
