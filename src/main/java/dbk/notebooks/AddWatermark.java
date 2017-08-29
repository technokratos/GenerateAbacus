package dbk.notebooks;

import java.io.IOException;

/**
 * Created by denis on 23.05.17.
 */
public class AddWatermark {

    public static void main(String[] args) throws IOException, InterruptedException {
        //sudo soffice --headless --convert-to pdf *.xls

        //sudo soffice --convert-to pdf abacus_formula_even.3.xls
        //soffice --print-to-file abacus_formula_even.3.xls //to ps file
        //ps2pdf any.ps

        //pdftk L2_1.pdf background watermarkp.pdf output L2_1.wm.pdf
        //pdftk L2_2.pdf background watermarkp.pdf output L2_2.wm.pdf
        //pdftk L3_1.pdf background watermarkp.pdf output L3_1.wm.pdf
        //pdftk L3_2.pdf background watermarkp.pdf output L3_2.wm.pdf

        //pdftk abacus_formula_even.3.pdf background watermarkL.pdf output abacus_formula_even.3.wm.pdf


        //pdftk ./exercises/level3/abacus_formula_even.3.pdf background watermarker.pdf output abacus_formula_even.3.wm.pdf

//        final Process process = Runtime.getRuntime()
//                .exec(String.format("pdftk %s.pdf background watermarkerp.pdf output %s.wm.pdf", outFile, outFile));
//        process.waitFor();

    }
}
