package org.texastorque.auto;

import java.util.ArrayList;

public abstract class Block {

    private ArrayList<Command> block;

    private Block(Command... commands) {
        block = new ArrayList<>();
    }
}