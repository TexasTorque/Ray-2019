package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import java.util.ArrayList;

public class DoNothing extends Sequence {

    @Override
    public void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new DriveTime(0));

        sequence.add(block1);
    }

}