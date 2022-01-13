import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>This is the KeyController (KeyListener)</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class KeyController extends KeyAdapter {
    private final Presentation presentation; //Commands are given to the presentation

    public KeyController(Presentation p) {
        presentation = p;
    }

    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_DOWN, KeyEvent.VK_ENTER, KeyEvent.VK_SPACE, KeyEvent.VK_RIGHT -> presentation.nextSlide();
            case KeyEvent.VK_LEFT, KeyEvent.VK_UP -> presentation.prevSlide();
            case 'q', 'Q', KeyEvent.VK_ESCAPE -> System.exit(0);
        }
    }
}
