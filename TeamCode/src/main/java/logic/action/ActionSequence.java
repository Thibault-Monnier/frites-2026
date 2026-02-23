package logic.action;


import java.util.ArrayList;

/// An Action that runs a sequence of Actions in order. Each Action must complete before the next
/// one starts.
public class ActionSequence implements Action {
    private final ArrayList<Action> actions;
    private int currentActionIndex = 0;

    public ActionSequence() {
        this.actions = new ArrayList<>();
    }

    /// Adds an Action to the end of the sequence.
    public ActionSequence addAction(Action action) {
        actions.add(action);
        return this;
    }

    @Override
    public boolean run() {
        if (currentActionIndex >= actions.size()) {
            return true; // Sequence is complete
        }

        Action currentAction = actions.get(currentActionIndex);
        boolean actionComplete = currentAction.run();
        if (actionComplete) {
            currentActionIndex++;
        }
        return false; // Sequence is not yet complete
    }
}
