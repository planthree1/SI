package cartaocidadao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;

import org.apache.commons.io.IOUtils;

public class MergeFiles {

    public static void merge(File assinatura, File certChavePublica, File document) throws IOException {
        IOCopier.joinFiles(new File("/home/borys/NetBeansProjects/CartaoCidadao/extra/test"), new File[]{
            assinatura, certChavePublica, document });
    }

    public static void separate() throws Exception {
        byte[] delimiter = "--End--".getBytes(Charset.forName("UTF-8"));
        FileInputStream fis = new FileInputStream("/home/borys/NetBeansProjects/CartaoCidadao/extra/test");
        byte[] array = new byte[fis.available()];
        fis.read(array);
        IOCopier.tokens(array, delimiter);

    }
}

class IOCopier {

    public static void joinFiles(File destination, File[] sources)
            throws IOException {
        OutputStream output = null;
        try {
            output = createAppendableStream(destination);
            for (File source : sources) {
                appendFile(output, source);
            }
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private static BufferedOutputStream createAppendableStream(File destination)
            throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    private static void appendFile(OutputStream output, File source)
            throws IOException {
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            IOUtils.copy(input, output);
            IOUtils.copy(new ByteArrayInputStream("--End--".getBytes(Charset.forName("UTF-8"))), output);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static void tokens(byte[] array, byte[] delimiter) throws IOException {
        List<byte[]> byteArrays = new LinkedList<>();
        String[] name = {"assinatura", "certChavePublica", "document"};
        int begin = 0, count = 0;
        outer:
        for (int i = 0; i < array.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (array[i + j] != delimiter[j]) {
                    continue outer;
                }
            }
            byteArrays.add(Arrays.copyOfRange(array, begin, i));
            FileUtils.writeByteArrayToFile(new File("/home/borys/NetBeansProjects/CartaoCidadao/extra/result/" + name[count]), byteArrays.get(count));
            begin = i + delimiter.length;
            count++;
        }
    }
}
