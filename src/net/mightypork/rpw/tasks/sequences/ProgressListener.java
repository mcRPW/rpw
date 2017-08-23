package net.mightypork.rpw.tasks.sequences;

/**
 * Something that shows progress of a sequence based on steps
 *
 * @author Ondřej Hruška (MightyPork)
 */
public interface ProgressListener {

    /**
     * Called before step is executed
     *
     * @param index step index (0..n-1)
     * @param total number of steps (n)
     * @param name  step name
     */
    public void onStepStarted(int index, int total, String name);
}
