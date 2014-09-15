import java.io.*;

/**
 * Created by Xuemeng Wang on 14-9-15.
 */
public class GetNetInfo {
    public static void main(String[] args) {

        String uidString = execCmd("adb shell ls /proc/uid_stat");
        String[] uidArray = uidString.split("\n");
        int length = uidArray.length-1;
        for(int i=0;i<=length-1;i++)
        {
            String contentRcv = execCmd("adb shell cat /proc/uid_stat/"+uidArray[i]+"/tcp_rcv");
            String contentSnd = execCmd("adb shell cat /proc/uid_stat/"+uidArray[i]+"/tcp_snd");
            try {
                System.out.println(contentRcv);
                System.out.println(contentSnd);
                FileWriter fileWriter1 = new FileWriter(new File(uidArray[i]+"_recv.dat"), true);
                FileWriter fileWriter2 = new FileWriter(new File(uidArray[i]+"_snd.dat"), true);
                fileWriter1.write(contentRcv);
                fileWriter2.write(contentSnd);
                fileWriter1.close();
                fileWriter2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String execCmd(String command)
    {
        BufferedReader br = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec(command);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                if("".equals(line.trim())) continue;

                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();
    }
}