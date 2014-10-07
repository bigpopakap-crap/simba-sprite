
package components.simba;

public enum Motion {
    IDLE(15),
    RUN_START(1),
    RUN(8),
    RUN_CONTINUE(6),
    RUN_STOP(2),
    TURN(4),
    JUMP_UP(6),
    JUMP_UP_HANGING(1),
    JUMP_UP_LANDING(3),
    JUMP_FORWARD(4),
    JUMP_FORWARD_HANGING(1),
    JUMP_FORWARD_LANDING(5);

    private final int frames;
        public int getNumFrames() {
            return frames;
        }
    Motion(int frames) {
        this.frames = frames;
    }
}
