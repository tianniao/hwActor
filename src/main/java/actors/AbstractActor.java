package actors;

public abstract class AbstractActor implements Actor {
    private String className;
    private String categoryName;
    private String actorName;
    private Message message;
    private ActorPlatform actorPlatform;

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String getActorName() {
        return actorName;
    }

    @Override
    public void setClassName(String name) {
        this.className = name;
    }

    @Override
    public void setCategoryName(String name) {
        this.categoryName = name;
    }

    @Override
    public void setActorName(String name) {
        this.actorName = name;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public ActorPlatform getActorPlatform() {
        return actorPlatform;
    }

    @Override
    public void setActorPlatform(ActorPlatform actorPlatform) {
        this.actorPlatform = actorPlatform;
    }

}
