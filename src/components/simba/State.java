
package components.simba;

import java.io.File;

public class State {
    private final Motion motion;
        public Motion getMotion() {
            return motion;
        }
    private final Direction direction;
        public Direction getDirection() {
            return direction;
        }

    public State(Motion motion, Direction direction) {
        this.motion = motion;
        this.direction = direction;
    }
    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(getClass())) {
            return false;
        } else {
            State s = (State) o;
            return getMotion() == s.getMotion() && getDirection() == s.getDirection();
        }
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.motion != null ? this.motion.hashCode() : 0);
        hash = 37 * hash + (this.direction != null ? this.direction.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getMotion() + " " + getDirection();
    }

    public String toPathReference() {
        if (getDirection() == Direction.LEFT) {
            return getMotion().toString() + File.separator + "LEFT" + File.separator;
        } else {
            return getMotion().toString() + File.separator;
        }
    }
}
