package dbk.odf;

import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dbk on 30.08.2016.
 */
public class SimpleGeneratorTest {

    @Test
    public void checkSettings() {
        Settings locSet = new Settings();
        Assert.assertEquals(0, locSet.getDecimals());
        Assert.assertEquals(10, locSet.getSeries());
        Assert.assertEquals(4, locSet.getSteps());

    }



    @org.junit.Test
    public void testGenerate1_9() throws Exception {

        List<Level> levels = new ArrayList<>();
        List<Settings> settings = new ArrayList<>();
        Settings locSet = new Settings();
        locSet.setSteps(2);
        locSet.setSeries(1);
        settings.add(locSet);

        Level level = new Level("test", settings);
        levels.add(level);
        level.put(1, Arrays.<Integer>asList(9));
        level.put(9, Arrays.<Integer>asList(1));
        SimpleGenerator generator = new SimpleGenerator(levels);
        List<Tuple2<Level, List<List<List<Integer>>>>> generate = generator.generate(true);
        System.out.println(generate);
        List<Integer> steps = generate.iterator().next().getB().iterator().next().iterator().next();
        List<List<Integer>> results = new ArrayList<>();
        results.add(Arrays.asList(1,9, 10));
        results.add(Arrays.asList(9,1, 10));
        Assert.assertTrue("" + steps, results.contains(steps));
    }


    @org.junit.Test
    public void testGenerate1_8_9() throws Exception {

        List<Level> levels = new ArrayList<>();
        List<Settings> settings = new ArrayList<>();
        Settings locSet = new Settings();
        locSet.setSteps(2);
        locSet.setSeries(1);
        settings.add(locSet);

        Level level = new Level("test", settings);
        levels.add(level);
        level.put(1, Arrays.<Integer>asList(9, 8));
        level.put(8, Arrays.<Integer>asList(1));
        level.put(9, Arrays.<Integer>asList(1));
        SimpleGenerator generator = new SimpleGenerator(levels);
        List<Tuple2<Level, List<List<List<Integer>>>>> generate = generator.generate(true);
        System.out.println(generate);
        List<Integer> steps = generate.iterator().next().getB().iterator().next().iterator().next();
        List<List<Integer>> results = new ArrayList<>();
        results.add(Arrays.asList(1,9, 10));
        results.add(Arrays.asList(9,1, 10));
        results.add(Arrays.asList(1,8, 9));
        results.add(Arrays.asList(8,1, 9));
        Assert.assertTrue("" + steps, results.contains(steps));
    }
}