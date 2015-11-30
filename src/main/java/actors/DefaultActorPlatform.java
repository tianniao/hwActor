package actors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import actors.log.DefaultLogger;
import actors.log.Logger;

public class DefaultActorPlatform implements ActorPlatform {
    public static final Logger logger = DefaultLogger.getDefaultInstance();
    private final static String DEFAULT_PREFIX_ACTOR_NAME = "actor";

    private volatile Boolean running = true;
    private ActorManager actorManager = new DefaultActorManager();
    private volatile int messageCount = 0;
    private MessageLinkedList messageList = new MessageLinkedList();

    private ReentrantLock lock = new ReentrantLock();
    private Condition hasMessageCondition = lock.newCondition();
    private Condition hasActiveActorCondition = lock.newCondition();
    private Condition hasFinishCondition = lock.newCondition();

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
                        try {
                            lock.lock();
                            messageCount--;
                            if (messageCount <= 0) {
                                hasFinishCondition.signal();
                            }
                        }
                        finally {
                            lock.unlock();
                        }
                        notifyActorReturn();
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
        try {
            lock.lock();
            messageList.addMessage(message);
            messageCount++;
            if (messageCount > 0) {
                hasMessageCondition.signal();
            }
        }
        catch (Exception e) {
            logger.error("Send message exception ", e);
        }
        finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public void awaitAndTerminate() {
        try {
            lock.lock();
            while (messageCount > 0) {
                hasFinishCondition.await();
            }
        }
        catch (InterruptedException e) {
            logger.error("Await terminate exception ", e);
        }
        finally {
            lock.unlock();
        }
    }

    private Actor receiveMessage() throws InterruptedException {
        Actor actor = null;
        while (null == actor) {
            try {
                lock.lock();
                if (messageCount <= 0) {
                    hasMessageCondition.await();
                }
                actor = messageList.getAndRemoveMessageWithActor(actorManager);
                if (null != actor) {
                    return actor;
                }
                else {
                    hasActiveActorCondition.await();
                }
            }
            catch (Exception e) {
                logger.error("Receive message exception ", e);
            }
            finally {
                lock.unlock();
            }
        }
        return actor;
    }

    private void notifyActorReturn() {
        try {
            lock.lock();
            hasActiveActorCondition.signal();
        }
        finally {
            lock.unlock();
        }
    }

}
