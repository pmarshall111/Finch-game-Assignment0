package com.petermarshall;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import java.awt.Color;

//wanted to extend from Finch class, but they've made it final. So instead we have to include a Finch instance
//within our class.
class ColouredFinch {
    private Color colour;
    private Finch finch;
    private String description;

    ColouredFinch( Color colour, String description ) {
        this.colour = colour;
        this.finch = new Finch();
        this.description = description;
    }

    void turnOnLight(int duration) {
        this.finch.setLED(colour, duration);
    }

    boolean isTapped() {
        boolean tapped = this.finch.isTapped();
        if (tapped) {
            printColour();
        }
        return tapped;
    }

    void printColour() {
        System.out.println(this.description);
    }

    public String getDescription() {
        return description;
    }

    void buzz() {
        this.finch.buzz(10000, 2000); //10Khz for 2seconds.
    }

    void sayGameOver() {
        //TODO: record clip, save and link file location as argument.
        //this.finch.playClip();
    }

}
