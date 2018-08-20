package com.xdja.anymore.library.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by anymore on 2018/8/19.
 */
public class FileUtil {

    public static File mergePcmFiles(List<File> pcmFiles, String dstFileName){
        if (pcmFiles == null || pcmFiles.size() == 0){
            return null;
        }
        File dstPcmFile = new File(dstFileName);
        FileOutputStream fos = null;
        FileInputStream fis;
        try {
            if (dstPcmFile.exists()){
                dstPcmFile.delete();
            }
            dstPcmFile.createNewFile();
            fos = new FileOutputStream(dstPcmFile);
            byte[] buff = new byte[1024];
            for (File f : pcmFiles) {
                fis = new FileInputStream(f);
                int len;
                while ((len = fis.read(buff,0,buff.length)) > 0){
                    fos.write(buff,0,len);
                    fos.flush();
                }
                fis.close();
//                f.delete();//删除临时录音片段
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dstPcmFile;
    }
}
