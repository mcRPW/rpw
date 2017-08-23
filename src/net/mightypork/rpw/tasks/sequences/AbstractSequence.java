package net.mightypork.rpw.tasks.sequences;

import net.mightypork.rpw.utils.logging.Log;


/**
 * A command sequence
 *
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class AbstractSequence {

    /**
     * Last executed step
     */
    private final int lastStep = -1;


    /**
     * Perform a step
     *
     * @param step step
     * @return true if step succeeded, false on error
     */
    protected abstract boolean step(int step);


    /**
     * @return number of sequence steps
     */
    public abstract int getStepCount();


    /**
     * Get name of a step
     *
     * @param step step index
     * @return name (eg. "Creating thumbnails")
     */
    public abstract String getStepName(int step);


    /**
     * Stuff to be executed before the first step
     */
    protected abstract void before();


    /**
     * Stuff to be executed before calling given step
     *
     * @param index step ID
     */
    protected abstract void beforeStep(int index);


    /**
     * Stuff to be executed at the end of sequence
     *
     * @param success true if sequence succeeded, false if it was aborted and
     *                cleanup is to be done
     */
    protected abstract void after(boolean success);


    /**
     * Run all steps
     */
    public void run() {
        for (int i = 0; i < getStepCount(); i++) {
            if (!run(i)) {
                Log.w("Sequence failed at step #" + i + ": " + getStepName(i));
                break; // interrupted from within
            }
        }
    }


    /**
     * Run a step, call before() and after() where appropriate.
     *
     * @param step step ID
     * @return true if step was executed successfully, false if the step does
     * not exist or an error occurred.
     */
    public boolean run(int step) {
        final int count = getStepCount();

        if (step <= lastStep) {
            return true; // already executed
        }

        if (step < 0 || step >= count) {
            return false;
        }

        if (step == 0) before();

        beforeStep(step);
        final boolean success = step(step);

        if (step == getStepCount() - 1 || !success) after(success);

        return success;
    }


    /**
     * Run in thread (experimental)
     */
    public void runAsync() {
        (new Thread(new Runnable() {

            @Override
            public void run() {
                AbstractSequence.this.run();

            }
        })).start();

    }
}
