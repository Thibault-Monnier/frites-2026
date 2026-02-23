package logic.action;

public class SimpleAction implements Action {
    private final Runnable fn;

    public SimpleAction(Runnable fn) {
        this.fn = fn;
    }

    @Override
    public boolean run() {
        fn.run();
        return true; // Action is complete after one run
    }
}
