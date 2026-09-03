package coursework;

/**
 * Classpath-friendly entry point for IDEs and packaged launches.
 *
 * Starting an Application subclass directly makes the Java launcher expect
 * JavaFX on the module path. This plain class lets StartGUI initialize JavaFX
 * from the Maven-provided classpath instead.
 */
public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        StartGUI.main(args);
    }
}
