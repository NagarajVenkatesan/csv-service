package com.example.demo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.CsvRow;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

@Service
public class CsvService {

    public List<CsvRow> read(InputStream is) throws Exception {

        List<CsvRow> list = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            String[] line;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                CsvRow row = new CsvRow();
                row.setCol1(line[0]);
                row.setCol2(line[1]);
                list.add(row);
            }
        }
        return list;
    }

    public ByteArrayInputStream write(List<CsvRow> rows) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        writer.writeNext(new String[]{"col1", "col2", "status"});

        for (CsvRow r : rows) {
            writer.writeNext(new String[]{
                    r.getCol1(),
                    r.getCol2(),
                    String.valueOf(r.getStatus())
            });
        }

        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}