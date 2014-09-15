import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by victor on 14-9-9.
 */
public class NetInfo
{
    private static final Logger logger = LoggerFactory.getLogger(NetInfo.class);
    private static final String NETBASEDIR = "target/android-info/net/";
    private static final String PACKAGELISTFILENAME = NETBASEDIR+"packages.list";
    private TreeMap<String, List<String>> recvMap = new TreeMap<String, List<String>>();
    private TreeMap<String, List<String>> sndMap = new TreeMap<String, List<String>>();
    private TreeMap<String, String> nameUidMap = new TreeMap<String, String>();

    /**
     * 解析流量的dat文件,android 4.0版本往下
     * 第一列：接收
     * 第二列：发送
     */
    public void parseMemLt4()
    {
        String datPath = NETBASEDIR+"dat/";
        File file = new File(datPath);
        File[] datFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("_net.dat");
            }
        });
        for(File index : datFiles) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = null;
                String key = index.getName().replace("_net.dat", "");
                String packageName = nameUidMap.get(key);
                while ((line = bufferedReader.readLine()) != null) {
                    String[] array = line.trim().split(" "); //空格分隔
                    if (array.length != 2) continue;

                    //接收
                    if (recvMap.size() == 0 || !recvMap.containsKey(packageName)) {
                        List<String> recvList = new ArrayList<String>();
                        recvList.add(array[0]);
                        recvMap.put(packageName, recvList);
                    } else {
                        recvMap.get(packageName).add(array[0]);
                    }
                    //发送
                    if(sndMap.size()==0 || !sndMap.containsKey(packageName))
                    {
                        List<String> sndList = new ArrayList<String>();
                        sndList.add(array[1]);
                        recvMap.put(packageName, sndList);
                    }
                    else
                    {
                        recvMap.get(packageName).add(array[1]);
                    }

                }
                System.out.println(recvMap.size());
                System.out.println(sndMap.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 解析流量的dat文件,android 4.0版本往上
     * cat /proc/uid_stat/$uid/tcp_rcv(下行)
     * cat /proc/uid_stat/$uid/tcp_snd(上行)
     */
    public void parseMemGt4()
    {
        String datPath = NETBASEDIR+"dat/";
        File file = new File(datPath);
        File[] datFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("recv.dat") || pathname.getName().endsWith("snd.dat");
            }
        });
        for(File index : datFiles) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(index)));
                String key = index.getName().replace("_recv.dat", "").replace("_snd.dat", "");
                String packageName = nameUidMap.get(key);
                if(null==packageName) continue;
                String line = null;
                while((line=bufferedReader.readLine())!=null)
                {
                    if(index.getName().contains("recv")) {
                        //接收
                        if (recvMap.size() == 0 || !recvMap.containsKey(packageName)) {
                            List<String> recvList = new ArrayList<String>();
                            recvList.add(line);
                            recvMap.put(packageName, recvList);
                        } else {
                            recvMap.get(packageName).add(line);
                        }
                    }
                    else if(index.getName().contains("snd")) {
                        //发送
                        if (sndMap.size() == 0 || !sndMap.containsKey(packageName)) {
                            List<String> sndList = new ArrayList<String>();
                            sndList.add(line);
                            sndMap.put(packageName, sndList);
                        } else {
                            sndMap.get(packageName).add(line);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(recvMap.size());
        System.out.println(sndMap.size());


    }

    /**
     * 将map中的数据写到xml中
     */
    public void writeXmlFromMap()
    {
        String xmlPath = NETBASEDIR+"html/";
        if(null==recvMap || null==sndMap || recvMap.size()==0 || sndMap.size()==0)
            return;
        Iterator<String> itRecv = recvMap.keySet().iterator();
        Iterator<String> itSnd = sndMap.keySet().iterator();
        while(itRecv.hasNext() && itSnd.hasNext())
        {
            String keyRecv = itRecv.next();
            String keySnd = itSnd.next();
            List<String> valueRecv = recvMap.get(keyRecv);
            List<String> valueSnd = sndMap.get(keySnd);
            if(null==valueRecv || valueRecv.size()<=1 || null==valueSnd || valueSnd.size()<=1)
                continue;
            //写入xml
            File file = new File(xmlPath+keyRecv.replace("/", "_").replace(":", "_")+"_recv.xml");
            File fileSnd = new File(xmlPath+keySnd.replace("/", "_").replace(":", "_")+"_snd.xml");
            try {
                BufferedWriter bufferedWriterRecv = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                BufferedWriter bufferedWriterSnd = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileSnd, true)));
                String header =
                        "<?xml version=\"1.0\"?>\n" +
                                "<JSChart>\n" +
                                "\t<dataset type=\"line\">";
                String footer =
                        "</dataset>\n" +
                                "\t<optionset>\n" +
                                "\t\t<option set=\"setLineColor\" value=\"'#8D9386'\"/>\n" +
                                "\t\t<option set=\"setLineWidth\" value=\"4\"/>\n" +
                                "\t\t<option set=\"setTitleColor\" value=\"'#7D7D7D'\"/>\n" +
                                "\t\t<option set=\"setAxisColor\" value=\"'#9F0505'\"/>\n" +
                                "\t\t<option set=\"setGridColor\" value=\"'#a4a4a4'\"/>\n" +
                                "\t\t<option set=\"setAxisValuesColor\" value=\"'#333639'\"/>\n" +
                                "\t\t<option set=\"setAxisNameColor\" value=\"'#333639'\"/>\n" +
                                "\t\t<option set=\"setTextPaddingLeft\" value=\"0\"/>\n" +
                                "\t</optionset>\n " +
                                "</JSChart>";
                bufferedWriterRecv.write(header);
                bufferedWriterSnd.write(header);
                int pos = 1;
                for(String index : valueRecv)
                {
                    bufferedWriterRecv.write("<data unit=\""+pos+"\" value=\""+index+"\"/>\n");
                    pos++;
                }
                pos = 1;
                for(String index : valueSnd)
                {
                    bufferedWriterSnd.write("<data unit=\""+pos+"\" value=\""+index+"\"/>\n");
                    pos++;
                }
                bufferedWriterRecv.write(footer);
                bufferedWriterSnd.write(footer);

                bufferedWriterRecv.flush();
                bufferedWriterRecv.close();
                bufferedWriterSnd.flush();
                bufferedWriterSnd.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用xml数据，生成jschart html文件
     */
    /**
     * 生成jschart html文件
     */
    public void writeJsHtml()
    {
        String htmlPath = NETBASEDIR+"html/";
        File file = new File(htmlPath);
        File[] xmlFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xml");
            }
        });
        if(null== xmlFiles || xmlFiles.length==0) return;
        for(File index : xmlFiles)
        {
            File htmlFile = new File(htmlPath+index.getName().replace(".xml", ".html"));
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(htmlFile));
                bufferedWriter.write(
                        "<html>\n" +
                                "<head>\n" +
                                "\n" +
                                "<title>JSChart</title>\n" +
                                "\n" +
                                "<script type=\"text/javascript\" src=\"jscharts.js\"></script>\n" +
                                "\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "<div id=\"result\">\n" +
                                "<h3>"+htmlFile.getName().replace(".xml", "")+"</h3>" +
                                "<div id=\"graph\">Loading graph...</div>\n" +
                                "\n" +
                                "<script type=\"text/javascript\">\n" +
                                "\t\n" +
                                "\tvar myChart = new JSChart('graph', 'line');\n" +
                                "\tmyChart.setDataXML(\""+index.getName()+"\");\n" +
                                "\tmyChart.draw();\n" +
                                "\t\n" +
                                "</script>\n" +
                                "\n" +
                                "\n" +
                                "</div>\n" +
                                "</body>\n" +
                                "</html>\n"
                );
                bufferedWriter.flush();
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析packages.list文件，获取pid和uid的对应关系
     * 第一列：name
     * 第二列：uid
     */
    public void parsePackageList()
    {
        File file = new File(PACKAGELISTFILENAME);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line=bufferedReader.readLine())!=null)
            {
                String[] array = line.split(" ");
                nameUidMap.put(array[1], array[0]);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAllJsHtml()
    {
        String htmlPath = NETBASEDIR+"html/";
        File[] files = new File(htmlPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".html");
            }
        });
        if(files.length==0) return;

        File allHtmlFile = new File(htmlPath+"all_net.html");
        if(allHtmlFile.exists())
            allHtmlFile.delete();

        try {
            allHtmlFile.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(allHtmlFile, true));
            String header =
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head lang=\"en\">\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>网络流量</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h1> 网络流量</h1>\n";
            out.write(header);


            for(File index : files)
            {
                String fileName = index.getName();

                out.write("<iframe src=\""+fileName+"\" width=\"100%\" height=\"500\"></iframe><br>\n");

            }

            out.write("</body>\n</html>");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeHtml()
    {
        NetInfo netInfo = new NetInfo();
        netInfo.parsePackageList();
        netInfo.parseMemGt4();
        netInfo.parseMemLt4();
        netInfo.writeXmlFromMap();
        netInfo.writeJsHtml();
        netInfo.writeAllJsHtml();
    }


}
