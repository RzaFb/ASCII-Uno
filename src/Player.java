import java.util.ArrayList;

public class Player {
    ArrayList<Card> cards = new ArrayList<>();
    String name;
    int score;
    boolean bot;

    public Player(String name,boolean bot)
    {
        this.name = name;
        this.score = 0;
        this.bot = bot;
    }
}
