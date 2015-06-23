package edu.uw.nemo.proto.converter;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;

import static junit.framework.Assert.assertEquals;

/**
 * Created by joglekaa on 4/15/14.
 */
public class DIPConverterTest {

    @Test
    public void assertConverterHandlesMultipleLinksCorrectly() throws IOException, URISyntaxException {
        DIPConverter target = new DIPConverter();

        int count = target.convert("Scere20140427.txt","InputGraph8.csv");

       // int count = target.convert("InputGraph1.csv", "InputGraph1.txt");
        //assertEquals(22735, count);
    }

}
