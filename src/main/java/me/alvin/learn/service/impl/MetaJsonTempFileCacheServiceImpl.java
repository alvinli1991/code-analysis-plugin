package me.alvin.learn.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import me.alvin.learn.service.MetaJsonTempFileCacheService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: Li Xiang
 * Date: 2021/12/30
 * Time: 4:53 PM
 */
public class MetaJsonTempFileCacheServiceImpl implements MetaJsonTempFileCacheService {
    private static final Logger LOGGER = Logger.getInstance(MetaJsonTempFileCacheServiceImpl.class);


    private final Cache<String, String> className2MetaTempFileAbsPathCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean isClassMetaJsonTempFileExist(PsiClass clz) {
        return getClassMetaJsonTempFile(clz) != null;
    }

    @Override
    public VirtualFile getClassMetaJsonTempFile(PsiClass clz) {
        if (Objects.isNull(clz) || StringUtils.isBlank(clz.getQualifiedName())) {
            return null;
        }
        String filePath = className2MetaTempFileAbsPathCache.getIfPresent(clz.getQualifiedName());
        if (null == filePath) {
            return null;
        }
        return VirtualFileManager.getInstance().refreshAndFindFileByNioPath(FileSystems.getDefault().getPath(filePath));
    }

    @Override
    public void put(PsiClass clz, File tempFile) {
        if (Objects.isNull(clz) || Objects.isNull(tempFile)) {
            return;
        }
        className2MetaTempFileAbsPathCache.put(clz.getQualifiedName(), tempFile.getAbsolutePath());
    }

    @Override
    @Nullable
    public File getTempFile() {
        try {
            return FileUtil.createTempFile("action-meta", ".json", true);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public boolean writeToFile(File tempFile, String content) {
        try {
            FileUtil.writeToFile(tempFile, content);
            return true;
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return false;
    }
}
