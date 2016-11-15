package dbk.abacus;

/**
 * Created by dbk on 16-Aug-16.
 */
public class GeneratorCheck {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            //System.out.println("NEXT GENERATOR");
            Generator generator = new Generator(3, 4);
            generator.firstStep(1, 4);
            generator.nextStep(4, false);
            generator.nextStep(4, false);
            System.out.println(generator);
        }

        System.out.println("COMBI");
        for (int i = 0; i < 10; i++) {
            //System.out.println("NEXT GENERATOR");
            Generator generator = new GeneratorCombi(3, 19,9,1);
            generator.generate();
            System.out.println(generator);
        }
    }
}
