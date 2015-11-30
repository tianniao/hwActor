package actors;

public interface Actor {
    void execute();

    String getClassName();

    String getCategoryName();

    String getActorName();

    void setClassName(String name);

    void setCategoryName(String name);

    void setActorName(String name);

    Message getMessage();

    void setMessage(Message message);

    ActorPlatform getActorPlatform();

    void setActorPlatform(ActorPlatform actorPlatform);

}
