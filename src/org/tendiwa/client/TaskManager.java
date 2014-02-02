package org.tendiwa.client;

import org.tendiwa.core.Character;

class TaskManager {
private final Character player;
Task currentTask;

TaskManager(Character player) {

	this.player = player;
}

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
			if (player.isUnderAnyThreat()) {
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