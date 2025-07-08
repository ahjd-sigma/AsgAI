package ahjd.asgAI.utils;

public class BehaviourEnums {

    public enum BehaviourType {
        AGGRESSIVE,
        NEUTRAL,
        PASSIVE,
        CUSTOM
    }

    public enum BehaviourPriority {
        HIGHEST(0),
        HIGH(1),
        NORMAL(2),
        LOW(3),
        LOWEST(4);

        private final int priority;

        BehaviourPriority(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
}
