package net.mightypork.rpw.tasks.sequences;

import java.awt.Dialog.ModalityType;

import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.gui.windows.messages.DialogProgressTerminal;
import net.mightypork.rpw.utils.logging.Log;


public abstract class AbstractMonitoredSequence extends AbstractSequence {

    protected DialogProgressTerminal monitorDialog = null;


    @Override
    protected abstract boolean step(int step);


    @Override
    public abstract int getStepCount();


    @Override
    protected final void before() {
        Alerts.loading(true);

        monitorDialog = new DialogProgressTerminal(getMonitorTitle(), getMonitorHeading());
        monitorDialog.openDialog();

        doBefore();
    }


    protected abstract String getMonitorHeading();


    protected String getMonitorTitle() {
        return getMonitorHeading();
    }


    @Override
    protected void beforeStep(int index) {
        if (monitorDialog != null) {
            Log.f2(getStepName(index));
            monitorDialog.onStepStarted(index, getStepCount(), getStepName(index));
        }
    }


    protected abstract void doBefore();


    @Override
    protected final void after(boolean success) {
        Alerts.loading(false);

        if (monitorDialog != null) {
            monitorDialog.allowClose(true, success);
        }

        doAfter(success);

        monitorDialog.stopMonitoring();

        monitorDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        monitorDialog.forceGetFocus();
    }


    protected void closeMonitor() {
        if (monitorDialog != null) monitorDialog.closeDialog();
    }


    protected abstract void doAfter(boolean success);


    @Override
    public abstract String getStepName(int step);

}
