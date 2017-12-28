package dbk.generator.types;

import dbk.adapter.Sheet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ClassicBlock implements Block {
    List<Series> series;

    public ClassicBlock(List<Series> series) {
        this.series = series;
    }

    @Override
    public void writeBlock(Sheet sheet) {
        throw new NotImplementedException();
    }
}
