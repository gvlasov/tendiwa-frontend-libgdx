package org.tendiwa.client;

import com.google.common.io.Resources;
import org.tendiwa.lexeme.Language;
import org.tendiwa.lexeme.Localizable;
import org.tendiwa.lexeme.Modifier;
import org.tendiwa.lexeme.implementations.Russian;

import java.util.LinkedList;
import java.util.List;

public enum Languages {
	INSTANCE;
Language currentLanguage;

public static String getText(String textLocalizationId, Localizable... params) {
	return INSTANCE.currentLanguage.getLocalizedText(textLocalizationId, params);
}

public static String getWord(Localizable thing, boolean capitalizeFirstLetter, List<Modifier> modifiers) {
	return INSTANCE.currentLanguage.getLocalizedWord(thing.getLocalizationId(), capitalizeFirstLetter, modifiers);
}

public static String getWord(Localizable thing, boolean capitalizeFirstLetter) {
	return INSTANCE.currentLanguage.getLocalizedWord(thing.getLocalizationId(), capitalizeFirstLetter, new LinkedList<Modifier>());
}

public static Language getCurrentLanguage() {
	return INSTANCE.currentLanguage;
}

public static void init() {
	INSTANCE.currentLanguage = new Russian();
	INSTANCE.currentLanguage.loadCorpus(Resources.getResource("language/ru_RU/messages.ru_RU.texts"));
	INSTANCE.currentLanguage.loadCorpus(Resources.getResource("language/ru_RU/uiActions.ru_RU.texts"));
	INSTANCE.currentLanguage.loadCorpus(Resources.getResource("language/ru_RU/ui.ru_RU.texts"));
	INSTANCE.currentLanguage.loadCorpus(Resources.getResource("language/ru_RU/events.ru_RU.texts"));
	INSTANCE.currentLanguage.loadDictionary(Resources.getResource("language/ru_RU/actions.ru_RU.words"));
	INSTANCE.currentLanguage.loadDictionary(Resources.getResource("language/ru_RU/characters.ru_RU.words"));
	INSTANCE.currentLanguage.loadDictionary(Resources.getResource("language/ru_RU/sounds.ru_RU.words"));
	INSTANCE.currentLanguage.loadDictionary(Resources.getResource("language/ru_RU/items.ru_RU.words"));
}
}
