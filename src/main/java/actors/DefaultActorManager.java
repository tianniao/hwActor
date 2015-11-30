package actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import actors.util.StringUtils;

public class DefaultActorManager implements ActorManager {
    private final static int INIT_CAPACITY = 20;
    private Map<String, List<String>> classMap = new HashMap<>(2 * INIT_CAPACITY);
    private Map<String, List<String>> classCategoryMap = new HashMap<>(3 * INIT_CAPACITY);
    private Map<String, Actor> actorList = new HashMap<>(6 * INIT_CAPACITY);

    private void putCategoryToClassMap(String categoryName, String className) {
        List<String> cList = classMap.get(className);
        if (null == cList) {
            cList = new ArrayList<String>(INIT_CAPACITY);
            classMap.put(className, cList);
        }
        int i = 0;
        for (; i < cList.size(); i++) {
            if (StringUtils.equals(cList.get(i), categoryName)) {
                break;
            }
        }
        if (i >= cList.size()) {
            cList.add(categoryName);
        }
    }

    private void putActorToCategoryMap(String actorName, String categoryName, String className) throws Exception {
        String classCategoryName = className + NAME_SPLIT + categoryName;
        List<String> cList = classCategoryMap.get(classCategoryName);
        if (null == cList) {
            cList = new ArrayList<String>(INIT_CAPACITY);
            classCategoryMap.put(classCategoryName, cList);
        }
        for (int i = 0; i < cList.size(); i++) {
            if (StringUtils.equals(cList.get(i), actorName)) {
                throw new Exception("The same name of actor had exist!");
            }
        }

        cList.add(actorName);
        putCategoryToClassMap(categoryName, className);
    }

    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#putActorToActorMap(actors.struct.Actor)
     */
    @Override
    public void putActorToActorMap(Actor actor) throws Exception {
        if (null != actor) {
            putActorToActorMap(actor, actor.getActorName(), actor.getCategoryName(), actor.getClassName());
        }
    }

    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#putActorToActorMap(actors.struct.Actor, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void putActorToActorMap(Actor actor, String actorName, String categoryName, String className) throws Exception {
        String name = className + NAME_SPLIT + categoryName + NAME_SPLIT + actorName;
        if (null == actorList.get(name)) {
            actorList.put(name, actor);
        }
        else {
            throw new Exception("The same name(" + name + ") of actor had exist!");
        }
        
        putActorToCategoryMap(actorName, categoryName, className);
    }


    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#getActorByClass(java.lang.String)
     */
    @Override
    public Actor getActorByClass(String classname) {
        Actor actor = null;
        List<String> categoryList = classMap.get(classname);
        if (null != categoryList) {
            for (String category : categoryList) {
                actor = getActorByCategory(category, classname);
                if (null != actor) {
                    return actor;
                }
            }
        }
        return actor;
    }

    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#getActorByCategory(java.lang.String, java.lang.String)
     */
    @Override
    public Actor getActorByCategory(String categoryName, String className) {
        Actor actor = null;
        List<String> actorList = classCategoryMap.get(className + NAME_SPLIT + categoryName);
        if (null != actorList) {
            for (String actorName : actorList) {
                if (null != (actor = getActorByName(actorName, categoryName, className))) {
                    return actor;
                }
            }
        }
        return actor;
    }

    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#getActorByName(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Actor getActorByName(String actorName, String categoryName, String className) {
        return actorList.get(className + NAME_SPLIT + categoryName + NAME_SPLIT + actorName);
    }

    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#removeFromActorFromMap(actors.struct.Actor)
     */
    @Override
    public void removeFromActorFromMap(Actor actor) {
        actorList.put(actor.getClassName() + NAME_SPLIT + actor.getCategoryName() + NAME_SPLIT + actor.getActorName(),
                null);
    }
    
    /* (non-Javadoc)
     * @see actors.struct.ActorManagerin#returnActorToMap(actors.struct.Actor)
     */
    @Override
    public void returnActorToMap(Actor actor) {
        actorList.put(actor.getClassName() + NAME_SPLIT + actor.getCategoryName() + NAME_SPLIT + actor.getActorName(), actor);
    }

}
