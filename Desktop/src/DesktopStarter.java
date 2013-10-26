import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.tendiwa.client.BookFun;
import org.tendiwa.client.GameScreen;
import org.tendiwa.client.TendiwaGame;

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Title";
        cfg.useGL20 = true;
        cfg.width = 1024;
        cfg.height = 768;
	    cfg.resizable = false;
	    cfg.vSyncEnabled = false;
//        new LwjglApplication(new BookFun(), cfg);
	    new LwjglApplication(new TendiwaGame(cfg), cfg);
    }
}