package actors;

public class Message {
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getFromActor() {
        return fromActor;
    }

    public void setFromActor(String fromActor) {
        this.fromActor = fromActor;
    }

    public boolean isSequenced() {
        return isSequenced;
    }

    public void setSequenced(boolean isSequenced) {
        this.isSequenced = isSequenced;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
    private String categoryName;
    private String actorName;
    private String fromActor;
    private boolean isSequenced;
    private Object[] data;
}
