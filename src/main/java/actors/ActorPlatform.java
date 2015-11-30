package actors;

public interface ActorPlatform {

    boolean sendMessage(Message message);

    void awaitAndTerminate();

    void putActor(Actor at) throws Exception;

    <T extends Actor> void createActors(Class<T> c, int count) throws Exception;

    <T extends Actor> void createActors(Class<T> c, String category, int count) throws Exception;

    <T extends Actor> void createActors(Class<T> c, String category, String name) throws Exception;
}
