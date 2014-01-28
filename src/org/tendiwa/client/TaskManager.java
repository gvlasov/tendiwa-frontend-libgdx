package org.tendiwa.client;

import org.tendiwa.core.Tendiwa;

class TaskManager {
Task currentTask;

boolean trySettingTask(Task task) {
	if (currentTask == null) {
		currentTask = task;
		return true;
	} else {
		return false;
	}
}

public void executeCurrentTask() {
	if (currentTask != null) {
		if (currentTask.ended()) {
			currentTask = null;
		} else {
			currentTask.execute();
			if (Tendiwa.getPlayerCharacter().isUnderAnyThreat()) {
				currentTask = null;
			}
		}
	} else {
		assert false;
	}
}

public boolean hasCurrentTask() {
	return currentTask != null;
}

public void cancelCurrentTask() {
	currentTask = null;
}
}