package com.meilishuo.android.performance;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by victor on 14-9-5.
 */
public class MemInfo
{
    private static final Logger logger = LoggerFactory.getLogger(MemInfo.class);
    private static final String JSCHARTPATH = "target/android-info/mem/html/";
    private static final String MEMFILEPATH = "target/android-info/mem/dat/mem.dat";

    TreeMap<String, List<String>> memMap = new TreeMap<String, List<String>>();

    /**
     * 解析mem.dat存储到map中
     */
    public void parseMemFile()
    {
        File file = new File(MEMFILEPATH);
        if(null==file) return;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            //去掉首行
            bufferedReader.readLine();
            while((line=bufferedReader.readLine())!=null)
            {
                String[] array = line.trim().split(" "); //空格分隔
                if(array.length!=2) continue;
                if(memMap.size()==0 || !memMap.containsKey(array[1]))
                {
                    List<String> memList = new ArrayList<String>();
                    memList.add(array[0]);
                    memMap.put(array[1], memList);
                }
                else
                {
                    memMap.get(array[1]).add(array[0]);
                }
            }
            System.out.println(memMap.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将map中的数据写到到xml中
     */
    public void writeXmlFromMap()
    {
        if(null==memMap || memMap.size()==0)
            return;
        Iterator<String> it = memMap.keySet().iterator();
        while(it.hasNext())
        {
            String key = it.next();
            List<String> value = memMap.get(key);
            if(null==value || value.size()<=1)
                continue;
            //写入xml
            File file = new File(JSCHARTPATH+key.replace("/", "_").replace(":", "_")+"_mem.xml");
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                bufferedWriter.write(
                        "<?xml version=\"1.0\"?>\n" +
                        "<JSChart>\n" +
                        "\t<dataset type=\"line\">");
                int pos = 1;
                for(String index : value)
                {
                    bufferedWriter.write("<data unit=\""+pos+"\" value=\""+index+"\"/>\n");
                    pos++;
                }
                bufferedWriter.write(
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
                        "</JSChart>");
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成jschart html文件
     */
    public void writeJsHtml()
    {
        File file = new File(JSCHARTPATH);
        File[] xmlFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("_mem.xml");
            }
        });
        if(null== xmlFiles || xmlFiles.length==0) return;
        for(File index : xmlFiles)
        {
            File htmlFile = new File(index.getAbsolutePath().replace(".xml", ".html"));
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

    public void writeAllJsHtml()
    {
        File[] files = new File(JSCHARTPATH).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("_mem.html");
            }
        });
        if(files.length==0) return;

        File allHtmlFile = new File(JSCHARTPATH+"all_mem.html");
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
                            "    <title>内存变化趋势</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h1> 内存变化趋势</h1>\n";
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
    public void generateMemHtmlInfo()
    {
        MemInfo memInfo = new MemInfo();
        memInfo.parseMemFile();
        memInfo.writeXmlFromMap();
        memInfo.writeJsHtml();
        memInfo.writeAllJsHtml();
    }

}
