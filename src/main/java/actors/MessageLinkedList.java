package actors;

import actors.util.StringUtils;



public class MessageLinkedList {
    private LinkedMessage noSequenceHead;
    private LinkedMessage noSequenceTail;

    private LinkedMessage sequenceHead;
    private LinkedMessage sequenceTail;


    class LinkedMessage {
        private volatile Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public LinkedMessage getNext() {
            return next;
        }

        public void setNext(LinkedMessage next) {
            this.next = next;
        }

        private volatile LinkedMessage next;

        LinkedMessage(Message message) {
            this.message = message;
            this.next = null;
        }
    }

    public void addMessage(Message message) {
        if (null != message) {
            LinkedMessage node = new LinkedMessage(message);
            if (message.isSequenced()) {
                if (null != sequenceHead) {
                    sequenceTail.setNext(node);
                    sequenceTail = node;
                }
                else {
                    sequenceHead = sequenceTail = node;
                }
            }
            else {
                if (null != noSequenceHead) {
                    noSequenceTail.setNext(node);
                    noSequenceTail = node;
                }
                else {
                    noSequenceHead = noSequenceTail = node;
                }
            }
        }
    }

    public Actor getActor(Message m, ActorManager actorManager) {
        if (StringUtils.isBlank(m.getActorName()) && StringUtils.isBlank(m.getCategoryName())
                && StringUtils.isNotBlank(m.getClassName())) {
            return actorManager.getActorByClass(m.getClassName());
        }
        else if (StringUtils.isBlank(m.getActorName()) && StringUtils.isNotBlank(m.getCategoryName())
                && StringUtils.isNotBlank(m.getClassName())) {
            return actorManager.getActorByCategory(m.getCategoryName(), m.getClassName());
        }
        else {
            return actorManager.getActorByName(m.getActorName(), m.getCategoryName(), m.getClassName());
        }
    }

    public Actor getAndRemoveMessageWithActor(ActorManager actorManager) throws InterruptedException {
        LinkedMessage node = noSequenceHead;
        LinkedMessage preNode = noSequenceHead;
        Actor actor = null;
        Message m = null;
        // get sequence message first
        if (null != sequenceHead) {
            m = sequenceHead.getMessage();
            if (null != (actor = getActor(m, actorManager))) {
                if (sequenceTail == sequenceHead) {
                    sequenceTail = null;
                }
                sequenceHead = sequenceHead.getNext();
                // set actor null in map
                actorManager.removeFromActorFromMap(actor);

                // init message with actor
                actor.setMessage(m);
                return actor;
            }
        }

        // get no sequence message
        node = noSequenceHead;
        preNode = noSequenceHead;
        actor = null;
        m = null;
        while (null != node) {
            m = node.getMessage();
            if (null != (actor = getActor(m, actorManager))) {
                // remove message
                if (node == noSequenceHead) {
                    noSequenceHead = node.getNext();
                    node.setNext(null);
                }
                else {
                    preNode.setNext(node.getNext());
                    node.setNext(null);
                }
                if (node == noSequenceTail) {
                    if (noSequenceHead == null) {
                        noSequenceTail = null;
                    }
                    else {
                        noSequenceTail = preNode;
                    }
                }

                // set actor null in map
                actorManager.removeFromActorFromMap(actor);

                // init message with actor
                actor.setMessage(m);
                return actor;
            }
            preNode = node;
            node = node.getNext();
        }
        return null;
    }

}
