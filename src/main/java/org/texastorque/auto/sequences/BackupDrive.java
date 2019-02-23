package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import java.util.ArrayList;

public class BackupDrive extends Sequence {

    @Override
    public void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new DriveTime(1.0));
        block1.add(new LiftSet(1));

        sequence.add(block1);
    }

}