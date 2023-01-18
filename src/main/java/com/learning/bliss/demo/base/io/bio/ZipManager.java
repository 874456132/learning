package com.learning.bliss.demo.base.io.bio;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;


/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/6/7 18:21
 * @Version 1.0
 */
public class ZipManager {

    /**
     * zip压缩功能测试. 将d://temp//zipout目录下的所有文件连同子目录压缩到d://temp//out.zip.
     *
     * @param sourceDir     所要压缩的目录名（包含绝对路径）
     * @param targetZipFile 压缩后的文件名
     * @throws Exception
     */
    public void createZip(String sourceDir, String targetZipFile){
        File folderObject = new File(sourceDir);
        if (folderObject.exists()) {
            List fileList = getSubFiles(folderObject);
            //压缩文件名
            ZipOutputStream zos = null;
            InputStream is = null;
            try {
                zos = new ZipOutputStream(new FileOutputStream(targetZipFile));
                byte[] buf = new byte[1024];
                int readLen = 0;
                for (int i = 0; i < fileList.size(); i++) {
                    File f = (File) fileList.get(i);
                    System.out.println("Adding: " + f.getPath() + f.getName());
                    //创建一个ZipEntry，并设置Name和其它的一些属性
                    ZipEntry ze = new ZipEntry(getAbsFileName(sourceDir, f));
                    ze.setSize(f.length());
                    ze.setTime(f.lastModified());
                    //将ZipEntry加到zos中，再写入实际的文件内容
                    zos.putNextEntry(ze);
                    is = new BufferedInputStream(new FileInputStream(f));
                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        zos.write(buf, 0, readLen);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (zos != null) {
                    try {
                        zos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new RuntimeException("this folder isnot exist!");
        }
    }



    /**
     * 测试解压缩功能. 将d://download//test.zip连同子目录解压到d://temp//zipout目录下.
     *
     * @throws Exception
     */
    public void releaseZipToFile(String sourceZip, String outFileName) throws IOException {
        /*ZipFile zfile = new ZipFile(sourceZip);
        System.out.println(zfile.getName());
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            //从ZipFile中得到一个ZipEntry
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                continue;
            }
            //以ZipEntry为参数得到一个InputStream，并写到OutputStream中
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(outFileName, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
            System.out.println("Extracted: " + ze.getName());
        }
        zfile.close();*/
        ZipFile zipFile = new ZipFile(sourceZip);
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceZip)));
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ZipEntry zipEntry = null;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            outputStream = new BufferedOutputStream(new FileOutputStream(getRealFileName(outFileName, zipEntry.getName())));;
            inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            byte[] b = new byte[1024];
            while (inputStream.read(b) != -1){
                outputStream.write(b);
            }
        }
        inputStream.close();
        outputStream.close();
        zipInputStream.close();
        zipFile.close();
    }


    /**
     * 取得指定目录下的所有文件列表，包括子目录.
     *
     * @param baseDir File 指定的目录
     * @return 包含java.io.File的List
     */
    private List<File> getSubFiles(File baseDir) {
        List<File> ret = new ArrayList();
        File[] tmp = baseDir.listFiles();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].isFile()) {
                ret.add(tmp[i]);
            }
            if (tmp[i].isDirectory()) {
                ret.addAll(getSubFiles(tmp[i]));
            }
        }
        return ret;
    }


    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    private File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("\\\\");
        File ret = new File(baseDir);
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, dirs[dirs.length - 1]);
        return ret;
    }


    /**
     * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.
     *
     * @param baseDir      java.lang.String 根目录
     * @param realFile     java.io.File 实际的文件名
     * @return 相对文件名
     */
    private String getAbsFileName(String baseDir, File realFile) {
        File real = realFile;
        File base = new File(baseDir);
        String ret = real.getName();
        while (true) {
            real = real.getParentFile();
            if (real == null)
                break;
            if (real.equals(base))
                break;
            else {
                ret = real.getName() + File.separator + ret;
            }
        }
        return ret;
    }
}