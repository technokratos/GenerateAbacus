package dbk.trainer;

import dbk.abacus.Count;
import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import org.jopendocument.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Stream;

/**
 * Created by denis on 09.02.17.
 */
public class TrainerWriter {
    private final List<Tuple2<Level, List<List<List<Integer>>>>> result;
    private final String mainDirName;

    public TrainerWriter(List<Tuple2<Level, List<List<List<Integer>>>>> data, String mainDirName) {
        this.result = data;
        this.mainDirName = mainDirName;
    }

    public void write() throws IOException {
        makeDir(mainDirName);

        for (Tuple2<Level, List<List<List<Integer>>>> levelData: result){
            Level level = levelData.getA();
            String levelDir = mainDirName + "/" + level.getTitle();
            makeDir(levelDir);
            copyTrainerTo(levelDir);
            List<List<List<Integer>>> mainList = levelData.getB();

            int i = 0;
            for (List<List<Integer>> nestedList: mainList){
                for (List<Integer> steps: nestedList) {
                    String fileName = levelDir + "/a" + i+ ".txt";
                    FileWriter fileWriter = new FileWriter(fileName);
                    steps.forEach(s -> {
                        try {
                            fileWriter.write(s.toString());
                            fileWriter.write("\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    fileWriter.write("\n");
                    fileWriter.write("9999999");
                    fileWriter.flush();
                    fileWriter.close();
                    i++;

                }

            }


        }
    }

    private void copyTrainerTo(String levelDir) throws IOException {
        FileUtils.copyDirectory(new File("Trainer"), new File(levelDir));
    }

    private void makeDir(String header) {
        File mainDir = new File(header);
        if (!mainDir.exists()) {
            mainDir.mkdir();
            Stream.of(mainDir.listFiles()).forEach(File::delete);
        }
    }
}
