package main.run;

import java.math.BigDecimal;

import actors.AbstractActor;
import actors.Message;

public class PiQuarterActor extends AbstractActor {

    @Override
    public void execute() {
        Object[] obs = this.getMessage().getData();
        long from = (long) obs[0];
        long to = (long) obs[1];
        // System.out.println("from=" + from + " to=" + to);
        BigDecimal sum = BigDecimal.ZERO;
        int scale = 15;
        for (long i = from; i <= to; i++) {
            // sum += ArithUtil.sub(ArithUtil.div(1.0, 4 * i - 3, 25),
            // ArithUtil.div(1.0, 4 * i - 1, 25));
            // sum += (double) 1.0 / (4 * i - 3) - (double) 1.0 / (4 * i - 1);
            sum = sum.add((BigDecimal.ONE.divide(BigDecimal.valueOf(4 * i - 3), scale, BigDecimal.ROUND_HALF_EVEN))
                    .subtract(BigDecimal.ONE.divide(BigDecimal.valueOf(4 * i - 1), scale, BigDecimal.ROUND_HALF_EVEN)));
        }
        // System.out.println(from + " to " + to + "=" + sum);
        Message message = new Message();
        message.setFromActor(this.getActorName());
        message.setClassName(PiSumActor.class.getCanonicalName());
        // message.setCategoryName("sum");
        // message.setActorName("sum");
        message.setData(new Object[] { sum });
        this.getActorPlatform().sendMessage(message);
    }

}
