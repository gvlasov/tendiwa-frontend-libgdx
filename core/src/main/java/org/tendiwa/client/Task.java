package org.tendiwa.client;

/**
 * A task represents a user action that is processed for several turns. For example, when user clicks with a mouse to
 * send the PlayerCharacter multiple cells away, or when user leaves the PlayerCharacter to rest for multiple turns.
 */
public interface Task {
boolean ended();

void execute();
}
