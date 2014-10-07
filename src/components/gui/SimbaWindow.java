
package components.gui;

import components.simba.Direction;
import components.simba.Motion;
import components.simba.Simba;
import components.simba.State;
import components.simba.StateThread;
import components.world.World;
import java.awt.Graphics;
import javax.swing.JPanel;

public class SimbaWindow extends javax.swing.JFrame {
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 500;
    public static final int FLOOR_HEIGHT = 100;

    class MyPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(simba.getImage(), simba.getX(), simba.getY(), this);
            g.drawLine(0, WINDOW_HEIGHT - FLOOR_HEIGHT, WINDOW_WIDTH/2, WINDOW_HEIGHT - FLOOR_HEIGHT);
        }
    }

    private Simba simba;
    private MyPanel panel;

    public SimbaWindow(Simba simba) {
        this.simba = simba;
        initComponents();
        panel = new MyPanel();
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        panel.setLocation(25, 25);
        add(panel);
        setSize(WINDOW_WIDTH + 50, WINDOW_HEIGHT + 75);
    }
    public static void main(String args[]) {
        SimbaWindow w = new SimbaWindow(new Simba(new World(), 50, 100));
        w.setVisible(true);
        w.loop();
    }

    public void loop() {
        while (isVisible()) {
            //if (simba.isStateThreadOpen()) {
                simba.dispatchStateThread(gatherStateThread());
            //}
            repaint();
        }
    }
        private StateThread gatherStateThread() {
            State currentState = simba.getCurrentState();
            //IF SIMBA IS FALLING
            if (simba.isFalling()) {
                //IF HE WAS MOVING FORWARD ALREADY
                if (currentState.getMotion() == Motion.JUMP_FORWARD
                 || currentState.getMotion() == Motion.RUN_START
                 || currentState.getMotion() == Motion.RUN
                 || currentState.getMotion() == Motion.JUMP_FORWARD_HANGING) {
                    //THEN RETURN "JUMP FORWARD HANGING"
                    return new StateThread(new State(Motion.JUMP_FORWARD_HANGING, currentState.getDirection()));
                } else {
                    //ELSE RETURN "JUMP UP HANGING"
                    return new StateThread(new State(Motion.JUMP_UP_HANGING, currentState.getDirection()));
                }
            }
            //IF SIMBA IS NOT FALLING
            switch (currentState.getMotion()) {
                //IF SIMBA WAS FALLING FORWARD
                case JUMP_FORWARD_HANGING   :   //RETURN "JUMP FORWARD LANDING"
                                                return new StateThread(new State(Motion.JUMP_FORWARD_LANDING, currentState.getDirection()),
                                                                       new State(Motion.IDLE, currentState.getDirection()));
                //IF SIMBA WAS FALLING STRAIGHT
                case JUMP_UP_HANGING        :   //RETURN "JUMP UP LANDING"
                                                return new StateThread(new State(Motion.JUMP_UP_LANDING, currentState.getDirection()),
                                                                       new State(Motion.IDLE, currentState.getDirection()));
                default                     :   break;
            }
            //IF SIMBA IS NOT FALLING AND NOT LANDING
            //IF PRESSED KEY IS OPPOSITE DIRECTION FROM SIMBA'S MOTION
            if ((currentState.getDirection() == Direction.LEFT && switch_l_r == KeyDirection.RIGHT)
             || (currentState.getDirection() == Direction.RIGHT && switch_l_r == KeyDirection.LEFT)) {
                //RETURN "TURN"
                
                return new StateThread(new State(Motion.TURN, currentState.getDirection()),
                                       new State(Motion.IDLE, currentState.getDirection() == Direction.LEFT ? Direction.RIGHT : Direction.LEFT));
            }
            //IF SIMBA WAS RUNNING
            if (currentState.getMotion() == Motion.RUN) {
                //IF PRESSED KEY IS SAME DIRECTION AS SIMBA'S MOTION
                if ((currentState.getDirection() == Direction.LEFT && switch_l_r == KeyDirection.LEFT)
                 || (currentState.getDirection() == Direction.RIGHT && switch_l_r == KeyDirection.RIGHT)) {
                    switch (switch_u_d) {
                        //IF UP KEY IS PRESSED
                        case UP     :   //RETURN "RUN CONTINUE" THEN "JUMP FORWARD"
                                        return new StateThread(new State(Motion.RUN_CONTINUE, currentState.getDirection()),
                                                               new State(Motion.JUMP_FORWARD, currentState.getDirection()),
                                                               new State(Motion.JUMP_FORWARD_HANGING, currentState.getDirection()));
                        //IF UP KEY IS NOT PRESSED
                        case NULL   :   //RETURN "RUN CONTINUE" ONLY
                                        return new StateThread(new State(Motion.RUN_CONTINUE, currentState.getDirection()),
                                                               new State(Motion.RUN, currentState.getDirection()));
                        default     :   break;
                    }
                } else {
                    //ELSE RETURN "RUN STOP"
                    return new StateThread(new State(Motion.RUN_STOP, currentState.getDirection()));
                }
            }
            //IF SIMBA JUST FINISHED A CYCLE OF RUNNING
            if (currentState.getMotion() == Motion.RUN_CONTINUE) {
                //IF UP KEY IS PRESSED
                if (switch_u_d == KeyDirection.UP) {
                    //RETURN "JUMP FORWARD"
                    return new StateThread(new State(Motion.JUMP_FORWARD, currentState.getDirection()),
                                           new State(Motion.JUMP_FORWARD_HANGING, currentState.getDirection()));
                }
            }
            //IF SIMBA WAS IN IDLE STATE
            if (currentState.getMotion() == Motion.IDLE) {
                switch (switch_l_r) {
                    //IF NO LEFT/RIGHT DIRECTION IS CHOSEN
                    case NULL   :   switch (switch_u_d) {
                                        //IF NO UP/DOWN DIRECTION IS CHOSEN
                                        case NULL   :   //RETURN "IDLE"
                                                        return new StateThread(new State(Motion.IDLE, currentState.getDirection()));
                                        //IF UP IS CHOSEN
                                        case UP     :   //RETURN "JUMP UP"
                                                        return new StateThread(new State(Motion.JUMP_UP, currentState.getDirection()),
                                                                               new State(Motion.JUMP_UP_HANGING, currentState.getDirection()));
                                        default     :   return null;
                                    }
                    //IF LEFT IS CHOSEN
                    case LEFT   :   switch (switch_u_d) {
                                        //IF NO UP/DOWN DIRECTION IS CHOSEN
                                        case NULL   :   //RETURN "RUN START" THEN "RUN"
                                                        return new StateThread(new State(Motion.RUN_START, Direction.LEFT),
                                                                               new State(Motion.RUN, Direction.LEFT));
                                        //IF UP IS CHOSEN
                                        case UP     :   //RETURN "JUMP FORWARD"
                                                        return new StateThread(new State(Motion.JUMP_FORWARD, Direction.LEFT),
                                                                               new State(Motion.JUMP_FORWARD_HANGING, Direction.LEFT));
                                        default     :   return null;
                                    }
                    //IF RIGHT IS CHOSEN
                    case RIGHT   :   switch (switch_u_d) {
                                        //IF NO UP/DOWN DIRECTION IS CHOSEN
                                        case NULL   :   //RETURN "RUN START" THEN "RUN"
                                                        return new StateThread(new State(Motion.RUN_START, Direction.RIGHT),
                                                                               new State(Motion.RUN, Direction.RIGHT));
                                        case UP     :   //RETURN "JUMP FORWARD"
                                                        return new StateThread(new State(Motion.JUMP_FORWARD, Direction.RIGHT),
                                                                               new State(Motion.JUMP_FORWARD_HANGING, Direction.RIGHT));
                                        default     :   return null;
                                    }
                    default     :   return null;
                }
            }
            //IF NOTHING IMPORTANT HAS HAPPENED, RETURN "IDLE"
            return new StateThread(new State(Motion.IDLE, currentState.getDirection()));
        }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private enum KeyDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        NULL;
    }

    private boolean left = false;
    private boolean right = false;
    private KeyDirection switch_l_r = KeyDirection.NULL;
    private boolean up = false;
    private boolean down = false;
    private KeyDirection switch_u_d = KeyDirection.NULL;
    private boolean space = false;
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case 37     :   left = true;
                            switch_l_r = KeyDirection.LEFT;
                            break;
            case 39     :   right = true;
                            switch_l_r = KeyDirection.RIGHT;
                            break;
            case 38     :   up = true;
                            switch_u_d = KeyDirection.UP;
                            break;
            case 40     :   down = true;
                            switch_u_d = KeyDirection.DOWN;
                            break;
            case 32     :   space = true;
                            break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case 37     :   left = false;
                            if (right) {
                                switch_l_r = KeyDirection.RIGHT;
                            } else {
                                switch_l_r = KeyDirection.NULL;
                            }
                            break;
            case 39     :   right = false;
                            if (left) {
                                switch_l_r = KeyDirection.LEFT;
                            } else {
                                switch_l_r = KeyDirection.NULL;
                            }
                            break;
            case 38     :   up = false;
                            if (down) {
                                switch_u_d = KeyDirection.DOWN;
                            } else {
                                switch_u_d = KeyDirection.NULL;
                            }
                            break;
            case 40     :   down = false;
                            if (up) {
                                switch_u_d = KeyDirection.UP;
                            } else {
                                switch_u_d = KeyDirection.NULL;
                            }
                            break;
            case 32     :   space = false;
                            break;
        }
    }//GEN-LAST:event_formKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
