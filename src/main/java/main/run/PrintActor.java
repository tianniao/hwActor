package main.run;

import actors.AbstractActor;

public class PrintActor extends AbstractActor {

    @Override
    public void execute() {
        Object[] obs = this.getMessage().getData();
        for (Object ob : obs) {
            System.out.print(ob);
        }
        System.out.println();
    }

}
