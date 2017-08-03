package dbk.abacus;

import dbk.odf.Settings;
import dbk.rand.RandomLevel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dbk on 18-Aug-16.
 */
public class Lesson {
    String title;
    private final List<Settings> settings;
    Marker marker = new FormulaMarker();



    //todo generator should be use length of list to generate chance
    final TreeMap<Integer,List<Integer>> availableMap = new TreeMap<>();
    final TreeMap<Integer, List<Integer>> positiveMap = new TreeMap<>();
    final TreeMap<Integer, List<Integer>> negativeMap = new TreeMap<>();
    final TreeMap<Integer,List<Integer>> obligatoryMap = new TreeMap<>();
    final HashMap<Integer, List<Tuple2<Integer, Integer>>> resultMap = new HashMap<>();
    final TreeMap<Integer,List<Integer>> blockedMap = new TreeMap<>();
    List<Double> probability = new ArrayList<>();

    Random r = RandomLevel.getR();


    public Lesson(Lesson orig, String index) {
        this(orig.getTitle() + index, orig.settings);
        availableMap.putAll(orig.availableMap);
        positiveMap.putAll(orig.positiveMap);
        negativeMap.putAll(orig.negativeMap);
        obligatoryMap.putAll(orig.obligatoryMap);
        resultMap.putAll(orig.resultMap);
        blockedMap.putAll(orig.blockedMap);
        probability.addAll(orig.probability);


    }

    public Lesson(String name, List<Settings> settings) {
        this.title = name;
        this.settings = settings.stream().map(Settings::new).collect(Collectors.toList());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void put(Integer number, List<Integer> list) {
        list.forEach(i-> put(number, i));
    }
    public void put(Integer number, List<Integer> list, boolean obligatory) {
        list.forEach(i-> put(number, i, obligatory));
    }

    public void put(Integer number, Integer pair) {
        put(number, pair, false);
    }
    public void put(Integer number, Integer pair, boolean obligatory) {
        TreeMap<Integer, List<Integer>> availableMap = this.availableMap;
        addToMap(number, pair, availableMap);
        addToTupleMapForSum(number, pair, resultMap);
        if (obligatory) {
            addToMap(number, pair, obligatoryMap);
        }
        if (pair >0) {
            addToMap(number, pair, positiveMap);
        } else {
            addToMap(number, pair, negativeMap);
        }
    }

    public void putBlocked(Integer key, Integer pair) {
        addToMap(key, pair, blockedMap);
    }

    public void remove(Integer key, List<Integer> pairs) {
        pairs.forEach(pair -> remove(key, pair));
    }
    public void remove(Integer key, Integer pair) {
        int keyWithPair = key + pair;
        List<Tuple2<Integer, Integer>> tuple2s = resultMap.get(keyWithPair);
        if (tuple2s != null) {
            tuple2s.remove(new Tuple2<>(key, pair));
            if (tuple2s.isEmpty()) {
                resultMap.remove(keyWithPair);
            }
        }
        removeFromMap(key, pair, this.obligatoryMap);
        removeFromMap(key, pair, positiveMap);
        removeFromMap(key, pair, negativeMap);
        removeFromMap(key, pair, availableMap);
    }

    private void removeFromMap(Integer key, Integer pair, TreeMap<Integer, List<Integer>> map) {
        List<Integer> list = map.get(key);
        if (list != null) {
            list.remove(pair);
            if (list.isEmpty()) {
                map.remove(key);
            }
        }
    }

    private static void addToTupleMapForSum(Integer number, Integer pair, Map<Integer, List<Tuple2<Integer, Integer>>> map) {
        Integer sum = number + pair;
        List<Tuple2<Integer, Integer>> list = map.get(sum);
        if( list == null) {
            list = new ArrayList<>();
            map.put(sum, list);
        }
        list.add(new Tuple2<>(number, pair));
    }


    public static void addToMap(Integer number, Integer pair, Map<Integer, List<Integer>> map) {
        List<Integer> list = map.get(number);
        if( list == null) {
            list = new ArrayList<>();
            map.put(number, list);
        }
        list.add(pair);
    }
    public List<Integer> get(Integer number){
        return availableMap.get(number);
    }

    public List<Integer> get(Integer number, boolean positve){
        if(positve) {
            return positiveMap.get(number);
        } else {
            return negativeMap.get(number);
        }
    }
    public List<Integer> getPositive(Integer number) {
        return positiveMap.get(number);
    }
    public List<Integer> getPositiveKeys() {
        return new ArrayList<>(positiveMap.navigableKeySet());
    }

    public List<Integer> getNegative(Integer number) {
        return negativeMap.get(number);
    }



    public double getPositiveRelation(Integer number) {
        double pos = getPositive(number).size();
        List<Integer> negList = getNegative(number);
        double negative = (negList == null)? 0:negList.size();
        return pos/(pos + negative);
    }

    public boolean isObligatoryOperand(Integer number, Integer pair) {
        List<Integer> pairs = obligatoryMap.get(number);
        return (pairs != null) && pairs.contains(pair);
    }

    public List<Integer> getObligatoryPair(Integer number) {
        return obligatoryMap.get(number);
    }

    public NavigableSet<Integer> getKeyNumbers() {
        return new TreeSet<>(availableMap.navigableKeySet());
    }


    public List<Integer> getObligatoryKeyNumbers(){
        return new ArrayList<>(obligatoryMap.navigableKeySet());
    }

    public List<Tuple2<Integer, Integer>> getResultMap(Integer result) {
        return resultMap.get(result);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "title='" + title + '\'' +
                ", settings=" + settings +
                ", availableMap=" + availableMap +
                ", obligatoryMap=" + obligatoryMap +
                ", resultMap=" + resultMap +
                ", probability=" + probability +
                '}';
    }

    public List<Settings> getSettings() {
        return settings;
    }

    public Marker getMarker() {
        return marker;
    }

    public TreeMap<Integer, List<Integer>> getBlockedMap() {
        return blockedMap;
    }
}
