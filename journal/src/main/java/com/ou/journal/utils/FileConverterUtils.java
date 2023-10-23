package com.ou.journal.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

public class FileConverterUtils {
    public static byte[] generateHTMLFromPDF(byte[] pdfBytes) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
            PDDocument pdf = PDDocument.load(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer output = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
            new PDFDomTree().writeText(pdf, output);
            output.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] convertToPDF(byte[] docBytes) throws IOException {
        try (ByteArrayInputStream docxInputStream = new ByteArrayInputStream(docBytes);
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {

            IConverter converter = LocalConverter.builder().build();

            converter.convert(docxInputStream).as(DocumentType.MS_WORD)
                    .to(pdfOutputStream).as(DocumentType.PDF)
                    .execute();

            converter.shutDown();

            return pdfOutputStream.toByteArray();
        }
    }
}
