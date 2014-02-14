package net.mightypork.rpw.tasks.sequences;


import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.gui.windows.messages.DialogProgressTerminal;
import net.mightypork.rpw.utils.logging.Log;


public abstract class AbstractMonitoredSequence extends AbstractSequence {

	private DialogProgressTerminal dpt = null;


	@Override
	protected abstract boolean step(int step);


	@Override
	public abstract int getStepCount();


	@Override
	protected final void before() {

		Alerts.loading(true);

		dpt = new DialogProgressTerminal(getMonitorTitle(), getMonitorHeading());
		dpt.openDialog();

		doBefore();
	}


	protected abstract String getMonitorHeading();


	protected String getMonitorTitle() {

		return getMonitorHeading();
	}


	@Override
	protected void beforeStep(int index) {

		if (dpt != null) {
			Log.f2(getStepName(index));
			dpt.onStepStarted(index, getStepCount(), getStepName(index));
		}
	}


	protected abstract void doBefore();


	@Override
	protected final void after(boolean success) {

		Alerts.loading(false);

		if (dpt != null) {
			dpt.allowClose(true, success);
		}

		doAfter(success);
		dpt.stopMonitoring();
	}


	protected void closeMonitor() {

		if (dpt != null) dpt.closeDialog();
	}


	protected abstract void doAfter(boolean success);


	@Override
	public abstract String getStepName(int step);

}
