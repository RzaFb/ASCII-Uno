import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

    public static void main(String[] args)
    {

        Deck deck = new Deck();
        for(int i = 0; i < 4; i++)
        {
            String color = null;

            switch (i) {
                case 0 -> color = "red";
                case 1 -> color = "green";
                case 2 -> color = "blue";
                case 3 -> color = "black";
                default -> throw new IllegalStateException("Unexpected value: " + i);
            }

            for(int j = 2; j < 11; j++)
            {
                if(j == 7)
                {
                    if ("black".equals(color)) {
                        Card card1 = new Card(Integer.toString(j), color, 15);
                        deck.cards.add(card1);
                    } else {
                        Card card2 = new Card(Integer.toString(j), color, 10);
                        deck.cards.add(card2);
                    }
                } else {
                    Card card3 = new Card(Integer.toString(j), color, j);
                    deck.cards.add(card3);
                }
            }
            Card cardA = new Card("A", color, 11);
            Card cardB = new Card("B", color, 12);
            Card cardC = new Card("C", color, 12);
            Card cardD = new Card("D", color, 13);
            deck.cards.add(cardA);
            deck.cards.add(cardB);
            deck.cards.add(cardC);
            deck.cards.add(cardD);
        }
        Scanner sc = new Scanner(System.in);
        int choice;
        boolean clockwise = true;
        boolean turnEnd;
        int drawX2 = 0;
        int currentPlayer = 0;
        String currentColor = "";
        int maxPlayers;
        ArrayList<Player> players = new ArrayList<>();

        boolean max = false;
        do {
            System.out.println("3 players, 4 players or 5 players?");
            maxPlayers = sc.nextInt();

            if(maxPlayers < 6)
                if(maxPlayers>2)
                    max = true;
        }while (max == false);

        boolean correct = false;
        do {
            System.out.println("1. Play with bot\n2. Play with players");
            choice = sc.nextInt();

            switch (choice)
            {
                case 1, 2 -> correct = true;
            }
        }while(correct == false);

        if(choice == 1)
        {
            System.out.println("Enter your name:");
            String name = sc.nextLine();
            name = sc.nextLine();

            Player player = new Player(name, false);
            players.add(player);
            drawCard(deck, players.get(0), 7);

            for(int i = 1; i < maxPlayers; i++)
            {
                String botName = "bot" + Integer.toString(i);
                Player bot = new Player(botName, true);
                players.add(bot);
                drawCard(deck, players.get(i), 7);
            }
        } else {
            String skip = sc.nextLine();
            for(int i = 0; i < maxPlayers; i++)
            {
                System.out.println("Enter name for player" + (i+1) + ":");
                String name = sc.nextLine();

                Player player = new Player(name, false);
                players.add(player);
                drawCard(deck, players.get(i), 7);
            }
        }

        Player gameStart = new Player("Start", true);
        drawCard(deck, gameStart, 1);
        pickCard(deck, gameStart, 1);
        currentColor.equals(deck.cards.get(deck.cards.size()-1).color);

        do{
            turnEnd = false;
            do{
                clearScreen();
                playersList(players);
                showLastCard(deck);
                System.out.println();
                if(players.get(currentPlayer).bot == true)
                {
                    if(drawX2 > 0)
                    {
                        boolean hasSeven = false;
                        for(int i = 0; i < players.get(currentPlayer).cards.size(); i++)
                        {
                            if(players.get(currentPlayer).cards.get(i).symbol.equals("7"))
                            {
                                if(players.get(currentPlayer).cards.get(i).color.equals("black"))
                                {
                                    hasSeven = true;
                                    pickCard(deck, players.get(currentPlayer), i);
                                    drawX2 += 2;
                                    turnEnd = true;
                                }
                                else
                                {
                                    hasSeven = true;
                                    pickCard(deck, players.get(currentPlayer), i);
                                    drawX2++;
                                    turnEnd = true;
                                }
                                break;
                            }
                        }
                        if(hasSeven == false)
                        {
                            drawCard(deck, players.get(currentPlayer), (drawX2*2));
                            turnEnd = true;
                            drawX2 = 0;
                        }
                    } else {
                        boolean hasValid = false;
                        for(int i = 0; i < players.get(currentPlayer).cards.size(); i++)
                        {
                            if(players.get(currentPlayer).cards.get(i).symbol.equals(deck.cards.get(deck.cards.size()-1).symbol) || players.get(currentPlayer).cards.get(i).color.equals(currentColor))
                            {
                                hasValid = true;
                                break;
                            }
                        }
                        if(hasValid == true)
                        {
                            botMove(deck, players.get(currentPlayer), currentColor);
                            currentColor = deck.cards.get(deck.cards.size()-1).color;
                            switch (deck.cards.get(deck.cards.size()-1).symbol)
                            {
                                case "2" -> {
                                    Random rng = new Random();
                                    int r = rng.nextInt(players.get(currentPlayer).cards.size());
                                    int rr = rng.nextInt(maxPlayers);
                                    players.get(currentPlayer).score -= players.get(currentPlayer).cards.get(r).score;
                                    players.get(rr).score += players.get(currentPlayer).cards.get(r).score;
                                    players.get(rr).cards.add(players.get(currentPlayer).cards.get(r));
                                    players.get(currentPlayer).cards.remove(r);
                                    turnEnd = true;
                                }
                                case "7" -> {
                                    if(currentColor == "black")
                                        drawX2 += 2;
                                    else
                                        drawX2++;
                                    turnEnd = true;
                                }
                                case "10" -> {
                                    clockwise = changeDirection(clockwise);
                                    turnEnd = true;
                                }
                                case "A" -> {
                                    currentPlayer = nextTurn(maxPlayers, currentPlayer, clockwise);
                                    turnEnd = true;
                                }
                                case "B" -> {
                                    Random rng = new Random();
                                    int newColor = rng.nextInt(4);
                                    switch (newColor)
                                    {
                                        case 0 -> currentColor.equals("red");
                                        case 1 -> currentColor.equals("green");
                                        case 2 -> currentColor.equals("blue");
                                        case 3 -> currentColor.equals("black");
                                    }
                                    turnEnd = true;
                                }
                                case "8" -> {turnEnd = false;}
                                default -> {turnEnd = true;}

                            }
                        }
                        else
                        {
                            drawCard(deck, players.get(currentPlayer), 1);
                            if(players.get(currentPlayer).cards.get(players.get(currentPlayer).cards.size()-1).symbol.equals(deck.cards.get(deck.cards.size()-1).symbol) || players.get(currentPlayer).cards.get(players.get(currentPlayer).cards.size()-1).color.equals(currentColor))
                            {
                                pickCard(deck, players.get(currentPlayer), players.get(currentPlayer).cards.size()-1);
                            }
                            turnEnd = true;
                        }
                    }
                }
                else {
                    System.out.println(players.get(currentPlayer).name + "'s turn...");
                    showHand(players.get(currentPlayer));
                    if (drawX2 > 0)
                    {
                        int card = sc.nextInt();
                        if(players.get(currentPlayer).cards.get(card-1).symbol.equals("7"))
                        {
                            if(players.get(currentPlayer).cards.get(card-1).color.equals("black"))
                            {
                                pickCard(deck, players.get(currentPlayer), card);
                                drawX2 += 2;
                                turnEnd = true;
                            }else{
                                pickCard(deck, players.get(currentPlayer), card);
                                drawX2++;
                                turnEnd = true;
                            }
                        }else{
                            System.out.println("drawing penalty for not picking a 7");
                            drawCard(deck, players.get(currentPlayer), (drawX2*2));
                            drawX2 = 0;
                            turnEnd = true;
                        }
                    }else{
                        do {
                            int card = sc.nextInt();
                            if (card == 99) {
                                drawCard(deck, players.get(currentPlayer), 1);
                                if (players.get(currentPlayer).cards.get(players.get(currentPlayer).cards.size() - 1).symbol.equals(deck.cards.get(deck.cards.size() - 1).symbol) || players.get(currentPlayer).cards.get(players.get(currentPlayer).cards.size() - 1).symbol.equals(currentColor)) {
                                    pickCard(deck, players.get(currentPlayer), players.get(currentPlayer).cards.size() - 1);
                                    turnEnd = true;
                                }
                            }
                            if(card <= players.get(currentPlayer).cards.size()){
                                if(players.get(currentPlayer).cards.get(card-1).symbol.equals(deck.cards.get(deck.cards.size()-1).symbol) || players.get(currentPlayer).cards.get(card-1).color.equals(currentColor)) {
                                    pickCard(deck, players.get(currentPlayer), card);
                                    currentColor = deck.cards.get(deck.cards.size()-1).color;
                                    turnEnd = true;
                                }
                            }
                        }while (turnEnd == false);
                        String sym = deck.cards.get(deck.cards.size()-1).symbol;
                        switch(sym)
                        {
                            case "2" -> {
                                two(currentPlayer, players);
                                turnEnd = true;
                            }
                            case "7" -> {
                                if(currentColor == "black")
                                    drawX2 += 2;
                                else
                                    drawX2++;
                                turnEnd = true;
                            }
                            case "10" -> {
                                clockwise = changeDirection(clockwise);
                                turnEnd = true;
                            }
                            case "A" -> {
                                currentPlayer = nextTurn(maxPlayers, currentPlayer, clockwise);
                                turnEnd = true;
                            }
                            case "B" -> {
                                currentColor = wildCard();
                                turnEnd = true;
                            }
                            case "8" -> {turnEnd = false;}
                            default -> {turnEnd = true;}

                        }
                    }
                }
            }while (turnEnd == false);
            currentPlayer = nextTurn(maxPlayers, currentPlayer, clockwise);
        }while(checkGameOver(players) == false);
    }

    public static boolean changeDirection(boolean clockwise)
    {
        if(clockwise == true)
            return false;
        else
            return true;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static String wildCard()
    {
        System.out.println("pick a new color\t1.red\t2.green\t3.blue\t4.black");
        Scanner scanner = new Scanner(System.in);
        do{
            int i = scanner.nextInt();
            switch (i)
            {
                case 1 -> {return "red";}
                case 2 -> {return "green";}
                case 3 -> {return "blue";}
                case 4 -> {return "black";}
                default -> {System.out.println("Please enter a correct input");}
            }
        }while(true);
    }

    public static void playersList(ArrayList<Player> players)
    {
        for(int i =0; i < players.size(); i++)
        {
            System.out.println(players.get(i).name + ": " + players.get(i).score + " | " + players.get(i).cards.size() + " cards");
        }
    }

    public static void two(int i, ArrayList<Player> players)
    {
        System.out.println("Give a random card to a player of your choice\tEnter the player's name:");
        Scanner scan = new Scanner(System.in);
        String name = scan.nextLine();

        for(int j = 0; j < players.size(); j++)
        {
            if(players.get(j).name.equals(name))
            {
                Random random = new Random();
                int rand = random.nextInt(players.get(i).cards.size());
                players.get(i).score -= players.get(i).cards.get(rand).score;
                players.get(j).score += players.get(i).cards.get(rand).score;
                players.get(j).cards.add(players.get(i).cards.get(rand));
                players.get(i).cards.remove(players.get(i).cards.get(rand));
            }
        }
    }

    public static void botMove(Deck deck, Player player, String color)
    {
        for(int i = 0; i < player.cards.size(); i++)
        {
            if(player.cards.get(i).symbol.equals(deck.cards.get(deck.cards.size()-1).symbol)||player.cards.get(i).color.equals(color))
            {
                pickCard(deck, player, i+1);
                return;
            }
        }
    }

    public static void drawCard(Deck deck, Player player, int num)
    {
        Random random = new Random();
        for(int i = 0; i < num; i++)
        {
            int j = random.nextInt(deck.cards.size() - 1);
            player.cards.add(deck.cards.get(j));
            player.score += deck.cards.get(j).score;
            deck.cards.remove(j);
        }
    }

    public static boolean checkGameOver(ArrayList<Player> players)
    {
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).cards.size() == 0)
                return true;
        }
        return false;
    }

    public static int nextTurn(int max, int i, boolean clockwise)
    {
        if(clockwise == true)
        {
            if(i == max-1)
                i = 0;
            else
                i++;
        }
        else
        {
            if(i == 0)
                i = max-1;
            else
                i--;
        }
        return i;
    }

    public static void showHand(Player player)
    {
        int size = player.cards.size();

        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < size - 1; j++)
            {
                switch (player.cards.get(j).color)
                {
                    case "red":
                        if(i == 1)
                        {
                            System.out.print(ANSI_RED_BACKGROUND + ANSI_BLACK + "  " + player.cards.get(j).symbol + " \t▕" + ANSI_RESET);
                        }else{
                            System.out.print(ANSI_RED_BACKGROUND + ANSI_BLACK + "\t\t▕" + ANSI_RESET);
                        }
                        break;
                    case "green":
                        if(i == 1)
                        {
                            System.out.print(ANSI_GREEN_BACKGROUND + ANSI_BLACK + "  " + player.cards.get(j).symbol + " \t▕" + ANSI_RESET);
                        }else{
                            System.out.print(ANSI_GREEN_BACKGROUND + ANSI_BLACK + "\t\t▕" + ANSI_RESET);
                        }
                        break;
                    case "blue":
                        if(i == 1)
                        {
                            System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "  " + player.cards.get(j).symbol + " \t▕" + ANSI_RESET);
                        }else{
                            System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "\t\t▕" + ANSI_RESET);
                        }
                        break;
                    case "black":
                        if(i == 1)
                        {
                            System.out.print(ANSI_BLACK_BACKGROUND + "  " + player.cards.get(j).symbol + " \t▕" + ANSI_RESET);
                        }else{
                            System.out.print(ANSI_BLACK_BACKGROUND + "\t\t▕" + ANSI_RESET);
                        }
                }
            }
            switch (player.cards.get(player.cards.size()-1).color)
            {
                case "red":
                    switch (i)
                    {
                        case 1:
                            System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + "  " + player.cards.get(player.cards.size()-1).symbol + "\t\t\t" + ANSI_RESET);
                            break;
                        case 6:
                            System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + "\t\t\t " + player.cards.get(player.cards.size()-1).symbol + "\t" + ANSI_RESET);
                            break;
                        default:
                            System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + "\t\t\t\t" + ANSI_RESET);
                    }
                    break;
                case "green":
                    switch (i)
                    {
                        case 1:
                            System.out.println(ANSI_GREEN_BACKGROUND + ANSI_BLACK + "  " + player.cards.get(player.cards.size()-1).symbol + "\t\t\t" + ANSI_RESET);
                            break;
                        case 6:
                            System.out.println(ANSI_GREEN_BACKGROUND + ANSI_BLACK + "\t\t\t " + player.cards.get(player.cards.size()-1).symbol + "\t" + ANSI_RESET);
                            break;
                        default:
                            System.out.println(ANSI_GREEN_BACKGROUND + ANSI_BLACK + "\t\t\t\t" + ANSI_RESET);
                    }
                    break;
                case "blue":
                    switch (i)
                    {
                        case 1:
                            System.out.println(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "  " + player.cards.get(player.cards.size()-1).symbol + "\t\t\t" + ANSI_RESET);
                            break;
                        case 6:
                            System.out.println(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "\t\t\t " + player.cards.get(player.cards.size()-1).symbol + "\t" + ANSI_RESET);
                            break;
                        default:
                            System.out.println(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "\t\t\t\t" + ANSI_RESET);
                    }
                    break;
                case "black":
                    switch (i)
                    {
                        case 1:
                            System.out.println(ANSI_BLACK_BACKGROUND + "  " + player.cards.get(player.cards.size()-1).symbol + "\t\t\t" + ANSI_RESET);
                            break;
                        case 6:
                            System.out.println(ANSI_BLACK_BACKGROUND +"\t\t\t " + player.cards.get(player.cards.size()-1).symbol + "\t" + ANSI_RESET);
                            break;
                        default:
                            System.out.println(ANSI_BLACK_BACKGROUND + "\t\t\t\t" + ANSI_RESET);
                    }
            }
        }
        for(int i = 0; i < player.cards.size(); i++)
        {
            System.out.print("\t" + (i+1) + "\t");
        }
        System.out.println("\nenter 99 to draw a card");
    }

    public static void showLastCard(Deck deck)
    {
        int s = deck.cards.size()-1;
        switch (deck.cards.get(deck.cards.size()-1).color)
        {
            case "red":
                for(int i = 0; i < 8; i++)
                {
                    switch (i)
                    {
                        case 1:
                            System.out.print(ANSI_RED_BACKGROUND + ANSI_BLACK + " " + deck.cards.get(s).symbol + "\t\t\t\t" + ANSI_RESET + "\n");
                            break;
                        case 6:
                            System.out.print(ANSI_RED_BACKGROUND + ANSI_BLACK + "\t\t\t " + deck.cards.get(s).symbol + "\t" + ANSI_RESET + "\n");
                            break;
                        default:
                            System.out.print(ANSI_RED_BACKGROUND + "\t\t\t\t" + ANSI_RESET + "\n");
                    }
                }
                break;
            case "green":
                for(int j = 0; j < 8; j++)
                {
                    switch (j)
                    {
                        case 1:
                            System.out.print(ANSI_GREEN_BACKGROUND + ANSI_BLACK + " " + deck.cards.get(s).symbol + "\t\t\t\t" + ANSI_RESET + "\n");
                            break;
                        case 6:
                            System.out.print(ANSI_GREEN_BACKGROUND + ANSI_BLACK + "\t\t\t " + deck.cards.get(s).symbol + "\t" + ANSI_RESET + "\n");
                            break;
                        default:
                            System.out.print(ANSI_GREEN_BACKGROUND + "\t\t\t\t" + ANSI_RESET + "\n");
                    }
                }
                break;
            case "blue":
                for(int k = 0; k < 8; k++)
                {
                    switch (k)
                    {
                        case 1:
                            System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLACK + " " + deck.cards.get(s).symbol + "\t\t\t\t" + ANSI_RESET + "\n");
                            break;
                        case 6:
                            System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "\t\t\t " + deck.cards.get(s).symbol + "\t" + ANSI_RESET + "\n");
                            break;
                        default:
                            System.out.print(ANSI_BLUE_BACKGROUND + "\t\t\t\t" + ANSI_RESET + "\n");
                    }
                }
                break;
            case "black":
                for(int w = 0; w < 8; w++)
                {
                    switch (w)
                    {
                        case 1:
                            System.out.print(ANSI_BLACK_BACKGROUND + " " + deck.cards.get(s).symbol + "\t\t\t\t" + ANSI_RESET + "\n");
                            break;
                        case 6:
                            System.out.print(ANSI_BLACK_BACKGROUND + "\t\t\t " + deck.cards.get(s).symbol + "\t" + ANSI_RESET + "\n");
                            break;
                        default:
                            System.out.print(ANSI_BLACK_BACKGROUND + "\t\t\t\t" + ANSI_RESET + "\n");

                    }
                }
                break;
        }
    }

    public static void pickCard(Deck deck, Player player, int num)
    {
        player.score -= player.cards.get(num-1).score;
        deck.cards.add(player.cards.get(num-1));
        player.cards.remove(num-1);

    }
}
