package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Joris Schellekens on 5/2/2017.
 */
@Category(UnitTest.class)
public class PdfAttachedFileTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfAttachedFileTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfAttachedFileTest/";

    @Test
    public void extractSingleAttachmentFromPdf()
    {
        String filename = sourceFolder + "FileWithSingleAttachment.pdf";
        String attachmentFilename = sourceFolder + "FileWithSingleAttachment_001.jpg";

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(filename));

            // create
            PdfAttachedFile rootFile = new PdfAttachedFile(pdfDocument);

            // assert number of attachments
            assert(rootFile.listFiles().length == 1);

            // check content of attachments
            PdfAttachedFile attachment = (PdfAttachedFile) rootFile.listFiles()[0];
            byte[] bytes0 = attachment.getBytes();
            byte[] bytes1 = Files.readAllBytes(new File(attachmentFilename).toPath());

            assert(bytes0.length == bytes1.length);
            for(int i=0;i<bytes0.length;i++)
                assert(bytes0[i] == bytes1[i]);

        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }

    @Test
    public void extractMultipleAttachmentFromPdf()
    {
        String filename = sourceFolder + "FileWithMultipleAttachments.pdf";
        String[] attachmentFilename = { sourceFolder + "FileWithMultipleAttachments_001.jpg",
                                        sourceFolder + "FileWithMultipleAttachments_002.jpg",
                                        sourceFolder + "FileWithMultipleAttachments_003.jpg",
                                        sourceFolder + "FileWithMultipleAttachments_004.jpg"
                                            };

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(filename));

            // create
            PdfAttachedFile rootFile = new PdfAttachedFile(pdfDocument);

            // assert number of attachments
            assert(rootFile.listFiles().length == attachmentFilename.length);


            File[] attachments = rootFile.listFiles();
            Arrays.sort(attachments, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            // check content of attachments
            for(int i=0;i<attachments.length;i++) {

                PdfAttachedFile attachment = (PdfAttachedFile) attachments[i];
                byte[] bytes0 = attachment.getBytes();
                byte[] bytes1 = Files.readAllBytes(new File(attachmentFilename[i]).toPath());

                // compare bytes
                assert (bytes0.length == bytes1.length);
                for (int j = 0; j < bytes0.length; j++)
                    assert (bytes0[j] == bytes1[j]);

            }
        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }


}
