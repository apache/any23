package org.deri.any23.mime.purifier;

import org.deri.any23.mime.purifier.Purifier;
import org.deri.any23.mime.purifier.WhiteSpacesPurifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Reference test case for {@link org.deri.any23.mime.purifier.WhiteSpacesPurifier}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class WhiteSpacesPurifierTest {

    private Purifier purifier;

    @Before
    public void setUp() {
        this.purifier = new WhiteSpacesPurifier();
    }

    @After
    public void tearDown() {
        this.purifier = null;
    }

    @Test
    public void testPurification() throws IOException {
        InputStream inputStream =
                getInputStream(new File("src/test/resources/application/xhtml/blank-file-header.xhtml"));
        this.purifier.purify(inputStream);
        Assert.assertNotNull(inputStream);
        Assert.assertTrue(
                validatePurification(
                       readInputStreamAsString(inputStream)
                )
        );
        
    }

    /**
     * @param file the file to be load.
     * @return the input stream containing the file.
     * @throws java.io.IOException
     */
    private InputStream getInputStream(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) != -1) {
            bos.write(buffer);
        }
        fis.close();
        InputStream bais;
        bais = new ByteArrayInputStream(bos.toByteArray());
        return bais;
	}

    /**
     * Reads an {@link java.io.InputStream} as a {@link String}.
     * 
     * @param in
     * @return
     * @throws IOException
     */
    private String readInputStreamAsString(InputStream in)
            throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    /**
     * Checks if a {@link String} starts with a propert character.
     *  
     * @param string
     * @return
     */
    private boolean validatePurification(String string) {
        char firstChar = string.charAt(0);
        return (firstChar != '\t') && (firstChar != '\n') && (firstChar != ' ');
    }

}
