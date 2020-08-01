import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import org.cryptacular.bean.CipherBean;

public class ExecutionDecode{
    public static String byte2HexString(byte[] bytes) {
        String hex = "";
        if (bytes != null) {
            for (Byte b : bytes) {
                hex += String.format("%02X", b.intValue() & 0xFF);
            }
        }
        return hex;
    }

    public static void saveFile(String filename,byte [] data)throws Exception{
        if(data != null){
            String filepath = filename;
            File file  = new File(filepath);
            if(file.exists()){
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        }
    }

    public static byte[] unGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            if (args[0].toLowerCase().equals("-t")){
                try {
                    String execution = new String(args[1]);
                    // 将execution解码为byte数组
                    byte[] bytepayload = Base64.getDecoder().decode(execution);
                    // 获取字节码对象
                    Class class1 = Class.forName("org.jasig.spring.webflow.plugin.EncryptedTranscoder");
                    // 创建对象
                    Object test = class1.newInstance();
                    // 反射获取属性
                    Field field = class1.getDeclaredField("cipherBean");
                    // 设置反射时取消Java的访问检查
                    field.setAccessible(true);
                    // 返回指定对象 test 上此 Field 表示的字段的值
                    CipherBean cipherb = (CipherBean) field.get(test);
                    // 执行解密操作
                    byte[] result = cipherb.decrypt(bytepayload);
                    // 解压缩
                    byte[] unGZippaylaod = unGZip(result);
                    // 写文件
//                    saveFile("test.bin", unGZippaylaod);
                    // 需要转化为十六进制
                    String resulthex = byte2HexString(unGZippaylaod);
                    String payload = new String(unGZippaylaod);
    //                System.out.println(payload);
                    System.out.println(resulthex);
                }catch (Exception e){
                e.printStackTrace();
                }
            }
            else if (args[0].toLowerCase().equals("-f")) {
                File f = new File(args[1]);
                if (f.exists()) {
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(f));
                        String str;
                        while ((str = in.readLine()) != null) {
                            System.out.println(str);
                        }
                        System.out.println(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    throw new RuntimeException(args[1] + " File Not Found...");
                }
            }
            else if (args[0].toLowerCase().equals("-b")){
                File f = new File(args[1]);
                if (f.exists()) {
                    try {
                        byte[] fileContents = new byte[(int) f.length()];
                        FileInputStream fis = new FileInputStream(f);
                        fis.read(fileContents);
                        fis.close();
                        String resulthex = byte2HexString(fileContents);
                        System.out.println(resulthex);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    throw new RuntimeException(args[1] + " File Not Found...");
                }

            }
        }
        else {
            System.out.println("Usage:");
            System.out.println("\tExecutionDecode -t <execution-value>       解密execution");
            System.out.println("\tExecutionDecode -f <execution-value-file>  读取文件内容");
            System.out.println("\tExecutionDecode -b <execution-value-byte>  将byte类型转为十六进制");
        }
    }
}