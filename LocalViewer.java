import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LocalViewer extends Application {
    public static void main(String[] args) { launch(args); }
    
    @Override 
    public void start(Stage stage) {
        WebView webView = new WebView();
        List<String> params = getParameters().getRaw();
        
        // FIXED URL HANDLING - handles bare filenames, full paths, and web URLs
        String url = params.isEmpty() ? Paths.get("index.htm").toUri().toString() : params.get(0);
        
        // Convert bare filenames/paths to proper file:// URLs
        if (!url.startsWith("http") && !url.startsWith("file://")) {
            try {
                Path path = Paths.get(url);
                url = path.toAbsolutePath().toUri().toString();
            } catch (Exception e) {
                // Fallback to relative path
                url = Paths.get(url).toUri().toString();
            }
        }
        
        String title = params.size() > 1 ? params.get(1) : "Local HTML Viewer";
        double width = params.size() > 2 && !params.get(2).equalsIgnoreCase("auto") ? 
                       Double.parseDouble(params.get(2)) : 1200;
        double height = params.size() > 3 && !params.get(3).equalsIgnoreCase("auto") ? 
                        Double.parseDouble(params.get(3)) : 800;
        
        webView.getEngine().load(url);
        Scene scene = new Scene(webView, width, height);
        stage.setScene(scene);
        stage.setTitle(title);
        
        // Window positioning & behavior (skip if fullscreen/maximized)
        boolean fullscreen = params.size() > 4 && params.get(4).equalsIgnoreCase("true");
        boolean maximized = params.size() > 5 && params.get(5).equalsIgnoreCase("true");
        boolean resizable = params.size() > 6 ? !params.get(6).equalsIgnoreCase("false") : true;
        boolean alwaysOnTop = params.size() > 7 && params.get(7).equalsIgnoreCase("true");
        double x = params.size() > 8 && !params.get(8).equalsIgnoreCase("center") ? Double.parseDouble(params.get(8)) : -1;
        double y = params.size() > 9 && !params.get(9).equalsIgnoreCase("center") ? Double.parseDouble(params.get(9)) : -1;
        
        stage.setResizable(resizable);
        stage.setAlwaysOnTop(alwaysOnTop);
        
        if (fullscreen) {
            stage.setFullScreen(true);
        } else if (maximized) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(width);
            stage.setHeight(height);
            if (x >= 0 && y >= 0) {
                stage.setX(x);
                stage.setY(y);
            } else {
                centerWindow(stage);
            }
        }
        
        stage.show();
    }
    
    private void centerWindow(Stage stage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
    }
}
