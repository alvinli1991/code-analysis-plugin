package me.alvin.learn.service;


import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

import java.io.File;


/**
 * @author: Li Xiang
 * Date: 2021/12/30
 * Time: 4:47 PM
 */
public interface MetaJsonTempFileCacheService {

    /**
     *
     * @param clz
     * @return
     */
    boolean isClassMetaJsonTempFileExist(PsiClass clz);

    /**
     *
     * @param clz
     * @return
     */
    @Nullable
    VirtualFile getClassMetaJsonTempFile(PsiClass clz);

    /**
     *
     * @param clz
     * @param tempFile
     */
    void put(PsiClass clz, File tempFile);

    /**
     *
     * @return
     */
    File getTempFile();

    /**
     *
     * @param tempFile
     * @param content
     * @return
     */
    boolean writeToFile(File tempFile,String content);

}
