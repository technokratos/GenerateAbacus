package dbk.notebooks;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Created by denis on 29.08.17.
 */
public class Params {

    @Parameter(names = "--trainer")
    private boolean trainer = false;

    @Parameter(names = "--level")
    public int level = 3;
    @Parameter(names = "--odd")
    public Odd odd = Odd.ODD;
    @Parameter(names = "--seek")
    public int seek = 2 * level + ((odd == Odd.ODD)? 1:0);
    @Parameter(names = "--trainerSeek")
    public int trainerSeek = seek * 10;
    @Parameter(names = "--taskDir")
    String taskDir = "tasks/level" + level + "/";
    @Parameter(names = "--taskName")
    public String taskName = "abacus_formula_" + odd.toString();
    @Parameter(names = "--outDir")
    private String outDir = "exercises/level" + level + "/";
    @Parameter(names = "--outFile")
    String outFile = outDir + "уровень_" + level + "_" + ((odd == Odd.EVEN)?1:2) + "." + seek;
    @Parameter(names = "--outTrainerDir")
    String outTrainerDir = outDir + "trainer";
    @Parameter(names = "--outMarker")
    String outMarker = outFile + ".marker.xls";
    @Parameter(names = "--trainerMarkerFile")
    String trainerMarkerFile = outTrainerDir + "/" + "trainer." + level + "."
            + ((odd == Odd.EVEN)?1:2) + "." + seek + ".marker.xls";

    @Parameter(names = "--help", help = true)
    private boolean help = true;

    @Parameter(names = "--page")
    public MainFormulaApp.PAGE_ORIENTATION page = MainFormulaApp.PAGE_ORIENTATION.PORTRAIT;

    public static final int LANDSCAPE_SERIES = 10;
    public static final int PORTRAIT_SERIES = 7;
    public static final int LANDSCAPE_TASKS_ON_PAGE = 4;

    public static final int PORTRAINT_TASKS_ON_PAGE = 6;

    public Params(String[] args){
//        JCommander.newBuilder()
//                .addObject(this)
//                .build()
//                .parse(args);
        JCommander jCommander = new JCommander(this);
        jCommander.parse(args);
        if (help) {
            jCommander.usage();
            return;
        }
    }

    public boolean isTrainer() {
        return trainer;
    }

    public MainFormulaApp.PAGE_ORIENTATION getPage() {
        return page;
    }

    public int getLevel() {
        return level;
    }

    public Odd getOdd() {
        return odd;
    }

    public int getSeek() {
        return seek;
    }

    public int getTrainerSeek() {
        return trainerSeek;
    }

    public String getTaskDir() {
        return taskDir;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getOutDir() {
        return outDir;
    }

    public String getOutFile() {
        return outFile;
    }

    public String getOutTrainerDir() {
        return outTrainerDir;
    }

    public String getOutMarker() {
        return outMarker;
    }

    public String getTrainerMarkerFile() {
        return trainerMarkerFile;
    }
}
