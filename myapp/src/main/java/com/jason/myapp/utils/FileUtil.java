package com.jason.myapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private static final FileUtil fileUtil = new FileUtil();

    public static FileUtil getInstance() {

        return fileUtil;
    }

    public String getFileWithAbsolutePath(String absolutePath) {
        File file = new File(absolutePath);
        String result = "";
        BufferedReader br = null;
        FileInputStream fileInputStream = null;
        InputStreamReader isr = null;

        try {
            fileInputStream = new FileInputStream(file);
            isr = new InputStreamReader(fileInputStream);
            br = new BufferedReader(isr);
            String str = null;

            while ((str = br.readLine()) != null) {

                result += str;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    new FileInputStream(file).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @JavascriptInterface
    public synchronized String getFile(String path) {

        File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
        String result = "";
        BufferedReader br = null;
        FileInputStream fileInputStream = null;
        InputStreamReader isr = null;


        try {
            fileInputStream = new FileInputStream(file);
            isr = new InputStreamReader(fileInputStream);
            br = new BufferedReader(isr);
            String str = null;

            while ((str = br.readLine()) != null) {
                result += str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    new FileInputStream(file).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @JavascriptInterface
    public synchronized boolean existsFile(String path) {

        File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
        return file.exists();
    }

    @JavascriptInterface
    public synchronized void writeFile(String path, String param) {

        if (TextUtils.isEmpty(param)) {
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
        File folder = new File(file.getParent());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(param);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 追加方式写入文件
     *
     * @param path
     * @param param
     */
    public synchronized void writeFileAppend(String path, String param) {

        File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
        File folder = new File(file.getParent());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            fw.write(param);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 删除文件
     *
     * @param path
     */
    public synchronized void removeFile(String path) {

        File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
        if (file.exists()) {
            file.delete();
        }

    }

    public synchronized void writeObject(String path, Serializable obj) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        ObjectOutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(path);
            outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(obj);
        } catch (IOException e) {
            removeFile(path);
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                    new FileOutputStream(path).close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized Object readObject(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        Object obj = null;
        ObjectInputStream inputStream = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(path);
            inputStream = new ObjectInputStream(fileInputStream);
            obj = inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    new FileInputStream(path).close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return obj;
    }


    /**
     * 清空文件夹内的文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


    //删除文件夹
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        boolean flag;
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file;
        file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }


    /**
     * 移动文件到指定目录
     *
     * @param oldPath String  如：c:/fqf.txt
     * @param newPath String  如：d:/fqf.txt
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);

    }

    /**
     * 复制单个文件
     *
     * @param oldPath String  原文件路径  如：c:/fqf.txt
     * @param newPath String  复制后路径  如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
//           int  bytesum  =  0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);  //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
//               int  length;
                while ((byteread = inStream.read(buffer)) != -1) {
//                   bytesum  +=  byteread;  //字节数  文件大小
//                   System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }


    /**
     * 删除文件
     *
     * @param filePathAndName String  文件路径及名称  如c:/fqf.txt
     * @return boolean
     */
    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myDelFile = new File(filePath);
            myDelFile.delete();

        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();

        }

    }

    public List<String> readAllTxtFile(String path) throws Exception {
        List<String> readList = new ArrayList<String>();
        File file = new File(path);
        File[] files = file.listFiles();
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(files[i]));
                BufferedReader br = new BufferedReader(reader);
                String line = "";
                line = br.readLine();
                while (line != null) {
                    Log.d("file", "readAllTxtFile: " + files[i].getName() + ": " + line);
                    readList.add(line);
                    line = br.readLine();

                }
                br.close();
            }
        }
        return readList;
    }

    /**
     * 读取一个txt文件的内容
     *
     * @param file
     * @return
     */
    public String readStringTxtFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
                result.append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 获取文件数目
     *
     * @param path
     * @return
     */
    public int getFileSize(String path) {
        int fileCount = 0;
        File d = new File(path);
        File list[] = d.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                fileCount++;
            }
        }
        return fileCount;
    }

    /**
     * 同一盘下移动文件
     *
     * @param firmwarePath
     * @param target
     * @return
     */
    public synchronized boolean moveFileSameDisk(String firmwarePath, String target) {

        File sourceFile = new File(firmwarePath);
        boolean renameTo = sourceFile.renameTo(new File(target));
        return renameTo;
    }

    /**
     * 文件夹删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    /**
     * 安全删除单个文件.
     *
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    /**
     * 复制assets图片到sd卡
     *
     * @param path
     * @param imageName
     * @param context
     */
    public void sWithImage(String path, String imageName, Context context) {
        AssetManager asm = context.getAssets();
        InputStream is;
        try {
            InputStream ist = asm.open(imageName);
            Bitmap images = BitmapFactory.decodeStream(ist);

            FileOutputStream out = new FileOutputStream(path + imageName);
            images.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            ist.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
