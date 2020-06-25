package com.cilys.lottery.web;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by admin on 2020/6/14.
 * java model生成器
 */
public class JavaModelTemplate {

    public static void main(String[] args) throws Exception{
        writeFile("Model", "t_pay");
    }

    private static void writeFile(String javaClassName, String tableName) throws Exception{
        String dir = System.getProperty("user.dir");
        System.out.println("dir = " + dir);
        String packageName = JavaModelTemplate.class.getPackage().getName();
        String packagePath = packageName.replace(".", File.separator);
        String filePath = dir + File.separator
                + "src" + File.separator
                + "main" + File.separator
                + "java" + File.separator
                + packagePath + File.separator
                + "model" + File.separator
                + javaClassName + ".java";

        File f = new File(javaClassName + ".java");

        if (f.exists()){
            System.err.println("The file " + javaClassName + "[" + f.getPath() +"]" + " exist..");
            return;
        }

        StringBuilder su = new StringBuilder();
        su.append("package " + packageName + ".model;");
        su.append("\n");

        addImport(su, new String[]{
                "import java.util.List;"
        });

        su.append("public class " + javaClassName + " extends BaseModel<" + javaClassName + "> {");
            su.append("\n");

            su.append("    private static " + javaClassName + " dao = new " + javaClassName + "();");
            su.append("\n");
            su.append("\n");

        addInsertMethod(javaClassName, su);
        addQueryAllMethod(javaClassName, tableName, su);
        addDelByIdMethod(su);
        addUpdateMethod(javaClassName, tableName, su);
        addQueryByIdMethod(javaClassName, tableName, su);

        su.append("\n");
        su.append("}");

        System.out.println(f.getName());
        FileWriter fw = new FileWriter(f.getName());
        fw.write(su.toString());
        fw.close();
    }

    private static void addInsertMethod(String javaClassName, StringBuilder su){
        su.append("\n");

        su.append("    public static boolean insert(" + javaClassName + " b) {");
        su.append("\n");
        su.append("        if (b == null) {");
        su.append("\n");
        su.append("            return false;");
        su.append("\n");
        su.append("        }");

        su.append("\n");
        su.append("\n");

        su.append("        return b.save();");
        su.append("\n");
        su.append("    }");
        su.append("\n");
    }

    private static void addQueryAllMethod(String javaClassName, String tableName,
                                          StringBuilder su){
        su.append("\n");
        su.append("    public static List<" + javaClassName + "> queryAll() {");
        su.append("\n");


        su.append("        return dao.find(\"select * from " + tableName + "\");");
        su.append("\n");
        su.append("    }");
        su.append("\n");
    }

    private static void addDelByIdMethod(
                                     StringBuilder su){
        su.append("\n");

        su.append("    public static boolean delById(Object id) {");
        su.append("\n");
        su.append("        return dao.deleteById(id);");
        su.append("\n");
        su.append("    }");
        su.append("\n");
    }

    private static void addUpdateMethod(String javaClassName, String tableName,
                                        StringBuilder su){
        su.append("\n");
        su.append("    public static boolean updateInfo(" + javaClassName + " b) {" );
        su.append("\n");
        su.append("        if (b == null) {");
        su.append("\n");
        su.append("            return false;");
        su.append("\n");
        su.append("        }");
        su.append("\n");
        su.append("        return b.update();");
        su.append("\n");
        su.append("    }");
        su.append("\n");
    }

    private static void addQueryByIdMethod(String javaClassName, String tableName,
                                           StringBuilder su){
        su.append("\n");
        su.append("    public static " + javaClassName + " queryById(Object id) {");
        su.append("\n");
        su.append("        return dao.findById(id);");
        su.append("\n");
        su.append("    }");
        su.append("\n");
    }

    private static void addImport(StringBuilder su, String[] importPackages) {
        su.append("\n");
        if (importPackages != null && importPackages.length > 0) {
            for (String importPackage : importPackages) {
                su.append("\n");
                su.append(importPackage);
                su.append("\n");
            }
        }
        su.append("\n");
    }

}