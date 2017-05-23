package dbk.notebooks;

import java.io.IOException;

import static dbk.notebooks.MainFormulaApp.outfile;

/**
 * Created by denis on 23.05.17.
 */
public class AddWatermark {

    public static void main(String[] args) throws IOException {
        //pdftk ./exercises/level3/abacus_formula_even.3.pdf background watermarker.pdf output abacus_formula_even.3.wm.pdf

        Runtime.getRuntime().exec(String.format("pdftk %s.pdf background watermarker.pdf output %s.wm.pdf", outfile, outfile));
    }
}
