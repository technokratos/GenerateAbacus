package dbk.cards;

import dbk.abacus.Tuple2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainCardsApp {

    public static final Color BACKGROUND = Color.WHITE;
    public static final Color LINE_COLORS = Color.BLACK;
    public static final String CARDS_DIR = "Cards";
    public static final int SEEK = 1;

    static Random r = new Random(SEEK);;
    static int ALL_CARDS = 50;
    static int NUM_ON_CARD = 4;

    static Card[] cards = new Card[ALL_CARDS];
    static {
        IntStream.range(0, ALL_CARDS).forEach(i-> cards[i] = new Card(i));
    }
    static List<Card> notFullCars = new ArrayList<>(Arrays.asList(cards));
    static List<Card>[] notFulledPositions = new List[NUM_ON_CARD];
    static {
        IntStream.range(0, NUM_ON_CARD)
                .forEach(i -> notFulledPositions[i] = new ArrayList<>(Arrays.asList(cards)));
    }

    static BufferedImage oval = null;
    static final int LINE_WIDTH = 20;
    static {
        try {
            oval = ImageIO.read(new File("/home/denis/IdeaProjects/GenerateAbacus/src/main/resources/oval300x100.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int OVAL_WIDTH = oval.getWidth();
    public static final int WIDTH_NUMBER = 3 * OVAL_WIDTH;
    public static final int OVAL_HEIGHT = oval.getHeight();
    public static final int HEIGHT_NUMBER = 7 * OVAL_HEIGHT;

    /**
     * 4 - 2
     *
     * 1   1   4   4
     * 2   3   2   3
     *
     *
     * @param args
     */

    public static void main(String[] args){



        IntStream.range(1, ALL_CARDS * NUM_ON_CARD/2 + 1).forEach(MainCardsApp::insert);

        Stream.of(cards).forEach(System.out::println);




        Stream.of(MainCardsApp.cards).collect(Collectors.groupingBy(c->c))
                .entrySet().stream()
                .filter(e-> e.getValue().size() > 1)
                .forEach(e-> System.out.println("Duplicated " + e.getKey() + " " + e.getValue().size()));


        makeDir(CARDS_DIR, false);
        String currentDir = CARDS_DIR + "/cards_" + ALL_CARDS + "_" + NUM_ON_CARD + "_seek_" + SEEK;
        makeDir(currentDir, true );

        int digits = (int) Math.ceil(Math.log10(NUM_ON_CARD * ALL_CARDS /2));
        List<Tuple2<BufferedImage, Card>> cardImages = Stream.of(cards)
                .map(c -> new Tuple2<>( drawCard(c,digits), c))
                .collect(Collectors.toList());
        IntStream.range(0, cardImages.size()).forEach(i -> {
            try {
                ImageIO.write(cardImages.get(i).getA(), "png", new File(currentDir+"/card" + i +"_" + cardImages.get(i).getB()+ ".png"));
                System.out.println("written image " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //List<Card> available = Arrays.asList(cards);



    }

    private static BufferedImage drawCard(Card c, int digits) {
        List<BufferedImage> numberImages = IntStream.of(c.nums)
                .mapToObj(num -> drawNum(num, digits))
                .collect(Collectors.toList());
        int cols = (int) Math.sqrt(NUM_ON_CARD);//4->2; 6->2; 9->3
        int rows = NUM_ON_CARD/cols;


        final int horSpace = OVAL_WIDTH;
        final int vertSpace = 2 * OVAL_HEIGHT;

        final BufferedImage first = numberImages.iterator().next();

        int border = 20;
//        final int width = cols * (digits * OVAL_WIDTH) + (cols - 1) * horSpace + 2 * border;
//        final int height = rows * HEIGHT_NUMBER + (rows - 1) * vertSpace + 2 * border;
        final int width = cols * (first.getWidth()) + (cols - 1) * horSpace + 2 * border;
        final int height = rows * first.getHeight() + (rows - 1) * vertSpace + 2 * border;
        final BufferedImage imageCard = new BufferedImage(width,
                height, BufferedImage.TYPE_3BYTE_BGR );

        final Graphics g = imageCard.createGraphics();




        //g.fillRect(0, 0, imageCard.getWidth(), imageCard.getHeight() );
        g.setColor(BACKGROUND);
        //g.fillRect(border, border, imageCard.getWidth() - 2 * border, imageCard.getHeight() - 2 * border);
        g.fillRect(0, 0, imageCard.getWidth(), imageCard.getHeight() );

//        g.setColor(Color.BLUE);
//        g.drawRect(1, 1, imageCard.getWidth() -1, imageCard.getHeight() -1);

        IntStream.range(0, cols)
                .forEach(x-> IntStream.range(0, rows)
                    .forEach(y-> g.drawImage(numberImages.get(x + y * cols),
                            border + x * ((digits * OVAL_WIDTH) + horSpace), border + y* (HEIGHT_NUMBER + vertSpace), null )));
        g.setColor(Color.LIGHT_GRAY);
        IntStream.range(0, cols)
                .forEach(x-> IntStream.range(0, rows)
                        .forEach(y-> g.drawRect(border + x * ((digits * OVAL_WIDTH) + horSpace), border + y* (HEIGHT_NUMBER + vertSpace),
                                first.getWidth(), first.getHeight() )));

        return imageCard;

    }

    private static BufferedImage drawNum(int num, int digits) {

        BufferedImage imageNum = new BufferedImage(digits * OVAL_WIDTH, HEIGHT_NUMBER, BufferedImage.TYPE_3BYTE_BGR );
        //draw 3  vert lines
        //draw hor line

        Graphics g = imageNum.getGraphics();
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, imageNum.getWidth(), imageNum.getHeight());
        g.setColor(LINE_COLORS);

        g.fill3DRect(0, OVAL_HEIGHT * 2 - LINE_WIDTH / 2,
                WIDTH_NUMBER, LINE_WIDTH, true);

        IntStream.range(0,digits)
                .forEach(i -> {
                    int startVertX = i * OVAL_WIDTH + OVAL_WIDTH / 2 - LINE_WIDTH / 4;
                    int endVertX = i * OVAL_WIDTH + OVAL_WIDTH / 2 + LINE_WIDTH / 4;
                    g.fill3DRect(startVertX, 0,
                           LINE_WIDTH/2, HEIGHT_NUMBER, true);
                });

        //15
        //d=0, ten = 10; value = 5;
        //d=1, ten = 100, value = 15;
        IntStream.range(0, digits )
                .forEach(d-> {
                    int ten = (int) Math.pow(10, d + 1);
                    int value = (num%ten) ;
                    if (d> 0) {
                        value = value/((int)Math.pow(10,d));
                    }
                    final int pos = digits - d - 1;
                    drawNum(value, pos, g);
                });

        return imageNum;
    }

    private static void drawNum(int num, int pos, Graphics g) {
        final int countOfUp;
        int posx = pos * oval.getWidth();
        int ovalH = OVAL_HEIGHT;
        final int posy;
        if (num >= 5) {
            countOfUp = num - 5;
            posy = ovalH - LINE_WIDTH/2;            //draw 5
        } else{
            posy = 0; //draw not 5
            countOfUp = num;
        }
        g.drawImage(oval, posx, posy , null);

        int height = 7 * ovalH;
        int startX = 2 * OVAL_HEIGHT + LINE_WIDTH/2;
        int oh = OVAL_HEIGHT;
        int lh = LINE_WIDTH;
        IntStream.range(0, 4)
                //.map(i-> 3 - i)
                .forEach(i-> {
                    final int ypos;
                    if (i < countOfUp) {
                        ypos = height - (4 - i) * oh - (oh - lh/2);
                    } else {
                        ypos = height - (4 - i) * oh ;

                    }
                    g.drawImage(oval, posx, ypos, null);
  //                 int posOnesX = i * ovalH + ovalH + upPosition;
//                    g.drawImage(oval, posx, ( height -  posOnesX), null);
                });
    }

    private static void makeDir(String header, boolean deleteAll) {
        File mainDir = new File(header);
        if (!mainDir.exists()) {
            mainDir.mkdir();
        } else if (deleteAll) {
            Stream.of(mainDir.listFiles()).forEach(File::delete);
        }
    }


    private static void insert(int num) {

        System.out.println("Start for: " + num);

        if(notFullCars.size() == 0) {
            System.out.println("it's strange, nothing card for " + num);
            return;
        }
        int firstCardNum = r.nextInt(notFullCars.size());

        Card firstCard = (Card) notFullCars.get(firstCardNum);

        Card secondCard;

        int iterationCount = 0;
        int secondCardNum;


            Object[] crossedCards = notFullCars.stream()
                    .filter(notFullCard -> notFullCard.crossEmpty(firstCard).length > 0
                            && notFullCard != firstCard)
                    .toArray();
            if (crossedCards.length == 0) {
                System.out.println("Nothing cross for " + num + " and card " + firstCard );
                return;
            }

            int secondCrossCardNum = r.nextInt(crossedCards.length);
            secondCard = (Card) crossedCards[secondCrossCardNum];


        int[] crossInCards = secondCard.crossEmpty(firstCard);


        int positionInCross = r.nextInt(crossInCards.length);
        int position = crossInCards[positionInCross];
        firstCard.set(position, num);
        secondCard.set(position, num);

        if (firstCard.isFilled()) {
            notFullCars.remove(firstCardNum);
        }
        if (secondCard.isFilled()) {
            notFullCars.remove(secondCard);//todo doesn't used id
        }

    }


    private static void checkLimit(int iterationCount, int num) {
        if (iterationCount%10 ==0) {
            System.out.println("for num: " + num + ", iteration is " + iterationCount);
        } else if(iterationCount > 1000) {
            System.out.println("for num: " + num + ", iteration is " + iterationCount);
            System.out.println("STOPPED for num: " + num + ", iteration is " + iterationCount);
            throw new IllegalStateException("Limit exceed");
        }
    }


    static class Card{
        final int id;
        int[] nums = new int[NUM_ON_CARD];

        Card(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Card card = (Card) o;

            return Arrays.equals(nums, card.nums);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(nums);
        }

        public int[] crossEmpty(Card card) {

            return IntStream.range(0, NUM_ON_CARD).filter(i ->  nums[i]==0 && card.nums[i]==0).toArray();
        }

        public void set(int position, int num) {
            nums[position] = num;
        }

        @Override
        public String toString() {
            return Arrays.toString(nums);
        }

        public boolean isFilled() {
            return IntStream.range(0, NUM_ON_CARD).allMatch(i ->  nums[i]!=0);
        }
    }
}
