package com.petermarshall;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import java.awt.Color;

//wanted to extend from Finch class, but they've made it final. So instead we have to include a Finch instance
//within our class.
class ColouredFinch {
    private Color color;
    private Finch finch;

    ColouredFinch( Color color ) {
        this.color = color;
        this.finch = new Finch();
    }

    void turnOnLight(int duration) {
        this.finch.setLED(color, duration);
    }

    boolean isTapped() {
        boolean tapped = this.finch.isTapped();
        if (tapped) {
            System.out.println(this.color.toString() + " was tapped!");
        }
        return tapped;
    }

    void buzz() {
        this.finch.buzz(10000, 2000); //10Khz for 2seconds.
    }

    void sayGameOver() {
        //TODO: record clip, save and link file location as argument.
        //this.finch.playClip();
    }

}
