import com.google.common.io.Resources;
import junit.framework.TestCase;
import org.junit.Test;
import org.tendiwa.lexeme.Language;
import org.tendiwa.lexeme.implementations.Russian;

import java.net.URL;

public class LocalizationTest extends TestCase {
    @Test
    public void test() {
        Language russian = new Russian();
        URL resource = Resources.getResource("language/ru_RU/actions.ru_RU.words");
        russian.loadDictionary(resource);
    }

}
