package dbk.cards;

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

    static Random r = new Random();//1);
    static int ALL_CARDS = 30;
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

        Stream.of(cards).collect(Collectors.groupingBy(c->c))
                .entrySet().stream()
                .filter(e-> e.getValue().size()>1)
                .forEach(e-> System.out.println("Duplicated " + e.getKey() + " " + e.getValue().size()));


        //List<Card> available = Arrays.asList(cards);



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
            return "Card{" + Arrays.toString(nums) +
                    '}';
        }

        public boolean isFilled() {
            return IntStream.range(0, NUM_ON_CARD).allMatch(i ->  nums[i]!=0);
        }
    }
}
