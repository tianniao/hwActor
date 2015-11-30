package main.run;

import java.math.BigDecimal;
import java.util.Arrays;

import actors.Actor;
import actors.ActorPlatform;
import actors.DefaultActorPlatform;
import actors.Message;


 
 
public class MainRunMore {
 
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MainRunMore pm = new MainRunMore();
        int count = 5;
        Integer[] actors = new Integer[count];
        Integer[] singles = new Integer[count];
        for (int i = 0; i < count; i++) {
            System.out.println();
            System.out.println("------" + i + "------");
            pm.runMore(actors, singles, i);
            System.out.println("------" + i + "------");
        }
        Arrays.sort(actors);
        System.out.println(Arrays.asList(actors));
    }

    public void runOnce() throws Exception {
        int initMessage = 100;
        ActorPlatform platform = new DefaultActorPlatform();

        // init actors
        platform.createActors(PiQuarterActor.class, 10);
        platform.createActors(PrintActor.class, 1);

        Actor at = new PiSumActor(initMessage);
        at.setClassName(PiSumActor.class.getCanonicalName());
        at.setCategoryName("sum");
        at.setActorName("sum");
        platform.putActor(at);

        // send init message
        long s = System.nanoTime();
        System.out.println("Actor start run");
        for (long i = 0; i < initMessage; i++) {
            Message message = new Message();
            message.setClassName(PiQuarterActor.class.getCanonicalName());
            message.setData(new Object[] { i * 2000000 + 1, (i + 1) * 2000000 });
            platform.sendMessage(message);
        }
        platform.awaitAndTerminate();
        int runTime = (int) ((System.nanoTime() - s) / 1000000);
        System.out.println("Execute time(miliseconds):" + runTime);

        System.out.println("------------------");
        BigDecimal sum = BigDecimal.ZERO;
        long t = System.nanoTime();
        System.out.println("Single thread start run");
        int scale = 15;
        for (long i = 1; i <= 200000000L; i++) {
            sum = sum.add((BigDecimal.ONE.divide(BigDecimal.valueOf(4 * i - 3), scale, BigDecimal.ROUND_HALF_EVEN))
                    .subtract(BigDecimal.ONE.divide(BigDecimal.valueOf(4 * i - 1), scale, BigDecimal.ROUND_HALF_EVEN)));
        }
        System.out.println("PI=" + (sum.multiply(BigDecimal.valueOf(4)).toString()));
        runTime = (int) ((System.nanoTime() - t) / 1000000);
        System.out.println("Execute time(miliseconds):" + runTime);

    }

    public void runMore(Integer[] a, Integer[] si, int count) throws Exception {
        int initMessage = 100;
        ActorPlatform platform = new DefaultActorPlatform();

        // init actors
        platform.createActors(PiQuarterActor.class, 10);
        platform.createActors(PrintActor.class, 1);

        Actor at = new PiSumActor(initMessage);
        at.setClassName(PiSumActor.class.getCanonicalName());
        at.setCategoryName("sum");
        at.setActorName("sum");
        platform.putActor(at);

        // send init message
        long s = System.nanoTime();
        System.out.println("Actor start run");
        for (long i = 0; i < initMessage; i++) {
            Message message = new Message();
            message.setClassName(PiQuarterActor.class.getCanonicalName());
            message.setData(new Object[] { i * 2000000 + 1, (i + 1) * 2000000 });
            platform.sendMessage(message);
        }
        platform.awaitAndTerminate();
        a[count] = (int) ((System.nanoTime() - s) / 1000000);
        System.out.println("Execute time(miliseconds):" + a[count]);

        System.out.println("------------------");
        BigDecimal sum = BigDecimal.ZERO;
        long t = System.nanoTime();
        System.out.println("Single thread start run");
        int scale = 15;
        for (long i = 1; i <= 200000000L; i++) {
            sum = sum.add((BigDecimal.ONE.divide(BigDecimal.valueOf(4 * i - 3), scale, BigDecimal.ROUND_HALF_EVEN))
                    .subtract(BigDecimal.ONE.divide(BigDecimal.valueOf(4 * i - 1), scale, BigDecimal.ROUND_HALF_EVEN)));
        }
        System.out.println("PI=" + (sum.multiply(BigDecimal.valueOf(4)).toString()));
        si[count] = (int) ((System.nanoTime() - t) / 1000000);
        System.out.println("Execute time(miliseconds):" + si[count]);

    }
 
}