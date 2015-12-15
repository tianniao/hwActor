package actors;

import java.util.concurrent.atomic.AtomicInteger;

import actors.log.DefaultLogger;
import actors.log.Logger;

public class DefaultActorPlatform implements ActorPlatform {
    public static final Logger logger = DefaultLogger.getDefaultInstance();
    private final static String DEFAULT_PREFIX_ACTOR_NAME = "actor";

    private volatile Boolean running = true;
    private ActorManager actorManager = new DefaultActorManager();
    private MessageLinkedList messageList = new MessageLinkedList();
    private AtomicInteger messageListAtomic = new AtomicInteger(0);

    class BackendThread extends Thread {
        @Override
        public void run() {
            Actor actor;
            while (running) {
                try {
                    actor = receiveMessage();
                    if (null != actor) {
                        actor.execute();
                        actorManager.returnActorToMap(actor);
                    }
                }
                catch (Exception e) {
                    logger.error("Backend thread exception ", e);
                }
            }
        }
    }

    public DefaultActorPlatform() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    public DefaultActorPlatform(int backendThreadCount) {
        for (int i = 0; i < backendThreadCount; i++) {
            BackendThread bt = new BackendThread();
            bt.setDaemon(true);
            bt.setPriority(Math.max(Thread.MIN_PRIORITY, Thread.currentThread().getPriority() - 1));
            bt.start();
        }
    }

    @Override
    public <T extends Actor> void createActors(Class<T> c, int count) throws Exception {
        createActors(c, null, count);
    }

    @Override
    public <T extends Actor> void createActors(Class<T> c, String category, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            createActors(c, category, DEFAULT_PREFIX_ACTOR_NAME + i);
        }
    }

    @Override
    public <T extends Actor> void createActors(Class<T> c, String category, String name) throws Exception {
        String className = c.getCanonicalName();
        Actor at = (Actor) c.newInstance();
        at.setClassName(className);
        at.setCategoryName(category);
        at.setActorName(name);
        at.setActorPlatform(this);
        actorManager.putActorToActorMap(at);
    }

    @Override
    public void putActor(Actor at) throws Exception {
        at.setActorPlatform(this);
        actorManager.putActorToActorMap(at);
    }

    @Override
    public boolean sendMessage(Message message) {
        acquiredMessageListLock();
        messageList.addMessage(message);
        messageListAtomic.compareAndSet(1, 0);
        return true;
    }

    @Override
    public void awaitAndTerminate() {
        /*
         * try { lock.lock(); while (messageCount > 0) {
         * hasFinishCondition.await(); } } catch (InterruptedException e) {
         * logger.error("Await terminate exception ", e); } finally {
         * lock.unlock(); }
         */
        try {
            Thread.sleep(15000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Actor receiveMessage() throws InterruptedException {
        Actor actor = null;
        while (null == actor) {
            acquiredMessageListLock();
            actor = messageList.getAndRemoveMessageWithActor(actorManager);
            messageListAtomic.compareAndSet(1, 0);
            if (null != actor) {
                return actor;
            }
        }
        return actor;
    }

    private void acquiredMessageListLock() {
        while (messageListAtomic.get() > 0 || !messageListAtomic.compareAndSet(0, 1)) {

        }
    }

}
