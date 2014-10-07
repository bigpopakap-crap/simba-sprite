
package components.simba;

import my.collection.Collection;

public class StateQueue {
    private State last;
    private Collection<State> queue;
    private boolean open;

    public StateQueue(State firstState) {
        last = firstState;
        queue = new Collection<State>();
        open = true;
    }
    public int getSize() {
        return queue.getNumObjects();
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }
    public State peekNext() {
        return queue.getObjectAt(0);
    }
    public State getNext() {
        last = queue.getObjectAt(0);
        queue.removeObjects(0);
        return last;
    }
    public State getPrevious() {
        return last;
    }

    public void enqueue(State... state) {
        if (open) {
            queue.addObjects(state);
        }
    }

    public void pause() {
        open = false;
    }
    public void resume() {
        open = true;
    }
}
