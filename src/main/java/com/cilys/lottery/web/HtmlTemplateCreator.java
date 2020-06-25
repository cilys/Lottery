package com.cilys.lottery.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by admin on 2020/6/14.
 * html页面生成器
 */
public class HtmlTemplateCreator {

    public static void main(String[] args) throws Exception{
//        writeFile("");
        readFile("template_list.html");
    }

    private static void readFile(String fileTemplate) throws Exception{
        FileReader fr = new FileReader(fileTemplate);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        StringBuilder su = new StringBuilder();
        if (line != null){
            su.append(line);
            su.append("\n");
        }
        while (line != null){
            line = br.readLine();

            if (line != null) {
                su.append(line);
                su.append("\n");
            }
        }
        String str = su.toString();
        str = str.replace("_headJsFile_", "js/scheme_buy_list.js");

        writeFile("scheme_buy_list", str);
    }

    private static void writeFile(String h5, String str) throws Exception{
        File f = new File(h5 + ".html");

        if (f.exists()){
            System.err.println("The file " + h5 + "[" + f.getPath() +"]" + " exist..");
            return;
        }

        StringBuilder su = new StringBuilder();

        su.append(str);


        FileWriter fw = new FileWriter(f.getName());
        fw.write(su.toString());
        fw.close();
    }




}