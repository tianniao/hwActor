package actors;

public interface ActorManager {

    public final static String NAME_SPLIT = "_";

    public abstract void putActorToActorMap(Actor actor) throws Exception;

    public abstract void putActorToActorMap(Actor actor, String actorName, String categoryName, String className)
            throws Exception;

    public abstract Actor getActorByClass(String classname);

    public abstract Actor getActorByCategory(String categoryName, String className);

    public abstract Actor getActorByName(String actorName, String categoryName, String className);

    public abstract void removeFromActorFromMap(Actor actor);

    public abstract void returnActorToMap(Actor actor);

}