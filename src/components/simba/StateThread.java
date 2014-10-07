
package components.simba;

public class StateThread {
    private State[] list;
    private int index;

    public StateThread(State... states) {
        list = states;
        index = 0;
    }
    @Override
    public String toString() {
        String out = "";
        for (State s : list) {
            out += s + ", ";
        }
        return out.substring(0, out.length() - 2);
    }

    public State getState() {
        return list[index];
    }

    public void advance() {
        index++;
    }

    public boolean isOpen() {
        return index == list.length || list[index].getMotion() == Motion.IDLE;
    }

}
