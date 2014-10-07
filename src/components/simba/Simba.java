
package components.simba;

import components.gui.SimbaWindow;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import components.world.World;

public class Simba {
    private static final int PICTURE_WIDTH = 150; //MODIFY: PICTURE WIDTH
        public int getWidth() {
            return PICTURE_WIDTH;
        }
    private static final int PICTURE_HEIGHT = 100; //MODIFY: PICTURE HEIGHT
        public int getHeight() {
            return PICTURE_HEIGHT;
        }
    private static final int FRAME_RATE_ADJUST = 5; //MODIFY: PICTURE FRAME RATE ADJUSTMENT
    private static final int MOVE_SPEED = 2; //MODIFY: MOVE SPEED
    private static final int JUMP_SPEED = 4; //MODIFY: JUMP SPEED
    private static final int FALL_SPEED = 2; //MODIFY: FALL SPEED

    private final World world;
        public World getWorld() {
            return world;
        }
    private int x;
        public int getX() {
            return x;
        }
        public void setX(int x) {
            this.x = x;
        }
    private int y;
        public int getY() {
            return y;
        }
        public void setY(int y) {
            this.y = y;
        }
    private State state;
        public State getCurrentState() {
            return state;
        }
        private void setCurrentState(State state) {
            this.state = state;
        }
    private StateThread stateThread;
        public boolean isStateThreadOpen() {
            return stateThread.isOpen();
        }
        public void dispatchStateThread(StateThread thread) {
            if (stateThread.isOpen()) {
                stateThread = thread;
                index = 0;
            }   
        }

    public Simba(World world, int x, int y) {
        this.world = world;
        setX(x);
        setY(y);
        setCurrentState(new State(Motion.IDLE, Direction.RIGHT)); //MODIFY: "CURRENT STATE"
        stateThread = new StateThread(new State(Motion.IDLE, Direction.RIGHT));
        dispatchStateThread(new StateThread(new State(Motion.IDLE, Direction.RIGHT)));
    }

    private int index = 0;
    public BufferedImage getImage() {
        String path = "C:" + File.separator + "Users" + File.separator + "keaswar" + File.separator + "Documents" + File.separator +
                    "NetBeansProjects" + File.separator + "moveSimba" + File.separator + "src" + File.separator + "simba" + File.separator
                                         + stateThread.getState().getMotion() + File.separator
                                         + (stateThread.getState().getDirection() == Direction.LEFT ? "LEFT" + File.separator : "")
                                         + toTwoDigits(index++/FRAME_RATE_ADJUST)
                                         + ".gif";
        setCurrentState(stateThread.getState());
        System.out.println(getCurrentState());

        //CHANGE POSITION
        if (isFalling()) {
            setY(Math.min(getY() + FALL_SPEED, SimbaWindow.WINDOW_HEIGHT - getHeight()));
        }
        if ((getCurrentState().getMotion() == Motion.JUMP_FORWARD) ||
            (getCurrentState().getMotion() == Motion.JUMP_UP && index > 2*FRAME_RATE_ADJUST)) {
            setY(Math.max(0, getY() - JUMP_SPEED));
        }
        if (getCurrentState().getMotion() == Motion.RUN ||
            getCurrentState().getMotion() == Motion.RUN_CONTINUE ||
            getCurrentState().getMotion() == Motion.RUN_STOP ||
            getCurrentState().getMotion() == Motion.JUMP_FORWARD ||
            getCurrentState().getMotion() == Motion.JUMP_FORWARD_HANGING ||
            getCurrentState().getMotion() == Motion.JUMP_FORWARD_LANDING) {
                switch (getCurrentState().getDirection()) {
                    case LEFT : setX(Math.max(0, getX() - MOVE_SPEED));
                                break;
                    case RIGHT: setX(Math.min(getX() + MOVE_SPEED, SimbaWindow.WINDOW_WIDTH - getWidth()));
                                break;
                }
        }
        
        if (index >= stateThread.getState().getMotion().getNumFrames() * FRAME_RATE_ADJUST) {
            if (stateThread.getState().getMotion() == Motion.TURN) {
                stateThread.advance();
                setCurrentState(new State(getCurrentState().getMotion(), getCurrentState().getDirection() == Direction.LEFT ? Direction.RIGHT : Direction.LEFT));
                setX(getX() + (getCurrentState().getDirection() == Direction.LEFT ? 50 : -50));
            } else {
                stateThread.advance();
                setCurrentState(getCurrentState());
            }
            index = 0;
        }
        
        try {
            return ImageIO.read(new File(path));
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("Attempted path: " + path);
            ex.printStackTrace();
            System.exit(0);
            return null;
        }
    }
        private static String toTwoDigits(int val) {
            String a = val + "";
            String b = "";
            for (int i=0; i<2-a.length(); i++) {
                b += 0;
            }
            return b + a;
        }

    public boolean isFalling() {
        return (getX() + getWidth() <= SimbaWindow.WINDOW_WIDTH/2 && getY() + getHeight() < SimbaWindow.WINDOW_HEIGHT - SimbaWindow.FLOOR_HEIGHT)
            || (getX() + getWidth() > SimbaWindow.WINDOW_WIDTH/2 && getY() + getHeight() < SimbaWindow.WINDOW_HEIGHT);
        //true when there is no floor underneath
        //true when at the last frame of JUMP_FORWARD
        //false when the landing animation must start
    }


}
