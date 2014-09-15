package com.meilishuo.android.performance;

import java.io.*;

/**
 * Created by Xuemeng Wang on 14-9-12.
 */
public class LoadrunnerHtmlWrapper {

    private static final String resultTxtPath = "result.txt";

    public static void main(String[] args) {
        String htmlTemplate =
                "<html>\n" +
                        "<head>\n" +
                        "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                        " </head>" +
                        "<h1>性能测试结果--${result}</h1>\n" +
                        "\n" +
                        "<ul>\n" +
                        "    <li><h3>压测地址:${url}</h3></li>\n" +
                        "    <li><h3>并发线程：${threadCount}</h3></li>\n" +
                        "    <li><h3>运行时间区间：${period}</h3></li>\n" +
                        "    <li><h3>平均响应时间:${avgRespTime}</h3></li>\n" +
                        "    <li><h3>服务器吞吐率:${throughtout}</h3></li>\n" +
                        "    <li><h3>cpu占用率:${cpu}</h3></li>\n" +
                        "</ul>\n" +
                "</html>";

        File file = new File(resultTxtPath);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            String interfaceType = null;
            float avgRespTime = 0;
            float throughout = 0;
            String result = null;
            while((line=bufferedReader.readLine())!=null)
            {
                //压测地址
                if(line.startsWith("Url")) {
                    htmlTemplate = htmlTemplate.replace("${url}", line.substring(line.indexOf("http://")));
                    if(line.toLowerCase().contains("snake"))
                        interfaceType = "snake";
                    else if(line.toLowerCase().contains("virus"))
                        interfaceType = "virus";
                    else
                        interfaceType = "other";
                }
                else if(line.startsWith("Max_user"))
                    htmlTemplate = htmlTemplate.replace("${threadCount}", line.substring(line.indexOf(":")+1));
                else if(line.startsWith("Period"))
                    htmlTemplate = htmlTemplate.replace("${period}", line.substring(line.indexOf(":")+1));
                else if(line.startsWith("Avg_resp_time")) {
                    htmlTemplate = htmlTemplate.replace("${avgRespTime}", line.substring(line.indexOf(":") + 1));
                    avgRespTime = Float.parseFloat(line.substring(line.indexOf(":")+1).trim());
                }
                else if(line.startsWith("Avg_tps")) {
                    htmlTemplate = htmlTemplate.replace("${throughtout}", line.substring(line.indexOf(":") + 1));
                    throughout = Float.parseFloat(line.substring(line.indexOf(":")+1).trim());
                }
                else if(line.startsWith("Average_cpu_idle"))
                    htmlTemplate = htmlTemplate.replace("${cpu}", line.substring(line.indexOf(":")+1));
            }
            bufferedReader.close();

            if("snake".equals(interfaceType))
            {
                if(throughout>150 && avgRespTime<0.3)
                    result = "成功";
            }
            else if("virus".equals(interfaceType))
            {
                if(throughout>200 && avgRespTime<0.2)
                    result = "成功";
            }
            else
            {
                result = "成功";
            }

            if(null==result) result = "失败";

            htmlTemplate = htmlTemplate.replace("${result}", result);
            writeToHtml("result.html", htmlTemplate);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToHtml(String fileName, String htmlContent)
    {
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(htmlContent);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
