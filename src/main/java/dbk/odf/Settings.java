package dbk.odf;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.TreeSet;

/**
 * series
 * steps
 * tens
 * hundreds
 * thouthends
 * decimal
 */
//@NoArgsConstructor
//@AllArgsConstructor
public class Settings {
    public Settings() {
    }

    public Settings(Settings s) {
        this(s.series,
                s.steps,
                s.decimals,
                s.duplicate,
                s.description,
                s.description1,
                s.description2,
                s.extensionDigit,
                s.extensionCount,
                s.addSum,
                s.formula,
                s.required);
    }

    public Settings(int series, int steps, int decimals, int duplicate, String description, String description1, String description2, int extensionDigit, int extensionCount, boolean addSum, TreeSet<String> formula, TreeSet<String> required) {
        this.series = series;
        this.steps = steps;
        this.decimals = decimals;
        this.duplicate = duplicate;
        this.description = description;
        this.description1 = description1;
        this.description2 = description2;
        this.extensionDigit = extensionDigit;
        this.extensionCount = extensionCount;
        this.addSum = addSum;
        this.formula = formula;
        this.required = required;
    }

    int series = 10;
    int steps = 4;
    /**
     *  количество знаков после запятой
     */
    int decimals = 0;
    /**
     * количество простых повторов *11
     */
    int duplicate = 0;
    //Урок
    String description = "";
    //Тема
    String description1 = "";
    //Домашняя работа
    String description2 = "";

    /**
     * количество дополнительных знаков
     */
    private int extensionDigit;
    /**
     * количество цифр с дополнительными знаками, меньше или ровно количеству шагов
     */
    private int extensionCount;
    private boolean addSum = false;

    private TreeSet<String> formula = new TreeSet<>();
    private TreeSet<String> required = new TreeSet<>();

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public int getDuplicate() {
        return duplicate;
    }

    public String getDescription() {
        return description;
    }

    /**
     * series
     * steps
     * extension
     * decimals
     * duplicate
     *
     * @param settingsName
     * @param value
     */

    public void setValue(String settingsName, String value) {
        try {
            switch (settingsName) {
                case "series":
                    setSeries(Integer.valueOf(value));
                    break;
                case "steps":
                    setSteps(Integer.valueOf(value));
                    break;
                case "extensionDigit":
                    setExtensionDigit(Integer.valueOf(value));
                    break;
                case "extensionCount":
                    setExtensionCount(Integer.valueOf(value));
                    break;
                case "decimals":
                    setDecimals(Integer.valueOf(value));
                    break;
                case "duplicate":
                    duplicate = Integer.valueOf(value);
                    break;
                case "description":
                    description = value;
                    break;
                case "description1":
                    description1 = value;
                    break;
                case "description2":
                    description2 = value;
                    break;
                case "addSum":
                    this.addSum = Boolean.valueOf(value);
                    break;
                case "formula":
                    this.formula.add(value);
                    break;
                case "required":
                    this.required.add(value);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    public Integer getExtensionCount() {
        return extensionCount;
    }

    public boolean getAddSum() {
        return addSum;
    }

    public void setAddSum(boolean addSum) {
        this.addSum = addSum;
    }

    public void setExtensionCount(int extensionCount) {
        this.extensionCount = extensionCount;
    }

    public void setExtensionDigit(int extensionDigit) {
        this.extensionDigit = extensionDigit;
    }

    public int getExtensionDigit() {
        return extensionDigit;
    }

    public boolean isAddSum() {
        return addSum;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public TreeSet<String> getFormula() {
        return formula;
    }

    public TreeSet<String> getRequired() {
        return required;
    }

    @Override
    public String toString() {
        return "Params{" +
                "series=" + series +
                ", steps=" + steps +
                ", decimals=" + decimals +
                ", duplicate=" + duplicate +
                ", description='" + description + '\'' +
                ", description1='" + description1 + '\'' +
                ", description2='" + description2 + '\'' +
                ", extensionDigit=" + extensionDigit +
                ", extensionCount=" + extensionCount +
                ", addSum=" + addSum +
                '}';
    }
}
