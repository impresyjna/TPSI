/**
 * Created by impresyjna on 15.04.2016.
 */
public class CVSLine {
    public String URL;
    public String req = "1";
    public String sendBytes;
    public String receiveBytes;

    public CVSLine(){

    }

    public CVSLine(String URL, String req, String sendBytes, String receiveBytes) {
        this.URL = URL;
        this.req = req;
        this.sendBytes = sendBytes;
        this.receiveBytes = receiveBytes;
    }
}
