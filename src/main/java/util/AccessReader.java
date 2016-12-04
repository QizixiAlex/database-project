package util;

import com.healthmarketscience.jackcess.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AccessReader {
    String filePath;
    public AccessReader(String filePath) {
        this.filePath = filePath;
    }

    public List<List<String>> getRows(String tableName) {
        List<List<String >> result = new ArrayList<>();
        File fs = new File(this.filePath);
        try {
            Database database = DatabaseBuilder.open(fs);
            Table table = database.getTable(tableName);
            table.forEach(row -> {
                List<String> currentRowList = new ArrayList<>();
                row.entrySet().forEach(entry -> {
                    String entryVal = String.valueOf(entry.getValue());
                    if (entryVal.length() == 0) {
                        entryVal = "null";
                    }
                    currentRowList.add(entryVal);
                });
                result.add(currentRowList);
                });
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
