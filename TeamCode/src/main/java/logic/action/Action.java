package logic.action;


@FunctionalInterface
public interface Action {
    /** Runs the action. Returns true if the action is complete, false otherwise. */
    boolean run();
}
