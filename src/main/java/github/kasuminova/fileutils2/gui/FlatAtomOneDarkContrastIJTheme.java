package github.kasuminova.fileutils2.gui;

import com.formdev.flatlaf.IntelliJTheme;

import java.io.IOException;
import java.util.Objects;

public class FlatAtomOneDarkContrastIJTheme extends IntelliJTheme.ThemeLaf {
    public static final String NAME = "Atom One Dark Contrast (Material)";

    public static boolean setup() {
        try {
            return setup( new FlatAtomOneDarkContrastIJTheme() );
        } catch( RuntimeException ex ) {
            return false;
        }
    }

    public static void installLafInfo() {
        installLafInfo( NAME, FlatAtomOneDarkContrastIJTheme.class );
    }

    public FlatAtomOneDarkContrastIJTheme() {
        super(loadTheme());
    }

    private static IntelliJTheme loadTheme() {
        try {
            return new IntelliJTheme(Objects.requireNonNull(FlatAtomOneDarkContrastIJTheme.class.getResourceAsStream(
                    "/theme/Atom One Dark Contrast.theme.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
