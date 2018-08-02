
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.any23.rdf.librdfa.Callback;
import org.apache.any23.rdf.librdfa.RdfaParser;

public class main {

  public static void main(String argv[]) {
    System.loadLibrary("rdfaJava"); // Attempts to load example.dll (on Windows) or libexample.so (on Linux)

    System.out.println("Adding and calling a normal C++ callback");
    System.out.println("----------------------------------------");
    String ds = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n"
        + "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n"
        + "      xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n"
        + "<head>\n"
        + "	<title>Test 0001</title>\n"
        + "<script type=\"text/javascript\" src=\"https://ff.kis.v2.scr.kaspersky-labs.com/89CF2878-190C-6147-88B9-F9B46CAB8AD0/main.js\" charset=\"UTF-8\"></script></head>\n"
        + "<body>\n"
        + "	<p>This photo was taken by <span class=\"author\" about=\"photo1.jpg\" property=\"dc:creator\">Mark Birbeck</span>.</p>\n"
        + "</body>\n"
        + "</html>";

    RdfaParser caller = new RdfaParser("http://www.google.com/");
    caller.init();
    Callback callback = new JavaCallback(new ByteArrayInputStream(ds.getBytes(StandardCharsets.UTF_8)));
    caller.setCallback(callback);

    caller.parse();
    //rdfa.set_rdfa_parser(caller);

  }
}

class JavaCallback extends Callback {

  BufferedReader bis = null;
  int len = 0;

  public JavaCallback(InputStream is) {
    super();
    bis = new BufferedReader(new InputStreamReader(is));
  }

  @Override
  public void default_graph(String subject, String predicate, String object, int object_type, String datatype, String language) {
    System.out.println("default_graph(...)");
    System.out.println("S=" + subject + "P=" + predicate + "O=" + object + "OT=" + object_type + "DT=" + datatype + "LANG=" + language);
  }

  @Override
  public void processor_graph(String subject, String predicate, String object, int object_type, String datatype, String language) {
    System.out.println("processor_graph(...)");
    System.out.println("S=" + subject + "P=" + predicate + "O=" + object + "OT=" + object_type + datatype + "LANG=" + language);
  }

  @Override
  public String fill_data(long buffer_length) {
    System.out.println("buffer_length:" + buffer_length);
    StringBuilder sb = new StringBuilder(new StringBuffer((int) buffer_length));
    len = 0;
    try {
      for (int c; (c = bis.read()) != -1;) {
        sb.append((char) c);
        len++;
      }
    } catch (IOException ex) {
    }
    return sb.toString();
  }

  @Override
  public long fill_len() {
    return len;
  }
}
