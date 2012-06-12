package webx.studio.server.core.job;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class SerialRule implements ISchedulingRule {
	
	public static final SerialRule instance = new SerialRule();

	private SerialRule() {
	}

	public boolean contains(ISchedulingRule rule) {
		return rule == this;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return rule instanceof SerialRule;
	}
}