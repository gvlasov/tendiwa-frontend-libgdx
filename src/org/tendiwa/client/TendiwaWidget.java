package org.tendiwa.client;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TendiwaWidget extends Table {
private Set<UiPortion> interestingUiPortions = new HashSet<>();

protected TendiwaWidget(UiPortion... uiPortionsOnInterest) {
	interestingUiPortions.addAll(Arrays.asList(uiPortionsOnInterest));
	debug();
}

/**
 * @return
 */
public final Collection<UiPortion> interstingUiPortions() {
	return interestingUiPortions;
}

public void onShow() {

}

public void onHide() {

}
}
