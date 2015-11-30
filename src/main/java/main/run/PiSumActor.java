package main.run;

import java.math.BigDecimal;

import actors.AbstractActor;
import actors.Message;

public class PiSumActor extends AbstractActor {

    private BigDecimal sum = BigDecimal.ZERO;
    private long c = 0;
    private int msgCount = 0;

    public PiSumActor(int count) {
        msgCount = count;
    }

    public BigDecimal getSum() {
        return sum.multiply(BigDecimal.valueOf(4));
    }

    @Override
    public void execute() {
        BigDecimal add = (BigDecimal) this.getMessage().getData()[0];
        sum = sum.add(add);
        c++;
        if (c >= msgCount) {
            Message message = new Message();
            message.setFromActor(this.getActorName());
            message.setClassName(PrintActor.class.getCanonicalName());
            message.setData(new Object[] { "PI=", getSum() });
            this.getActorPlatform().sendMessage(message);
        }
    }

}
