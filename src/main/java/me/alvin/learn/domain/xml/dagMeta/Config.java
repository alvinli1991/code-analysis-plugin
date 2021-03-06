package me.alvin.learn.domain.xml.dagMeta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2022/4/22
 * Time: 10:42 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Config {
    private ThreadPool threadPool;

    public Config() {
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(getThreadPool(), config.getThreadPool());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getThreadPool());
    }


    public static Builder builder(){
        return Builder.getInstance();
    }

    public static final class Builder {
        private ThreadPool threadPool;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder threadPool(ThreadPool threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setThreadPool(threadPool);
            return config;
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "threadPool=" + threadPool +
                '}';
    }
}
