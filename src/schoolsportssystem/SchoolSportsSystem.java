
package schoolsportssystem;

import view.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main Entry Point
 * Launches the Login Screen
 */
public class SchoolSportsSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
