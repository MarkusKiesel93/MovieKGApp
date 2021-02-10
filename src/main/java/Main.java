public class Main {

    public static void main(String[] args) {

        String dataPath = System.getProperty("user.dir") + "/data/";

        MovieKG app = new MovieKG(dataPath);
        app.loadMovieTTL();

        KgGUI fancyGUI = new KgGUI(app);
        fancyGUI.setVisible(true);

    }
}
