package com.masyaman.datapack.compare.objects;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.IOException;
import java.util.List;

public class CsvResourceParser {

    public static <T> List<T> parseCsv(Class<T> clazz, String resourceName) throws IOException {
        BeanListProcessor<T> rowProcessor = new BeanListProcessor<>(clazz);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(CsvParser.class.getResourceAsStream(resourceName));

        return rowProcessor.getBeans();
    }

}
