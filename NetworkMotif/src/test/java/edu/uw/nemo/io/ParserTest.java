package edu.uw.nemo.io;

import edu.uw.nemo.model.Mapping;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    @Test
    public void assertValidParse() throws IOException, URISyntaxException {
        Parser target = new Parser();
       //Mapping actual = target.parser("full_scere_20140427.csv");
       Mapping actual = target.parser("Scere2014Jan_index.csv");
        assertNotNull(actual);
        //assertEquals(5132, actual.getNodeCount());
        //assertEquals(22735, actual.getLinkCount());
        assertEquals(3722, actual.getNodeCount());
        assertEquals(5099, actual.getLinkCount());
    }

}